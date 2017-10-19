package jp.co.conol.favorlib.favor.model;

/**
 * Created by Masafumi_Ito on 2017/10/19.
 */

public class User {
    private int id;
    private String nickname = null;
    private String gender = "male";
    private int age = 20;
    private String custom_area1 = null;
    private String custom_area2 = null;
    private String custom_area3 = null;
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
            String custom_area1,
            String custom_area2,
            String custom_area3,
            String app_token,
            String push_token,
            boolean notifiable,
            String created_at,
            String updated_at) {

        this.id = id;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.custom_area1 = custom_area1;
        this.custom_area2 = custom_area2;
        this.custom_area3 = custom_area3;
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

    public String getCustom_area1() {
        return custom_area1;
    }

    public String getCustom_area2() {
        return custom_area2;
    }

    public String getCustom_area3() {
        return custom_area3;
    }

    public String getApp_token() {
        return app_token;
    }

    public String getPush_token() {
        return push_token;
    }

    public boolean getNotifiable() {
        return notifiable;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
