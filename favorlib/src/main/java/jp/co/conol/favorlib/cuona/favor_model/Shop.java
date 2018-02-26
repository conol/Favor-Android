package jp.co.conol.favorlib.cuona.favor_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        private ExtensionField[] extension_fields;
        private String created_at;
        private String updated_at;
        private ShopImage[] shop_images;

        private class ExtensionField {

            private int id;
            private String label;
            private String value;

            public int getId() {
                return id;
            }

            public String getLabel() {
                return label;
            }

            public String getValue() {
                return value;
            }
        }

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

        public ExtensionField[] getExtension_fields() {
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

    public String getName() {
        return shop.getName();
    }

    public String getIntroduction() {
        return shop.getIntroduction();
    }

    public String getGenre() {
        return shop.getGenre();
    }

    public String[] getImageUrls() {
        List<String> shopImage = new ArrayList<>();
        for(ShopInfo.ShopImage shopImageObj : shop.getShopImages()) {
            shopImage.add(shopImageObj.getImageUrl());
        }

        return shopImage.toArray(new String[0]);
    }

    public int getZipCode() {
        return shop.getZip_code();
    }

    public String getAddress() {
        return shop.getAddress();
    }

    public String getPhoneNumber() {
        return shop.getPhone_number();
    }

    public List<String[]> getExtensionFields() {
        List<String[]> arrayList = new ArrayList<>();
        for(ShopInfo.ExtensionField extensionField : shop.extension_fields) {
            String[] extensionFieldArray = new String[2];
            extensionFieldArray[0] = extensionField.getLabel();
            extensionFieldArray[1] = extensionField.getValue();
            arrayList.add(extensionFieldArray);
        }
        return arrayList;
    }

    public String getCreatedAt() {
        return shop.getCreated_at();
    }

    public String getUpdatedAt() {
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

    public String getEnteredAt() {
        return visit_history.getCreated_at();
    }
}
