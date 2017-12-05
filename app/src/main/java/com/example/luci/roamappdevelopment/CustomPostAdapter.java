package com.example.luci.roamappdevelopment;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by LUCI on 10/31/2017.
 */

public class CustomPostAdapter extends ArrayAdapter<Post>
{
    public CustomPostAdapter(Activity context, ArrayList<Post> posts) {
        super(context, 0, posts);
    }
    private PhotoViewAttacher attacher;
    static class ViewHolder
    {
        ImageView userIcon;
        TextView userName;
        TextView photoDescription;
        TextView photoLocation;
        ImageView postPhoto;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_post_item2, parent, false);
            holder = new ViewHolder();
            holder.userIcon = (ImageView) convertView.findViewById(R.id.user_photo_post);
            holder.userName = (TextView) convertView.findViewById(R.id.user_name_post);
            holder.photoDescription =  (TextView) convertView.findViewById(R.id.post_description);
            holder.photoLocation =  (TextView) convertView.findViewById(R.id.post_location);
            holder.postPhoto = (PhotoView) convertView.findViewById(R.id.post_photo);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

       final Post currentPost = getItem(position);
//        ImageView user_icon = (ImageView) convertView.findViewById(R.id.user_photo_post);
        try
        {
            Glide.with(getContext()).load(currentPost.getUseImagePath()).into(holder.userIcon);
        }catch(Exception e){}
    //    TextView user_name = (TextView) convertView.findViewById(R.id.user_name_post);
        if(currentPost.getUserName() != null)
        {
            holder.userName.setText(currentPost.getUserName());
        }else{holder.userName.setText(currentPost.getUserEmail());}

      // TextView user_photo_description = (TextView) convertView.findViewById(R.id.post_description);
        holder.photoDescription.setText(currentPost.getDescription());

     //   TextView user_photo_location = (TextView) convertView.findViewById(R.id.post_location);
         holder.photoLocation.setText(currentPost.getLocation());

      //  final PhotoView post_photo = (PhotoView) convertView.findViewById(R.id.post_photo);
      //  holder.postPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getContext()).load(currentPost.getPostPhotoFile()).centerCrop().crossFade().into(holder.postPhoto);
      //  holder.postPhoto.bringToFront();
        attacher = new PhotoViewAttacher(holder.postPhoto);
        return convertView;
    }
}
