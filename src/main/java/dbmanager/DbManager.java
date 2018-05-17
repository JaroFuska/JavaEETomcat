package dbmanager;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbManager {
    private final String DB_URL = "jdbc:mysql://localhost/DP";

    //    private final String DB_URL = "jdbc:sqlite:/DB/DP.db";
    public enum RegisterState {
        OK,
        EMAIL,
        LOGIN
    }


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

    public String login(String login, String pass) {
        Connection con = getConnection();
        String sql = "SELECT * FROM DP_USERS WHERE email = ? OR login = ? AND STATUS = 1";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, login);
            ps.setString(2, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("PASSWORD").equals(pass)) {
                    String type = (rs.getBoolean("TEACHER") ? "teacher" : "student");
                    return type;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        }
        return "OK";
    }

    public String dp_exercise_files_dir_insert(int exerciseID, File file, String path, String text) throws FileNotFoundException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                dp_exercise_files_dir_insert(exerciseID, f, path, text);
            }
        } else {
            dp_exercise_files_file_insert(exerciseID, new FileInputStream(file), path, text);
        }
        return "OK";
    }

    public String dp_exercise_files_file_insert(int exerciseID, InputStream file, String path, String text) {
        Connection con = getConnection();
        String sql = "INSERT INTO dp_exercise_files (exercise_id, file, path, text) VALUES(?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, exerciseID);
            ps.setBlob(2, file);
            ps.setString(3, path);
            ps.setString(4, text);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
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
        }
        return "OK";
    }

    public String dp_exercises_insert(int exerciseID, String text, boolean visible) {
        if (getExercisesCount() >= exerciseID) {
            return dp_exercises_update(exerciseID, text, visible);
        } else {
            Connection con = getConnection();
            String sql = "INSERT INTO dp_exercises (exercise_id, text, visible) VALUES(?, ?, ?)";
            try {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, exerciseID);
                ps.setString(2, text);
                ps.setBoolean(3, visible);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return "OK";
        }
    }

    public String dp_exercises_update(int exerciseID, String text, boolean visible) {
        Connection con = getConnection();
        String sql = "UPDATE dp_exercises SET text = ?, visible = ? WHERE exercise_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, text);
            ps.setBoolean(2, visible);
            ps.setInt(3, exerciseID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "OK";
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
        }
        return list;
    }

    public String getExerciseDesc(String exerciseID) {
        Connection con = getConnection();
        String ret = "";
        try {
            PreparedStatement statement = con.prepareStatement("SELECT TEXT FROM dp_exercises where exercise_id = ?");
            statement.setString(1, exerciseID);
            ResultSet rs = statement.executeQuery();
            rs.next();
            ret = rs.getString("TEXT");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean getExerciseVisibility(String exerciseID) {
        Connection con = getConnection();
        boolean ret = false;
        try {
            PreparedStatement statement = con.prepareStatement("SELECT VISIBLE FROM dp_exercises where exercise_id = ?");
            statement.setString(1, exerciseID);
            ResultSet rs = statement.executeQuery();
            rs.next();
            ret = rs.getBoolean("VISIBLE");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String createExercise(int exerciseID) throws SQLException, IOException {
        String mainDir = "";
        Connection con = getConnection();
        String sql = "SELECT file, path FROM dp_exercise_files WHERE exercise_id = ?";
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, exerciseID);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            String path = resultSet.getString(2);
            if (mainDir.equals("")) {
                mainDir = "/" + path.split("/")[0];
                new File(mainDir).mkdirs();
            }
            new File("/" + path.substring(0, path.lastIndexOf("/"))).mkdirs();
            File file = new File("/" + path);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[1];
            InputStream is = resultSet.getBinaryStream(1);
            while (is.read(buffer) > 0) {
                fos.write(buffer);
            }
            fos.close();
        }
        con.close();
        return mainDir;
    }

    public void insert(String code, String test) {
        try {
            Connection conn = getConnection();
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO DP_RUN_FILES (code, test) VALUES(?, ?)");
            pstm.setString(1, code);
            pstm.setString(2, test);
            pstm.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getUserData(String login) {
        Connection con = getConnection();
        HashMap<String, String> map = new HashMap<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM dp_users where login = ?");
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
        }
        return ret;
    }

    public static void main(String[] args) throws Exception {
        DbManager dbm = new DbManager();
//        dbm.readDB();
        dbm.getConnection();
        System.out.println();
    }
}


