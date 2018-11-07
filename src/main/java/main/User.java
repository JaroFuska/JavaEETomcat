package main;

public class User {
    private final int user_id;
    private final String first_name;
    private final String last_name;
    private final String email;
    private final String login;
    private final boolean teacher;

    public User(int user_id, String first_name, String last_name, String email, String login, boolean teacher) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.login = login;
        this.teacher = teacher;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public boolean isTeacher() {
        return teacher;
    }
}
