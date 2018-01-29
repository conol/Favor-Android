package jp.co.conol.favorlib.cuona.favor_model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masafumi_Ito on 2017/10/26.
 */

public class UsersAllOrder {

    private int id;
    private String name;
    private int quantity;
    private String created_at;
    private String updated_at;
    private MenuItem menu_item;
    private String shop_id;
    private String shop_name;
    private String enter_at;

    private class MenuItem {
        private int id;
        private String name;
        private int price_cents;
        private String price_format;
        private String notes;
        private MenuImage[] images;

        private class MenuImage {
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

        public int getPriceCents() {
            return price_cents;
        }

        public String getPriceFormat() {
            return price_format;
        }

        public String getNotes() {
            return notes;
        }

        public MenuImage[] getImages() {
            return images;
        }
    }

    public int getId() {
        return id;
    }

    public int getOrderedItemId() {
        return menu_item.getId();
    }

    public String getOrderedItemName() {
        return name;
    }

    public int getOrderedItemPriceCents() {
        return menu_item.getPriceCents();
    }

    public String getOrderedItemPriceFormat() {
        return menu_item.getPriceFormat();
    }

    // 単位を取得
    public String getOrderedItemPriceUnit() {
        return menu_item.getPriceFormat().replaceAll("[0-9]","").replace(".", "").replace(",", "");
    }

    public int getOrderedItemQuantity() {
        return quantity;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public String getOrderedItemNotes() {
        return menu_item.getNotes();
    }

    public String[] getOrderedItemImages() {
        List<String> itemImage = new ArrayList<>();
        for(MenuItem.MenuImage imageObj : menu_item.getImages()) {
            itemImage.add(imageObj.getImage_url());
        }
        return itemImage.toArray(new String[0]);
    }

    public String getShopId() {
        return shop_id;
    }

    public String getShopName() {
        return shop_name;
    }

    public String getEnterAt() {
        return enter_at;
    }
}
