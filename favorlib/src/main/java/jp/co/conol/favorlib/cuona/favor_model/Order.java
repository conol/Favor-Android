package jp.co.conol.favorlib.cuona.favor_model;

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
    private User user = null;
    private MenuItem menu_item = null;

    private int shop_id = 0;
    private String shop_name = null;
    private String enter_at = null;

    private class User {
        private int id;
        private String nickname;
        private String image_url;

        public int getId() {
            return id;
        }

        public String getNickname() {
            return nickname;
        }

        public String getImageUrl() {
            return image_url;
        }
    }

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

    // 注文用コンストラクタ
    public Order(int orderedItemId, int quantity) {
        this.menu_item_id = orderedItemId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getVisitHistoryId() {
        return visit_history_id;
    }

    public int getOrderedItemId() {
        return menu_item.getId();
    }

    public String getOrderedItemName() {
        return menu_item.getName();
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

    public String getOrderedItemNotes() {
        return menu_item.getNotes();
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public int getOrderedUserId() {
        return user.getId();
    }

    public String getOrderedUserNickname() {
        return user.getNickname();
    }

    public String getOrderedUserImageUrl() {
        return user.getImageUrl();
    }

    public String[] getOrderedItemImages() {
        List<String> itemImage = new ArrayList<>();
        for(MenuItem.MenuImage imageObj : menu_item.getImages()) {
            itemImage.add(imageObj.getImage_url());
        }
        return itemImage.toArray(new String[0]);
    }

    public int getShopId() {
        return shop_id;
    }

    public String getShopName() {
        return shop_name;
    }

    public String getEnterAt() {
        return enter_at;
    }
}
