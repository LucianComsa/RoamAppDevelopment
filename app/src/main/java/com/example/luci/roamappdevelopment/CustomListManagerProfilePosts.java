package com.example.luci.roamappdevelopment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by LUCI on 15-Dec-17.
 */

public class CustomListManagerProfilePosts
{
    private LinearLayout baseLayout;
    ArrayList<View> usedViews;


    public CustomListManagerProfilePosts(LinearLayout param)
    {
        this.baseLayout = param;
        usedViews = new ArrayList<>();
    }

    public void setPosts(ArrayList<Post> posts)
    {
        int n = 0;
        if(usedViews.size() != 0)
        {
            n = usedViews.size()-1;
        }
        for(int i = n; i < posts.size(); i++)
        {
            try
            {
               View v = getView(posts.get(i));
//                if(!usedViews.contains(v))
//                {
//                    usedViews.add(v);
                    baseLayout.addView(v);
        //        }
            }catch(Exception e){e.printStackTrace();}
        }
    }
    public void setVisible()
    {
        baseLayout.setVisibility(View.VISIBLE);
    }
    public void setGone()
    {
        baseLayout.setVisibility(View.GONE);
    }
    public void clearAll()
    {
        usedViews.clear();
        baseLayout.removeAllViews();
    }
    private View getView(final Post currentPost)
    {
            View convertView;
            convertView = LayoutInflater.from(MainActivity.main).inflate(R.layout.layout_post_item2, baseLayout);
            ImageView userIcon = (ImageView) convertView.findViewById(R.id.user_photo_post);
            TextView userName = (TextView) convertView.findViewById(R.id.user_name_post);
            TextView photoDescription =  (TextView) convertView.findViewById(R.id.post_description);
            TextView photoLocation =  (TextView) convertView.findViewById(R.id.post_location);
            ImageView postPhoto = (ImageView) convertView.findViewById(R.id.post_photo);
        try
        {
            Glide.with(MainActivity.main).load(currentPost.getUseImagePath()).into(userIcon);
        }catch(Exception e){}
        //    TextView user_name = (TextView) convertView.findViewById(R.id.user_name_post);
        if(currentPost.getUserName() != null)
        {
            userName.setText(currentPost.getUserName());
        }else{userName.setText(currentPost.getUserEmail());}

        // TextView user_photo_description = (TextView) convertView.findViewById(R.id.post_description);
        photoDescription.setText(currentPost.getDescription());

        //   TextView user_photo_location = (TextView) convertView.findViewById(R.id.post_location);
        photoLocation.setText(currentPost.getLocation());

        //  final PhotoView post_photo = (PhotoView) convertView.findViewById(R.id.post_photo);
        //  holder.postPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(MainActivity.main).load(currentPost.getPostPhotoFile()).centerCrop().crossFade().into(postPhoto);
        //  holder.postPhoto.bringToFront();
        PhotoViewAttacher attacher = new PhotoViewAttacher(postPhoto);
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                File f = currentPost.getPostPhotoFile();
                Intent i = new Intent(MainActivity.main, PhotoShowActivity.class);
                i.putExtra("image", f);
                MainActivity.main.startActivity(i);
            }
        });
        return convertView;
    }
}
