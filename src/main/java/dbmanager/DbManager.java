package dbmanager;

import main.User;
import com.arangodb.entity.BaseDocument;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DbManager {
    private final String DB_URL = "jdbc:mysql://localhost/DP";

    public Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, "admin", "admin");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    private void closeConnection(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public User login(String login, String pass) {
        Connection con = getConnection();
        String sql = "SELECT * FROM DP_USERS WHERE email = ? OR login = ? AND STATUS = 1";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, login);
            ps.setString(2, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("PASSWORD").equals(pass)) {
                    int user_id = rs.getInt("USER_ID");
                    String first_name = rs.getString("FIRST_NAME");
                    String last_name = rs.getString("LAST_NAME");
                    String email = rs.getString("EMAIL");
                    boolean teacher = rs.getBoolean("TEACHER");
                    return new User(user_id, first_name, last_name, email, login, teacher);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public String approveReg(String id) {
        Connection con = getConnection();
        String sql = "UPDATE DP_USERS SET status = 1 WHERE user_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public String setStatus(String id, int status) {
        Connection con = getConnection();
        String sql = "UPDATE DP_USERS SET status = ? WHERE user_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, Integer.parseInt(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public String dp_users_insert(String firstname, String lastname, String email, String login, String pass, boolean teacher) {
        Connection con = getConnection();
        String sql = "INSERT INTO DP_USERS (first_name, last_name, email, login, password, teacher, status)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, firstname);
            ps.setString(2, lastname);
            ps.setString(3, email);
            ps.setString(4, login);
            ps.setString(5, pass);
            ps.setBoolean(6, teacher);
            ps.setInt(7, 3);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public String dp_users_update(String firstname, String lastname, String pass, String login) {
        Connection con = getConnection();
        String sql = "UPDATE DP_USERS SET first_name = ?, last_name = ?, password = ? WHERE login = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, firstname);
            ps.setString(2, lastname);
            ps.setString(3, pass);
            ps.setString(4, login);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public String dp_exercise_files_dir_insert(int exerciseID, File file, String path) throws FileNotFoundException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                dp_exercise_files_dir_insert(exerciseID, f, path);
            }
        } else {
            dp_exercise_files_file_insert(exerciseID, new FileInputStream(file), path);
        }
        return "OK";
    }

    public String dp_exercise_files_file_insert(int exerciseID, InputStream file, String path) {
        Connection con = getConnection();
        String sql = "INSERT INTO dp_exercise_files (exercise_id, file, path) VALUES(?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, exerciseID);
            ps.setBlob(2, file);
            ps.setString(3, path);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public String dp_exercise_files_delete(int exerciseID) {
        Connection con = getConnection();
        String sql = "DELETE FROM dp_exercise_files WHERE exercise_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, exerciseID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public String dp_exercises_insert(int exerciseID, String text, boolean visible, InputStream config_file) {
        if (getExercisesCount() >= exerciseID) {
            return dp_exercises_update(exerciseID, text, visible, config_file);
        } else {
            Connection con = getConnection();
            String sql = "INSERT INTO dp_exercises (exercise_id, text, visible, config_file) VALUES(?, ?, ?, ?)";
            try {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, exerciseID);
                ps.setString(2, text);
                ps.setBoolean(3, visible);
                ps.setBlob(4, config_file);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return e.getMessage();
            } finally {
                closeConnection(con);
            }
            return "OK";
        }
    }

    public String dp_exercises_update(int exerciseID, String text, boolean visible, InputStream config_file) {
        Connection con = getConnection();
        String sql = "UPDATE dp_exercises SET text = ?, visible = ?, config_file = ? WHERE exercise_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, text);
            ps.setBoolean(2, visible);
            ps.setBlob(3, config_file);
            ps.setInt(4, exerciseID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public String dp_exercises_get_config_file(String exercise_id) {
        Connection con = getConnection();
        String ret = "";
        try {
            PreparedStatement statement = con.prepareStatement("SELECT CONFIG_FILE FROM dp_exercises WHERE exercise_id = ?");
            statement.setString(1, exercise_id);
            ResultSet rs = statement.executeQuery();
            rs.next();
            Blob conf_file = rs.getBlob(1);
            ret = new String(conf_file.getBytes(1l, (int)conf_file.length()));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return ret;
    }

    public int getExercisesCount() {
        Connection con = getConnection();
        int count = 0;
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(*) AS total FROM dp_exercises");
            rs.next();
            count = rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return count;
    }

    public List<String> getVisibleExercises() {
        Connection con = getConnection();
        List<String> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT exercise_id FROM dp_exercises WHERE visible = 1");
            while (rs.next()) {
                list.add(Integer.toString(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public String getExerciseDesc(String exerciseID) {
        Connection con = getConnection();
        String ret = "";
        try {
            PreparedStatement statement = con.prepareStatement("SELECT TEXT FROM dp_exercises WHERE exercise_id = ?");
            statement.setString(1, exerciseID);
            ResultSet rs = statement.executeQuery();
            rs.next();
            ret = rs.getString("TEXT");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return ret;
    }

    public boolean getExerciseVisibility(String exerciseID) {
        Connection con = getConnection();
        boolean ret = false;
        try {
            PreparedStatement statement = con.prepareStatement("SELECT VISIBLE FROM dp_exercises WHERE exercise_id = ?");
            statement.setString(1, exerciseID);
            ResultSet rs = statement.executeQuery();
            rs.next();
            ret = rs.getBoolean("VISIBLE");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return ret;
    }


    public String createExercise(int exerciseID, int user_id, int version) throws SQLException {
        Connection conn = getConnection();
        String mainDir = "";
        try {
            ArangoDBManager arangoManager = new ArangoDBManager();
            String sql;
            PreparedStatement ps;
            ResultSet resultSet;
            if (version > 0) {
//            has own files for exercise
                BaseDocument document = arangoManager.getDocument(String.format("%d-%d-%d", user_id, exerciseID, version));
                if (document == null || document.getProperties().size() == 0) {
//                do not have personal cache
                    sql = "SELECT file, path FROM dp_user_files WHERE exercise_id = ? AND user_id = ? AND version = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, exerciseID);
                    ps.setInt(2, user_id);
                    ps.setInt(3, version);
                    resultSet = ps.executeQuery();
                    BaseDocument newExerciseDocument = new BaseDocument();
                    newExerciseDocument.setKey(String.format("%d-%d-%d", user_id, exerciseID, version));
                    while (resultSet.next()) {
                        String path = resultSet.getString("path");
                        InputStream is = resultSet.getBinaryStream("file");
                        mainDir = createFile(path, is, mainDir);
                        java.sql.Blob ablob = resultSet.getBlob("file");
                        String str = new String(ablob.getBytes(1l, (int) ablob.length()));
                        newExerciseDocument.addAttribute(path, str);
                    }
                    arangoManager.insertDocument(newExerciseDocument);
                } else {
//                have personal cache
                    for (Map.Entry<String, Object> entry : document.getProperties().entrySet()) {
                        InputStream is = org.apache.commons.io.IOUtils.toInputStream((String) entry.getValue(), "UTF-8");
                        mainDir = createFile(entry.getKey(), is, mainDir);
                    }
                }
            } else {
//            do not have own files for exercise
                BaseDocument document = arangoManager.getDocument(String.format("%d", exerciseID));
                if (document == null) {
//                do not have cache for exercise
                    sql = "SELECT file, path FROM dp_exercise_files WHERE exercise_id = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, exerciseID);
                    resultSet = ps.executeQuery();
                    BaseDocument newExerciseDocument = new BaseDocument();
                    newExerciseDocument.setKey(String.valueOf(exerciseID));
                    while (resultSet.next()) {
                        String path = resultSet.getString("path");
                        InputStream is = resultSet.getBinaryStream("file");
                        mainDir = createFile(path, is, mainDir);
//                        dp_user_files_insert(user_id, exerciseID, is, path, 1);
                        java.sql.Blob ablob = resultSet.getBlob("file");
                        String str = new String(ablob.getBytes(1l, (int) ablob.length()));
                        newExerciseDocument.addAttribute(path, str);
                    }
                    arangoManager.insertDocument(newExerciseDocument);
                } else {
//                have cache for exercise
                    for (Map.Entry<String, Object> entry : document.getProperties().entrySet()) {
                        InputStream is = org.apache.commons.io.IOUtils.toInputStream((String) entry.getValue(), "UTF-8");
                        mainDir = createFile(entry.getKey(), is, mainDir);
                        dp_user_files_insert(user_id, exerciseID, is, entry.getKey(), 1);
                    }
                    ArangoDBManager arangoDB = new ArangoDBManager();
                    BaseDocument doc = arangoDB.getDocument(exerciseID);
                    doc.setKey(arangoDB.getKey(user_id, exerciseID, 1));
                    arangoDB.insertDocument(doc);
                }
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.close();
        return mainDir;
    }


    private String createFile(String path, InputStream is, String dir) {
        String mainDir = dir;
        try {

            if (mainDir.equals("")) {
                mainDir = "/" + (path.startsWith("/") ? path.split("/")[1] : path.split("/")[0]);
                new File(mainDir).mkdirs();
            }
            new File("/" + path.substring(0, path.lastIndexOf("/"))).mkdirs();
            File file = new File((path.startsWith("/")) ? path : "/" + path);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[1];
            while (is.read(buffer) > 0) {
                fos.write(buffer);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainDir;
    }

    private String createFile(String path, byte[] fileData, String dir) {
        String mainDir = dir;
        try {

            if (mainDir.equals("")) {
                mainDir = "/" + (path.startsWith("/") ? path.split("/")[1] : path.split("/")[0]);
                new File(mainDir).mkdirs();
            }
            new File("/" + path.substring(0, path.lastIndexOf("/"))).mkdirs();
            File file = new File((path.startsWith("/")) ? path : "/" + path);
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(fileData);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainDir;
    }

    public void insert(String code, String test) {
        Connection con = getConnection();
        try {
            PreparedStatement pstm = con.prepareStatement("INSERT INTO DP_RUN_FILES (code, test) VALUES(?, ?)");
            pstm.setString(1, code);
            pstm.setString(2, test);
            pstm.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
    }

    public HashMap<String, String> getUserData(String login) {
        Connection con = getConnection();
        HashMap<String, String> map = new HashMap<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM dp_users WHERE login = ?");
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            rs.next();
            map.put("user_id", Integer.toString(rs.getInt("USER_ID")));
            map.put("first_name", rs.getString("FIRST_NAME"));
            map.put("login", rs.getString("LOGIN"));
            map.put("last_name", rs.getString("LAST_NAME"));
            map.put("email", rs.getString("EMAIL"));
            map.put("status", Integer.toString(rs.getInt("STATUS")));
            map.put("teacher", Boolean.toString(rs.getBoolean("TEACHER")));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return map;
    }

    public ArrayList<HashMap<String, String>> getAllUsers() {
        Connection con = getConnection();
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT login FROM dp_users");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ret.add(getUserData(rs.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return ret;
    }


    public String dp_user_files_insert(int user_id, int exercise_id, InputStream file, String path, int version) {
        Connection con = getConnection();
        String sql = "INSERT INTO DP_USER_FILES (user_id, exercise_id, file, path, version) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setInt(2, exercise_id);
            ps.setBlob(3, file);
            ps.setString(4, path);
            ps.setInt(5, version);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public String dp_user_files_update(int user_id, int exercise_id, InputStream file, String path, int version) {
        path = (path.startsWith("/")) ? path.substring(1) : path;
        Connection con = getConnection();
        String sql = "UPDATE DP_USER_FILES SET file = ? WHERE path = ? AND user_id = ? AND exercise_id = ? AND version = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBlob(1, file);
            ps.setString(2, path);
            ps.setInt(3, user_id);
            ps.setInt(4, exercise_id);
            ps.setInt(5, version);
            System.out.println(ps.executeUpdate());
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }

    public int getUserLastVersion(int exerciseID, int user_id) {
        Connection con = getConnection();
        String sql = "SELECT max(version) FROM dp_user_files WHERE exercise_id = ? AND user_id = ?";
        PreparedStatement ps = null;
        int version = 0;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, exerciseID);
            ps.setInt(2, user_id);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) > 0) {
                version = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return version;
    }

    public String createNewVersion(int oldVresion, int newVersion, int userId, int exerciseId) {
        Connection con = getConnection();
        String sql = "INSERT INTO dp_user_files (USER_ID, EXERCISE_ID, FILE, VERSION, PATH) " +
                "(SELECT USER_ID, EXERCISE_ID, FILE, ?, PATH FROM dp_user_files WHERE USER_ID = ? AND EXERCISE_ID = ? AND VERSION = ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, newVersion);
            ps.setInt(2, userId);
            ps.setInt(3, exerciseId);
            ps.setInt(4, oldVresion);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            closeConnection(con);
        }
        return "OK";
    }


    public static void main(String[] args) throws Exception {
        DbManager dbm = new DbManager();
//        dbm.readDB();
        dbm.getConnection();
        System.out.println();
    }


}


