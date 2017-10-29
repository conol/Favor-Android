package jp.co.conol.favorlib.favor.model;

/**
 * Created by Masafumi_Ito on 2017/10/19.
 */

public class UsersSetting {

    private String nickname = null;
    private String gender = "male";
    private int age = 20;
    private String push_token = null;

    public UsersSetting(String nickname, String gender, int age, String push_token) {
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.push_token = push_token;
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

    public String getPush_token() {
        return push_token;
    }
}
