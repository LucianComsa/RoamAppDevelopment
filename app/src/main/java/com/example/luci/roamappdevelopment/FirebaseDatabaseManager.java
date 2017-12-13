package com.example.luci.roamappdevelopment;

import android.*;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by LUCI on 11/2/2017.
 */

public class FirebaseDatabaseManager {
    static FirebaseDatabaseManager instance;
    private DatabaseReference mStorageRef;
    private FirebaseDatabase database;
    private ArrayList<String> symbols;
    private ArrayList<String> holders;
    private PostInfo last_item= new PostInfo();
    private int number_of_retrieves = 0;
    private ArrayList<String> postRetrivalHistory;
    private ArrayList<String> locations;
    private int personalPostsCounter;

    private FirebaseDatabaseManager()
    {
        database = FirebaseDatabase.getInstance();
        mStorageRef = database.getReference();
        symbols = getSymbols();
        holders = getPlaceholders();
        personalPostsCounter = 0;
        DatabaseReference getPosts = database.getReference("postHistory");
        DatabaseReference ref = database.getReference("publicContent").child("posts");
        postRetrivalHistory = new ArrayList<>();
        locations =  new ArrayList<>();
        DatabaseReference refForLocations = FirebaseDatabase.getInstance().getReference("publicContent").child("locations");
        refForLocations.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                if(!locations.contains(key))
                {
                    locations.add(key);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public ArrayList<String> getLocations()
    {
        return locations;
    }

    public static FirebaseDatabaseManager getInstance()
    {
        if( instance == null)
        {
            instance = new FirebaseDatabaseManager();
        }
        return instance;
    }
    public static void destroy()
    {
        if(instance != null)
        {
            instance = null;
        }
    }
    public void writeProfileBio()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("publicContent").child("userProfile");
        ref.child(encodeEmail()).setValue(UserProfile.userProfileBio);
    }
    public void getProfileBio()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("publicContent").child("userProfile").child(encodeEmail());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String n = "";
                       try
                       {
                           n=dataSnapshot.getValue(String.class);
                          Log.d("description", n);
                           UserProfile.userProfileBio = n;
                           MainActivity.scaleAndSetProfileBio();
                       }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public boolean writeTODatabase(Post post)
    {
        FirebaseStorageManager.getInstance();
        UserProfile profile = UserProfile.getInstance();
        post.setUseImagePath(profile.imagePath.toString());
       DatabaseReference ref = database.getReference("publicContent").child("posts").child(encodeEmail());
        String n = ref.push().getKey();
        post.setUserName(UserProfile.displayName);
        ref.child(n).setValue(post);
        ref = database.getReference("publicContent").child("postHistory").child(n);
        PostInfo info = new PostInfo();
        info.userEmail = encodeEmail();
        info.key = n;
        info.publicState = true;
        ref.setValue(info);
        //now write the location.
        ref = database.getReference("publicContent").child("locations");
        ref.child(post.getLocation()).child(n).setValue(post);
        return true;
    }
    public void updateLocationList()
    {
        locations.clear();
        DatabaseReference refForLocations = FirebaseDatabase.getInstance().getReference("publicContent").child("locations");
        refForLocations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> snaps = dataSnapshot.getChildren().iterator();
                while(snaps.hasNext())
                {
                    DataSnapshot snap = snaps.next();
                    locations.add(snap.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getUserPosts(final int number)
    {
               final String n = encodeEmail();
               DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicContent").child("posts");
               reference.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       DataSnapshot snp = dataSnapshot.child(n);
                       try
                       {
                           UserProfile.postCount = (int)snp.getChildrenCount();
                           MainActivity.textPostsCount.setText(UserProfile.postCount +"");
                       }catch(Exception e){}
                       if(personalPostsCounter > 0)
                       {
                           Iterator<DataSnapshot> snaps = snp.getChildren().iterator();
                           for(int i = 0; i < personalPostsCounter; i++)
                           {
                               try
                               {
                                   snaps.next();
                               }catch(Exception e){break;}
                           }
                           ArrayList<Post> posts = new ArrayList<Post>();
                           for(int i = 0; i < number; i++)
                           {
                               try
                               {
                                   Post p = snaps.next().getValue(Post.class);
                                   posts.add(p);
                               }catch(Exception e){break;}
                           }
                           if(posts.size() > 0)
                           {
                               PostManager.getInstance().loadPersonalPosts(posts);
                               personalPostsCounter += number;
                           }
                       }
                       else
                       {
                           Iterator<DataSnapshot> snaps = snp.getChildren().iterator();
                           ArrayList<Post> posts = new ArrayList<Post>();
                           for(int i = 0; i < number; i++)
                           {
                               try
                               {
                                   Post p = snaps.next().getValue(Post.class);
                                   posts.add(p);
                               }catch(Exception e) {break;}
                               // Log.d("POST PERSONAL",p.getDescription());
                           }
                           if(posts.size() > 0)
                           {
                               PostManager.getInstance().loadPersonalPosts(posts);
                               personalPostsCounter += number;
                           }
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
    }
    private boolean hasLocation(String location)
    {
        location = location.toLowerCase();
        for(int i = 0; i < locations.size(); i++)
        {
            if(locations.get(i).toLowerCase().equals(location))
            {
                return true;
            }
        }
        return false;
    }
    private String getLocationFromList(String location)
    {
        for(int i = 0; i < locations.size(); i++)
        {
            if(locations.get(i).toLowerCase().equals(location))
            {
                return locations.get(i);
            }
        }
        return "";
    }
    public void searchForResults(final String location)
    {
        if(!hasLocation(location))
        {
            SearchActivity.setResultVisibility(false);
        }
        else
        {
            SearchActivity.setResultVisibility(true);
            DatabaseReference refForPosts = FirebaseDatabase.getInstance().getReference("publicContent").child("locations");
            refForPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot rightSnap = dataSnapshot.child(getLocationFromList(location));
                    if(rightSnap != null)
                    {
                        Iterator<DataSnapshot> snapsForPosts = rightSnap.getChildren().iterator();
                        ArrayList<Post> posts = new ArrayList<Post>();
                        while(snapsForPosts.hasNext())
                        {
                            Post p = snapsForPosts.next().getValue(Post.class);
                            posts.add(p);
                        }
                        PostManager.getInstance().loadResults(posts);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public ArrayList<Post> readFromDatabase(final int numberOfPosts, String startingFrom)
    {
        ArrayList<Post> postsToReturn = new ArrayList<>();
            DatabaseReference refForHistory = FirebaseDatabase.getInstance().getReference("publicContent").child("postHistory");
                refForHistory.limitToLast(numberOfPosts+number_of_retrieves).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<PostInfo> postIds = new ArrayList<>();
                        Iterator<DataSnapshot> snaps = dataSnapshot.getChildren().iterator();
                        String key = last_item.getKey();
                        if(key == null)
                        {
                            key = " ";
                        }
                        if(dataSnapshot.getChildrenCount() >= numberOfPosts)
                        {
                            for(int i = 0; i < numberOfPosts; i++)
                            {
                                if(i == 0)
                                {
                                    last_item = snaps.next().getValue(PostInfo.class);
                                   // postIds.add(last_item);
                                    if(!postRetrivalHistory.contains(last_item.getKey()))
                                    {
                                        postIds.add(last_item);
                                        Log.d("POST", last_item.getKey());
                                        postRetrivalHistory.add(last_item.getKey());
                                    }
                                    //        postIds.add(snaps.next().getValue(PostInfo.class));
                                }
                                else
                                {
                                    PostInfo info = snaps.next().getValue(PostInfo.class);
                                   // postIds.add(info);
                                    if(!postRetrivalHistory.contains(info.getKey()))
                                    {
                                        postIds.add(info);
                                        Log.d("POST", info.getKey());
                                        postRetrivalHistory.add(info.getKey());
                                    }
                                }
                                // Log.d("SOME POST", postIds.get(i).getKey());
                            }
                            if(!key.equals(last_item.getKey()))
                            {
                                DatabaseReference retrievePosts = FirebaseDatabase.getInstance().getReference("publicContent").child("posts");
                                retrievePosts.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ArrayList<Post> posts = new ArrayList<Post>();
                                        Iterator<DataSnapshot> snapsForEmails = dataSnapshot.getChildren().iterator();
                                        for(int i = 0; i < dataSnapshot.getChildrenCount(); i++)
                                        {
                                            DataSnapshot user = snapsForEmails.next();
                                            for(int j = 0; j < postIds.size(); j++)
                                            {
                                                if(user.hasChild(postIds.get(j).getKey()))
                                                {
                                                    Post p = user.child(postIds.get(j).getKey()).getValue(Post.class);
                                                    posts.add(p);
                                                    Log.d(postIds.get(j).getKey(), p.toString());
                                                }
                                            }
                                        }
                                        PostManager.getInstance().loadPosts(posts);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//        for(int i = 0; i < postIds.size(); i++)
//        {
//            Log.d("SOMETHING", postIds.get(i).getKey());
//        }
//        number_of_retrieves += numberOfPosts;
//        DatabaseReference retrievePosts = FirebaseDatabase.getInstance().getReference("publicContent").child("posts");
//        retrievePosts.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        number_of_retrieves += numberOfPosts;
        return postsToReturn;
    }
    private ArrayList<String> getSymbols()
    {
        String dot = ".";
        String hashtag = "#";
        String dollarSign = "$";
        String leftBracket = "[";
        String rightBracket = "]";
        ArrayList<String> list = new ArrayList<>();
        list.add(dot);
        list.add(hashtag);
        list.add(dollarSign);
        list.add(leftBracket);
        list.add(rightBracket);
        return list;
    }
    private ArrayList<String> getPlaceholders()
    {
        String dot = "&";
        String hashtag = "^";
        String dollarSign = "+";
        String leftBracket = "(";
        String rightBracket = ")";
        ArrayList<String> list = new ArrayList<>();
        list.add(dot);
        list.add(hashtag);
        list.add(dollarSign);
        list.add(leftBracket);
        list.add(rightBracket);
        return list;
    }
    private String encodeEmail()
    {
        String encoded = "";
        String email = UserProfile.email;
        for(int i = 0; i < email.length(); i++)
        {
            if(symbols.contains(email.charAt(i)+""))
            {
                int n = symbols.indexOf(email.charAt(i)+"");
                encoded += holders.get(n);
            }
            else
            {
                encoded += email.charAt(i);
            }
        }
        return encoded;

    }
    private String decodeEmail()
    {
        //for testing
        String encoded = encodeEmail();
        String decoded =  "";
        for(int i = 0; i < encoded.length(); i++)
        {
            if(holders.contains(encoded.charAt(i) + ""))
            {
                int n = holders.indexOf(encoded.charAt(i)+"");
                decoded += symbols.get(n);
            }
            else
            {
                decoded += encoded.charAt(i);
            }
        }
        return decoded;

    }
}
