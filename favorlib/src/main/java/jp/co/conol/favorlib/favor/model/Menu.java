package jp.co.conol.favorlib.favor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

// TODO api修正
public class Menu {

    private String category;
    private Item[] items;

    public class Item {
        private int id;
        private String name;
        private int price;
        private String notes;
        private Image[] images;

        private class Image {
            private String image_url;

            public String getImage_url() {
                return image_url;
            }
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public String getNotes() {
            return notes;
        }

        public String[] getImages() {
            List<String> itemImage = new ArrayList<>();
            for(Image imageObj : images) {
                itemImage.add(imageObj.getImage_url());
            }
            return itemImage.toArray(new String[0]);
        }
    }

    public String getCategory() {
        return category;
    }

    public Item[] getItems() {
        return items;
    }
}
