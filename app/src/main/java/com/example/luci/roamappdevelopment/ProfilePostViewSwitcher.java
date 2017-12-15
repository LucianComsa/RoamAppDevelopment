package com.example.luci.roamappdevelopment;

import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by LUCI on 12/13/2017.
 */

public class ProfilePostViewSwitcher
{
    private ExpandableGridView gridView;
    private ListView listView;

    public ProfilePostViewSwitcher(ListView listView, ExpandableGridView gridView)
    {
        this.gridView = gridView;
        this.listView = listView;
    }

    public void setListView()
    {
        gridView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        ListAdapter a = listView.getAdapter();
    }
    public void setGridView()
    {
        gridView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }
}
