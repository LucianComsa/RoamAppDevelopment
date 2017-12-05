package com.example.luci.roamappdevelopment;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by LUCI on 23-Nov-17.
 */

public class NavigationManager
{
    private BottomNavigationView bottom;
    private ViewPager swiper;
    boolean isFromBottom;
    boolean bottomHasSet;
    private Menu m;
    public NavigationManager()
    {
        isFromBottom = true;
        bottomHasSet = false;
    }
    public void setFromViewPager(int index)
    {
        isFromBottom = false;
        setFromBottom(index, m.getItem(index));

    }
    public void setBottom(BottomNavigationView bottom) {
        this.bottom = bottom;
        this.m = bottom.getMenu();
    }

    public void setSwiper(ViewPager swiper) {
        this.swiper = swiper;
    }

    public BottomNavigationView getBottom() {

        return bottom;
    }

    public ViewPager getSwiper() {
        return swiper;
    }
    public void setFromBottom(int index, MenuItem menu)
    {
        if(isFromBottom)
        {
          switch (index)
          {
              case 0: menu.setChecked(true);
                    swiper.setCurrentItem(0);
                  break;
              case 1: menu.setChecked(true);
                  swiper.setCurrentItem(1);
                  break;
              case 2:menu.setChecked(true);
                  swiper.setCurrentItem(2);
                  break;
          }
        }
        else
        {
            switch (index)
            {
                case 0:  menu.setChecked(true);
                    break;
                case 1:   menu.setChecked(true);
                    break;
                case 2:   menu.setChecked(true);
                    break;
            }
            isFromBottom = true;
        }
    }
}
