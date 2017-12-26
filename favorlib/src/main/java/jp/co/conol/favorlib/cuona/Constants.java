package jp.co.conol.favorlib.cuona;

/**
 * Created by m_ito on 2017/12/13.
 */

// package-private
class Constants {
    static class Urls {

        // サービス
        static final String SERVICE_NAME = "Favor";   // サービス名
        static final String SERVICE_JSON_NAME = "favor";   // jsonに書き込まれているサービス名
        static final String SERVICE_KEY = "UXbfYJ6SXm8G";   // サービスキー

        // デバイスマネージャーサーバー
        static final String CDMS_URL = "http://manage-dev.cuona.io";   // デバイスマネージャーURL（開発）
//        static final String CDMS_URL = "https://manage.cuona.io";   // デバイスマネージャーURL（本番）

        // サービスサーバー
        static final String FAVOR_URL = "http://favor-dev.cuona.io";   // FavorURL（開発）
//        static final String FAVOR_URL = "https://favor.cuona.io";   // FavorURL（本番）

        // ログ
//        static final String LOG_URL = ""; // ログを送らない
        static final String LOG_URL = CDMS_URL + "/api/device_logs/" + SERVICE_KEY + ".json"; // ログを送信（編集不要）
    }
}
