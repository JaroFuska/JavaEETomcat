import java.io.*;
import java.sql.*;
import java.util.ArrayList;

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

    //TODO show different content when teacher is pressent
    public String login(String login, String pass) {
        Connection con = getConnection();
        String sql = "SELECT * FROM DP_USERS WHERE email = ? OR login = ?";
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


    public String dp_users_insert(String firstname, String lastname, String email, String login, String pass, boolean teacher) {
        Connection con = getConnection();
        String sql = "INSERT INTO DP_USERS (first_name, last_name, email, login, password, teacher)" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, firstname);
            ps.setString(2, lastname);
            ps.setString(3, email);
            ps.setString(4, login);
            ps.setString(5, pass);
            ps.setBoolean(6, teacher);
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

    public String dp_exercise_files_update(int exerciseID, String text) {
        Connection con = getConnection();
        String sql = "UPDATE dp_exercises SET text = ? WHERE exercise_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, text);
            ps.setInt(2, exerciseID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "OK";
    }

    public int getExercises() {
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

//    public void insert(InputStream code, InputStream test) {
//        try {
//            Connection conn = getConnection();
//            PreparedStatement pstm = conn.prepareStatement("INSERT INTO DP_RUN_FILES (code, test) VALUES(?, ?)");
//            pstm.setBlob(1, code);
//            pstm.setBlob(2, test);
//            pstm.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

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

    public static void main(String[] args) throws Exception {
        DbManager dbm = new DbManager();
//        dbm.readDB();
        dbm.getConnection();
        System.out.println();
    }
}


