package com.PalasaFamily.DaMo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.PalasaFamily.DaMo.ui.main.MainFragment;


public class ScreenSlidePagerAdaptor extends FragmentStatePagerAdapter {
    public ScreenSlidePagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        MainFragment mainFragment = MainFragment.newInstance();
        mainFragment.SetPosition(position);
        return mainFragment;
    }

    @Override
    public int getCount() {
        return 7;
    }
}
