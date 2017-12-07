package com.example.luci.roamappdevelopment;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by LUCI on 13-Nov-17.
 */

public class PostManager {

    private static ArrayList<Post> postsCache = new ArrayList<>();
    private static PostManager instance;
    private PostManager() {}
    public static PostManager getInstance()
    {
        if(instance == null)
        {
            instance = new PostManager();
        }
        return instance;
    }
    public void addPost(Post p)
    {
        postsCache.add(p);
    }
    public void erasePosts()
    {
        postsCache.clear();
    }
    public static void UploadPost(Post post, String galleryPath)
    {
        IDGenerator id = IDGenerator.getInstance();
        String photoID = id.generateID();
        // store photo
        FirebaseStorageManager stmanager = FirebaseStorageManager.getInstance();
        stmanager.uploadPhotoToCloud(photoID,galleryPath,MainActivity.main);
        // store post
        post.setPhotoID(photoID);
        FirebaseDatabaseManager dbmanager = FirebaseDatabaseManager.getInstance();
        dbmanager.writeTODatabase(post);
    }

    public static void loadPosts(ArrayList<Post> posts)
    {
        FirebaseStorageManager manager = FirebaseStorageManager.getInstance();
       for(int i = 0; i < posts.size(); i++)
       {
            try
            {
                manager.retrievePhotoFromCloud(posts.get(i), MainActivity.newsfeed_adapter, "posts");
            }catch(Exception e){
            }
       }

    }
    public static void loadResults(ArrayList<Post> posts)
    {
        SearchActivity.postsForResult.clear();
        SearchActivity.resultsAdapter.clear();
        FirebaseStorageManager manager = FirebaseStorageManager.getInstance();
        for(int i = 0; i < posts.size(); i++)
        {
            try {
                manager.retrievePhotoFromCloud(posts.get(i), SearchActivity.resultsAdapter, "search");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void loadPersonalPosts(ArrayList<Post> posts)
    {
        FirebaseStorageManager manager = FirebaseStorageManager.getInstance();
        for(int i = 0; i < posts.size(); i++)
        {
            try {
                manager.retrievePhotoFromCloud(posts.get(i), MainActivity.postsForPersonalProfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

