package jp.co.conol.favor_android.custom;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by m_ito on 2017/12/14.
 */

public class SimpleAlertDialog {

    private AlertDialog mAlertDialog = null;

    public SimpleAlertDialog(Context context, String message) {
        mAlertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create();
    }

    public void show() {
        mAlertDialog.show();
    }
}
