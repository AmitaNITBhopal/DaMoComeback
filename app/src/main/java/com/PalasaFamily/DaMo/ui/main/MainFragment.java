package com.PalasaFamily.DaMo.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.PalasaFamily.DaMo.AddBackground;
import com.PalasaFamily.DaMo.MainActivity;
import com.PalasaFamily.DaMo.R;

public class MainFragment extends Fragment {


    //private MainViewModel mViewModel;
    private int mPosition;
    SharedPreferences sp;
    ViewPager mViewPager;

    public MainFragment() {
        mPosition = 0;
    }

    /*public MainFragment(int position) {
        mPosition = position;
    }*/

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public void SetPosition(int position)
    {
        mPosition = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainViewModel mViewModel = ViewModelProviders.of(this.getActivity()).get(MainViewModel.class);
        mViewModel.SetPosition(mPosition);

        LinearLayout l = (LinearLayout) getView().findViewById(R.id.layout_linear);
        l.setBackground(mViewModel.GetDrawableBG());

        String[] strArray = mViewModel.getText();
        TextView textV = (TextView) getView().findViewById(R.id.txtMsg);
        textV.setText(strArray[0]);
        TextView textAuthor = (TextView) getView().findViewById(R.id.txtAuthor);
        textAuthor.setText(strArray[1]);

        TextView dateTextV = (TextView) getView().findViewById(R.id.dateTextView);
        dateTextV.setText(mViewModel.getDate());

        InitializeIndicators();
        toggleArrowVisibility(mPosition == 0, mPosition == getResources().getInteger(R.integer.screen_count) - 1);
    }

    public void toggleArrowVisibility(boolean isAtZeroIndex, boolean isAtLastIndex) {
        ImageButton leftNav =  (ImageButton) getView().findViewById(R.id.leftNavButton);
        ImageButton rightNav = (ImageButton) getView().findViewById(R.id.rightNavButton);
        if(isAtZeroIndex)
            leftNav.setVisibility(View.INVISIBLE);
        else
            leftNav.setVisibility(View.VISIBLE);
        if(isAtLastIndex)
            rightNav.setVisibility(View.INVISIBLE);
        else
            rightNav.setVisibility(View.VISIBLE);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity mainActivity = (MainActivity) activity;
        mViewPager = mainActivity.GetViewPagerObj();
    }

    void InitializeIndicators() {

        ImageButton leftNav = (ImageButton) getView().findViewById(R.id.leftNavButton);
        ImageButton rightNav = (ImageButton) getView().findViewById(R.id.rightNavButton);

        // Images left navigation
        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager != null) {
                    mViewPager.arrowScroll(ViewPager.FOCUS_LEFT);
                }
            }
        });

        // Images right navigation
        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager != null) {
                    mViewPager.arrowScroll(ViewPager.FOCUS_RIGHT);
                }
            }
        });


    }


}
