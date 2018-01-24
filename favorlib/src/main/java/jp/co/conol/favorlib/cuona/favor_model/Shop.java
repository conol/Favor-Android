package jp.co.conol.favorlib.cuona.favor_model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class Shop {

    private ShopInfo shop;
    private VisitHistory visit_history;

    private class ShopInfo {
        private int id;
        private String name;
        private String introduction;
        private String genre;
        private int zip_code;
        private String address;
        private String phone_number;
        private ShopsExtensionField[] extension_fields;
        private String created_at;
        private String updated_at;
        private ShopImage[] shop_images;

        private class ShopImage {
            private String image_url;

            public String getImageUrl() {
                return image_url;
            }
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getIntroduction() {
            return introduction;
        }

        public String getGenre() {
            return genre;
        }

        public int getZip_code() {
            return zip_code;
        }

        public String getAddress() {
            return address;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public ShopsExtensionField[] getExtension_fields() {
            return extension_fields;
        }

        public ShopImage[] getShopImages() {
            return shop_images;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    private class VisitHistory {
        private int id;
        private int visit_group_id;
        private int num_visits;
        private int total_price = 0;
        private String last_visit_at;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public int getVisit_group_id() {
            return visit_group_id;
        }

        public int getNum_visits() {
            return num_visits;
        }

        public int getTotal_price() {
            return total_price;
        }

        public String getLast_visit_at() {
            return last_visit_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public int getShopId() {
        return shop.getId();
    }

    public String getShopName() {
        return shop.getName();
    }

    public String getShopIntroduction() {
        return shop.getIntroduction();
    }

    public String getShopGenre() {
        return shop.getGenre();
    }

    public String[] getShopImages() {
        List<String> shopImage = new ArrayList<>();
        for(ShopInfo.ShopImage shopImageObj : shop.getShopImages()) {
            shopImage.add(shopImageObj.getImageUrl());
        }

        return shopImage.toArray(new String[0]);
    }

    public int getShopZipCode() {
        return shop.getZip_code();
    }

    public String getShopAddress() {
        return shop.getAddress();
    }

    public String getShopPhoneNumber() {
        return shop.getPhone_number();
    }

    public ShopsExtensionField[] getShopsExtensionFields() {
        return shop.getExtension_fields();
    }

    public String getShopCreatedAt() {
        return shop.getCreated_at();
    }

    public String getShopUpdatedAt() {
        return shop.getUpdated_at();
    }


    public int getVisitHistoryId() {
        return visit_history.getId();
    }

    public int getVisitGroupId() {
        return visit_history.getVisit_group_id();
    }

    public int getNumVisits() {
        return visit_history.getNum_visits();
    }

    public String getLastVisitAt() {
        return visit_history.getLast_visit_at();
    }

    public int getTotalPrice() {
        return visit_history.getTotal_price();
    }

    public String getEnterShopAt() {
        return visit_history.getCreated_at();
    }
}