package com.adplan.smsapplication.controllers.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adplan.smsapplication.R;

public class HelpFragment extends Fragment {

    private static final String TAG = "HelpFragment";

    private ImageView image;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_help, container, false);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash, options);
        image = (ImageView) view.findViewById(R.id.fragment_help_image);
        image.setImageBitmap(bitmap);
        // Inflate the layout for this fragment
        return view;
    }
}
