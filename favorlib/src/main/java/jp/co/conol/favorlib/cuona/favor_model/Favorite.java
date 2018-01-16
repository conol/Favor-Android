package jp.co.conol.favorlib.cuona.favor_model;

/**
 * Created by Masafumi_Ito on 2017/10/26.
 */

public class Favorite {
    private int id = 0;
    private String name;
    private int level;
    private String created_at = null;
    private String updated_at = null;

    public Favorite(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }
}
