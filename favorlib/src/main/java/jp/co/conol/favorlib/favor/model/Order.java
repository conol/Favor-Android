package jp.co.conol.favorlib.favor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masafumi_Ito on 2017/10/26.
 */

public class Order {

    private int id = 0;
    private int visit_history_id = 0;
    private int menu_item_id = 0;
    private int quantity = 0;
    private String status = null;
    private String created_at = null;
    private String updated_at = null;
    private MenuItem menu_item = null;

    private class MenuItem {
        private int id;
        private String name;
        private int price;
        private String notes;
        private MenuImage[] images;

        private class MenuImage {
            private String image_url;

            public String getImage_url() {
                return image_url;
            }
        }

        public int geId() {
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

        public MenuImage[] getImages() {
            return images;
        }
    }

    // 注文用コンストラクタ
    public Order(int menu_item_id, int quantity) {
        this.menu_item_id = menu_item_id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getVisitHistoryId() {
        return visit_history_id;
    }

    public int getMenuItemId() {
        return menu_item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getMenuItemName() {
        return menu_item.getName();
    }

    public int getMenuItemPrice() {
        return menu_item.getPrice();
    }

    public String getMenuItemNotes() {
        return menu_item.getNotes();
    }

    public String[] getMenuItemImages() {
        List<String> itemImage = new ArrayList<>();
        for(MenuItem.MenuImage imageObj : menu_item.getImages()) {
            itemImage.add(imageObj.getImage_url());
        }
        return itemImage.toArray(new String[0]);
    }
}
