package jp.co.conol.favorlib.favor.model;

/**
 * Created by Masafumi_Ito on 2017/10/19.
 */

public class Register {
    private String nickname = null;
    private String gender = "male";
    private int age = 20;
    private String custom_area1 = null;
    private String custom_area2 = null;
    private String custom_area3 = null;
    private String push_token = null;

    public Register(String nickname, String gender, int age, String custom_area1, String custom_area2, String custom_area3, String push_token) {
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.custom_area1 = custom_area1;
        this.custom_area2 = custom_area2;
        this.custom_area3 = custom_area3;
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

    public String getCustom_area1() {
        return custom_area1;
    }

    public String getCustom_area2() {
        return custom_area2;
    }

    public String getCustom_area3() {
        return custom_area3;
    }

    public String getPush_token() {
        return push_token;
    }
}
