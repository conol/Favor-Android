package jp.co.conol.favorlib.favor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class EnteringShop {

    private Shop shop;
    private VisitHistory visit_history;

    private class Shop {
        private int id;
        private String name;
        private String introduction;
        private String genre;
        private int zip_code;
        private String address;
        private String phone_number;
        private String opens_at;
        private String closing_day;
        private String[] card_brands;
        private String notes;
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

        public String getOpens_at() {
            return opens_at;
        }


        public String getClosing_day() {
            return closing_day;
        }

        public String[] getCard_brands() {
            return card_brands;
        }

        public String getNotes() {
            return notes;
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
        private int seat_id;
        private int num_visit;
        private String last_visit_at;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public int getSeat_id() {
            return seat_id;
        }

        public int getNum_visit() {
            return num_visit;
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

    public List<String> getShopImages() {
        List<String> shopImage = new ArrayList<>();
        for(Shop.ShopImage shopImageObj : shop.getShopImages()) {
            shopImage.add(shopImageObj.getImageUrl());
        }

        return shopImage;
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

    public String getShopOpenAt() {
        return shop.getOpens_at();
    }


    public String getShopClosingDay() {
        return shop.getClosing_day();
    }

    public String[] getShopCardDrands() {
        return shop.getCard_brands();
    }

    public String getShopNotes() {
        return shop.getNotes();
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

    public int getLastVisitSeatId() {
        return visit_history.getSeat_id();
    }

    public int getNumVisit() {
        return visit_history.getNum_visit();
    }

    public String getLastVisitAt() {
        return visit_history.getLast_visit_at();
    }

    public String getVisitHistoryCreatedAt() {
        return visit_history.getCreated_at();
    }

    public String getVisitHistoryUpdatedAt() {
        return visit_history.getUpdated_at();
    }
}
