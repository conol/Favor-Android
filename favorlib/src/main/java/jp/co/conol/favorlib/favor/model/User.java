package jp.co.conol.favorlib.favor.model;

/**
 * Created by Masafumi_Ito on 2017/10/19.
 */

public class User {
    private int id;
    private String nickname = null;
    private String gender = "male";
    private int age = 20;
    private String app_token = null;
    private String push_token = null;
    private boolean notifiable = false;
    private String created_at = null;
    private String updated_at = null;

    public User(
            int id,
            String nickname,
            String gender,
            int age,
            String app_token,
            String push_token,
            boolean notifiable,
            String created_at,
            String updated_at) {

        this.id = id;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.app_token = app_token;
        this.push_token = push_token;
        this.notifiable = notifiable;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getAppToken() {
        return app_token;
    }

    public String getPushToken() {
        return push_token;
    }

    public boolean getNotifiable() {
        return notifiable;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }
}
