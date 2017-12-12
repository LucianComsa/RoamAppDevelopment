package com.example.luci.roamappdevelopment;

import android.content.Context;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by LUCI on 11/1/2017.
 */

public class FirebaseStorageManager
{
    static FirebaseStorageManager instance;
    private StorageReference mStorageRef;
    private FirebaseStorageManager(){}

    public static FirebaseStorageManager getInstance()
    {
        if( instance == null)
        {
            instance = new FirebaseStorageManager();
        }
        return instance;
    }
    public void uploadPhotoToCloud(String photoID, String galleryPath,final Context context)
    {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Uri uri = Uri.parse(galleryPath);
        String uriString = uri.toString();
        if(uriString.contains("content")) {
            Uri.parse(uri.toString().replace("file:/", "file:///"));
           // Log.d("DEBUG","path: " + uri.toString());
        }
        else
        {
            Uri newuri = Uri.fromFile(new File(uri.toString()));
            uri = newuri;
          //  Log.d("DEBUG","path2: " + newuri.toString());
        }
        StorageReference riversRef = mStorageRef.child("public_content/" + UserProfile.email + "/images/" + photoID + ".jpg");
        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, "The post has been submited", Toast.LENGTH_SHORT).show();
                        @SuppressWarnings("VisibleForTests")  Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        MainActivity.builder.setContentText("Upload complete")
                                .setProgress(0,0,false);
                        MainActivity.notificationManager.notify(1, MainActivity.builder.build());

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")  int n = (int)taskSnapshot.getTotalByteCount();
                        @SuppressWarnings("VisibleForTests")  int actual = (int) taskSnapshot.getBytesTransferred();
                        MainActivity.builder.setProgress(n,actual,false);
                        MainActivity.notificationManager.notify(1, MainActivity.builder.build());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }
    public void retrievePhotoFromCloud(Post holder, final CustomPostAdapter adapter, String type) throws IOException
    {
        String a = "accept";
       if(type.equals("search"))
       {
            int n = hasPost(holder.getPhotoID());
           if(n > -1)
           {
               a = "no";
               adapter.add(MainActivity.newsfeed_adapter.getItem(n));
           }
       }
       if(a.equals("accept"))
       {
           final String id = holder.getPhotoID();
           final Post p = holder;
           final File localFile = File.createTempFile(holder.getPhotoID(), "jpg");
           mStorageRef = FirebaseStorage.getInstance().getReference();
           StorageReference riversRef = mStorageRef.child("public_content/" + holder.getUserEmail() + "/images/" + holder.getPhotoID() + ".jpg");
           riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                   Post newPost= p;
                   newPost.setPostPhotoFile(localFile);
                   adapter.add(newPost);
                   //   MainActivity.newsfeed_adapter.notifyDataSetChanged();
                   // PostManager.getInstance().addPost(newPost);
               }

           });
       }
    }
    public void retrievePhotoFromCloud(Post holder, final ArrayList<Post> list) throws IOException
    {
        int n = hasPost(holder.getPhotoID());
       if(n > -1)
       {
            list.add(MainActivity.newsfeed_adapter.getItem(n));
       }else
       {
           final String id = holder.getPhotoID();
           final Post p = holder;
           final File localFile = File.createTempFile(holder.getPhotoID(), "jpg");
           mStorageRef = FirebaseStorage.getInstance().getReference();
           StorageReference riversRef = mStorageRef.child("public_content/" + holder.getUserEmail() + "/images/" + holder.getPhotoID() + ".jpg");
           riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                   Post newPost= p;
                   newPost.setPostPhotoFile(localFile);
                   list.add(p);
                   MainActivity.gridAdapter.notifyDataSetChanged();
                   //   MainActivity.newsfeed_adapter.notifyDataSetChanged();
                   // PostManager.getInstance().addPost(newPost);
               }
           });
       }
    }
    private int hasPost(String photoID)
    {
        int result = -1;
        ArrayAdapter<Post> a = MainActivity.newsfeed_adapter;
        for(int i = 0; i < a.getCount(); i++)
        {
            if(photoID.equals(a.getItem(i).getPhotoID()))
            {
                return i;
            }
        }
        return result;
    }
}

