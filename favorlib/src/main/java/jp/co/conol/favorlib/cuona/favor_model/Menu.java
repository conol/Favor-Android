package jp.co.conol.favorlib.cuona.favor_model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class Menu {

    private int id;
    private int menu_group_id;
    private String name;
    private String option;
    private int price_cents;
    private String price_format;
    private String notes;
    private Image[] images;
    private int category_id;
    private String category_name;

    private class Image {
        private String image_url;

        public String getImageUrl() {
            return image_url;
        }
    }

    public int getId() {
        return id;
    }

    public int getMenuGroupId() {
        return menu_group_id;
    }

    public String getName() {
        return name;
    }

    public String getOption() {
        return option;
    }

    public int getPriceCents() {
        return price_cents;
    }

    public String getPriceFormat() {
        return price_format;
    }

    public String getNotes() {
        return notes;
    }

    public String[] getImageUrls() {
        List<String> image_urls = new ArrayList<>();
        for(Image image : images) {
            image_urls.add(image.getImageUrl());
        }

        return image_urls.toArray(new String[0]);
    }

    public int getCategoryId() {
        return category_id;
    }

    public String getCategoryName() {
        return category_name;
    }
}
