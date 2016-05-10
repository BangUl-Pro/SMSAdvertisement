package com.ironfactory.smsapplication.controllers.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ironfactory.smsapplication.Global;
import com.ironfactory.smsapplication.R;
import com.ironfactory.smsapplication.entities.UserEntity;

public class CoinFragment extends Fragment {

    private static final String TAG = "CoinFragment";

    private TextView moneyView;
    private TextView addView;

    private UserEntity userEntity;

    public static CoinFragment createInstance(UserEntity userEntity) {
        CoinFragment fragment = new CoinFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Global.USER, userEntity);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CoinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userEntity = getArguments().getParcelable(Global.USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coin, container, false);
        init(view);
        return view;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    private void init(View rootView) {
        moneyView = (TextView) rootView.findViewById(R.id.fragment_coin_money);
        addView = (TextView) rootView.findViewById(R.id.fragment_coin_add);

        moneyView.setText(userEntity.getCoin() + "원");

        setListener();
    }

    private void setListener() {
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
