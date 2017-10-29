package jp.co.conol.favorlib.favor.model;

/**
 * Created by Masafumi_Ito on 2017/10/26.
 */

// TODO api修正
public class UsersAllOrder {

    private String enter_at;
    private Shop shop;
    private Order[] orders;

    private class Shop {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private class Order {

        private int id = 0;
        private int menu_item_id = 0;
        private String name = null;
        private int price;
        private int quantity = 0;
        private String created_at = null;
        private String updated_at = null;
        private MenuImage menu_images = null;

        private class MenuImage {
            private String image_url;

            public String getImage_url() {
                return image_url;
            }
        }
    }

    public String getEnterAt() {
        return enter_at;
    }

    public int getShopId() {
        return shop.getId();
    }

    public String getShopName() {
        return shop.getName();
    }

    public Order[] getOrders() {
        return orders;
    }
}
