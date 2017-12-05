package com.example.luci.roamappdevelopment;

import android.net.Uri;

/**
 * Created by LUCI on 11/1/2017.
 */

public class UserProfile
{
    public static String email;
    public static String displayName;
    public static Uri imagePath;
    public static boolean isSetUp;
    public static int postCount = 0;
    public static String userProfileBio = "";
    private static UserProfile instance;
    private UserProfile(){}
    public static UserProfile getInstance()
    {
        if(instance == null)
        {
            instance = new UserProfile();
        }return instance;
    }
    public static void erase()
    {
        email = null;
        displayName = null;
        imagePath = null;
        isSetUp = false;
        userProfileBio = null;
    }
}
