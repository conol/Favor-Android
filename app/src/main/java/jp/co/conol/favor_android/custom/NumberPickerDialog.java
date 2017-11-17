package jp.co.conol.favor_android.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import jp.co.conol.favor_android.R;

/**
 * Created by m_ito on 2017/11/05.
 */

public class NumberPickerDialog extends DialogFragment {

    private OnPositiveButtonClickedListener mListener;
    private int mViewId;
    private String mTitle;
    private int mMinValue;
    private int mMaxValue;
    private String mOkString;
    private String mCancelString;

    public interface OnPositiveButtonClickedListener {
        void onPositiveButtonClicked(int value);
    }

    public static NumberPickerDialog newInstance(int viewId, String title, int minValue, int maxValue, String okString, String cancelString) {
        NumberPickerDialog fragment = new NumberPickerDialog();
        Bundle args = new Bundle();
        args.putInt("viewId", viewId);
        args.putString("title", title);
        args.putInt("minValue", minValue);
        args.putInt("maxValue", maxValue);
        args.putString("ok", okString);
        args.putString("cancel", cancelString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnPositiveButtonClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnOkButtonListener");
        }
    }

    // Android6.0以下ではonAttachは次の仕様で呼ばれる
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        try {
            mListener = (OnPositiveButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnOkButtonListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mViewId = getArguments().getInt("viewId");
            mTitle = getArguments().getString("title");
            mMinValue = getArguments().getInt("minValue");
            mMaxValue = getArguments().getInt("maxValue");
            mOkString = getArguments().getString("ok");
            mCancelString = getArguments().getString("cancel");
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(mViewId, null, false);

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(mMaxValue);
        numberPicker.setMinValue(mMinValue);
        numberPicker.setValue(1);
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setPositiveButton(mOkString, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // OKクリック時の処理
                mListener.onPositiveButtonClicked(numberPicker.getValue());
            }
        });
        builder.setNegativeButton(mCancelString, null);
        builder.setView(view);
        return builder.create();
    }
}