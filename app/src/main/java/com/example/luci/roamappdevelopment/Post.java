package com.example.luci.roamappdevelopment;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LUCI on 11/1/2017.
 */

public class Post {

    private String photoID;
    boolean isPublic;
    private String userEmail;
    private String location;
    private String userName;
    private String Description;
    private Date date;
    private int hour;
    private int minute;
    private File postPhotoFile;
    private Bitmap postPhoto;
    private ImageView image;
    private String useImagePath;

    //fields for feeding newsfeed
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {

        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Bitmap getpostPhoto() {
        return postPhoto;
    }
    public void setImage(ImageView v)
    {
        image = v;
    }
    public ImageView getImage()
    {
        return image;
    }
    public void setPostPhotoFile(File postPhotoFile) {
        this.postPhotoFile = postPhotoFile;
    }

    public File getPostPhotoFile() {
        return postPhotoFile;
    }

    public void setpostPhoto(Bitmap postPhoto) {
        this.postPhoto = postPhoto;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        Description = description;
    }


    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return Description;
    }
    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public Post()
    {
        photoID = "";
        isPublic = true;
        Calendar cal = Calendar.getInstance();
        date = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);
    }
    public void setState(Boolean b)
    {
        isPublic = b;
    }
    public boolean getState()
    {
        return isPublic;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setID(String path) {

        this.photoID = path;
    }

    public void setUseImagePath(String useImagePath) {
        this.useImagePath = useImagePath;
    }

    public String getUseImagePath() {

        return useImagePath;
    }

    public String toString()
    {
            if(Description == null)
            {
                return userEmail;
            }
            else
            {
                return getDescription();
            }
    }
}
