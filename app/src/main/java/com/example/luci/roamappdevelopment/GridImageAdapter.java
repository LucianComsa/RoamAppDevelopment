package com.example.luci.roamappdevelopment;

/**
 * Created by LUCI on 23-Nov-17.
 */

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Sebastian on 23/11/2017.
 */
public class GridImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Post> dataList;
    private DisplayMetrics dm;

    public GridImageAdapter(Context c, ArrayList<Post> dataList) {

        mContext = c;
        this.dataList = dataList;
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, dipToPx(100)));
            imageView.setAdjustViewBounds(true);
       //     imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }
        Post p = (Post) getItem(position);
        Glide.with(MainActivity.main).load(p.getPostPhotoFile()).centerCrop().into(imageView);
        return imageView;
    }

    public int dipToPx(int dip) {
        return (int) (dip * dm.density + 0.5f);
    }

}