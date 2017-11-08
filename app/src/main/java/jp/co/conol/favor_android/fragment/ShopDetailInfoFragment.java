package jp.co.conol.favor_android.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.favor.model.Shop;

public class ShopDetailInfoFragment extends Fragment {

    private final Gson mGson = new Gson();
    private Context mContext;
    private Shop mShop;
    @BindView(R.id.shopAddressTextView) TextView mShopAddressTextView;
    @BindView(R.id.shopPhoneNumberTextView) TextView mShopPhoneNumberTextView;

    public ShopDetailInfoFragment() {
    }

    public static ShopDetailInfoFragment newInstance(String shopJson) {
        ShopDetailInfoFragment fragment = new ShopDetailInfoFragment();

        Bundle args = new Bundle();
        args.putString("shopJson", shopJson);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // Android6.0以下ではonAttachは次の仕様で呼ばれる
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        mContext = (Context) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // argsを取得
        if(getArguments() != null) {
            mShop = mGson.fromJson(getArguments().getString("shopJson"), Shop.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_detail_info, container, false);
        ButterKnife.bind(this, view);

        if(mShop != null) {
            mShopAddressTextView.setText(mShop.getShopAddress());
            mShopPhoneNumberTextView.setText(mShop.getShopPhoneNumber());
        }


        return view;
    }

}
