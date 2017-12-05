package com.example.luci.roamappdevelopment;


import android.*;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.zip.Inflater;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.support.v4.view.ViewPager.LayoutParams.*;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
//LUCI UPDATE
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;
    EditText mySearchField;
    static NotificationManager notificationManager;
    static NotificationCompat.Builder builder;
    private String newPostPath;
    boolean isFirstTime = true;
    Toolbar myToolbar;
    NavigationManager navManager;
    private static UserProfile profile;
    private GoogleApiClient googleApiClient;
    final int PERMISSIONS_REQUEST = 123;
    final int IMAGE_SELECTION_REQUEST = 1234;
    // public static TabLayout tabLayout;
    public static BottomNavigationView bottomNav;
    private ImageView righticon;
    private ImageView newsfeedButton;
    private ImageView logo;
    private CardView rounder;
    public static Activity main;
    //Fields for newsfeed
    static SwipeRefreshLayout refreshLayout;
    static CustomPostAdapter newsfeed_adapter;
    static ArrayList<Post> posts;
    static ListView newsfeed;
    //Fields for new post
    static boolean hasNewPhoto = false;
    static String path = "";
    private static EditText descriptionText;
    private static EditText locationText;
    private static ImageView pictureView;
    static String photoPath="";
    //Fields for profile
    static ScrollView profileScrollView;
    static GridImageAdapter gridAdapter;
    static ArrayList<Post> postsForPersonalProfile;
    static ExpandableGridView gridViewProfile;
    static TextView name;
    static TextView email;
    static ImageView userProfilePic;
    static TextView profileBio;
    static FloatingActionButton cameraOpen;
    static TextView textPostsCount;
    private static int initialScrollViewHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = this;
        profile = UserProfile.getInstance();
        setUserProfile();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        newsfeedButton = (ImageView) findViewById(R.id.newsfeedforlogo);
        newsfeedButton.setVisibility(View.INVISIBLE);
        newsfeedButton.setImageResource(R.drawable.ic_newsfeed);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        righticon = (ImageView) findViewById(R.id.righticon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navManager = new NavigationManager();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setSearchBar();
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.menu_newsfeed);
        navManager.setBottom(bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_profile:
                        navManager.setFromBottom(0, item);
                        break;
                    case R.id.menu_newsfeed:
                        navManager.setFromBottom(1, item);
                        break;
                    case R.id.menu_new:
                        navManager.setFromBottom(2, item);
                        break;
                }

                return false;
            }
        });
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        navManager.setSwiper(mViewPager);
        //   tabLayout = (TabLayout) findViewById(R.id.tabs);
        //   tabLayout.setupWithViewPager(mViewPager);
        //   tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        //   tabLayout.getTabAt(0).setIcon(R.drawable.ic_account_circle_black_24dp);
        //   tabLayout.getTabAt(1).setIcon(R.drawable.ic_newsfeed);
        //    tabLayout.getTabAt(2).setIcon(R.drawable.ic_add_black_24dp);
        //mViewPager.setCurrentItem(1);
        logo = (ImageView)findViewById(R.id.logo);
        rounder = (CardView) findViewById(R.id.cardView);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0)
                {
                    rounder.setVisibility(View.INVISIBLE);
                    newsfeedButton.setImageResource(R.mipmap.ic_settingslogo);
                    newsfeedButton.setVisibility(View.VISIBLE);
                    righticon.setImageResource(R.drawable.ic_newsfeed);
                    setSearchBarAppearence();
                    navManager.setFromViewPager(0);
                }
                else if(position == 1)
                {
                    newsfeedButton.setVisibility(View.INVISIBLE);
                    setLogoOnToolbar();
                    newsfeedButton.setVisibility(View.INVISIBLE);
                    righticon.setImageResource(R.drawable.ic_add_black_24dp);
                    setSearchBarAppearence();
                    navManager.setFromViewPager(1);
                }
                else if(position == 2)
                {
                    rounder.setVisibility(View.INVISIBLE);
                    newsfeedButton.setImageResource(R.drawable.ic_newsfeed);
                    newsfeedButton.setVisibility(View.VISIBLE);
                    righticon.setImageResource(R.drawable.ic_add_black_24dp);
                    setSearchBarAppearence();
                    navManager.setFromViewPager(2);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        posts = new ArrayList<>();
        mViewPager.setCurrentItem(1);
        //       setLogoListener();
        //uploadPost();
        setInitialPosts();
        askForPermissions();
        setInitialPersonalPosts();

    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        postsForPersonalProfile = null;
        gridViewProfile = null;
        gridAdapter = null;

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(hasNewPhoto)
        {
            loadLatestGalleryImage(path);
            path = "";
            hasNewPhoto = false;

        }
        // FirebaseDatabaseManager.getInstance().updateLocationList();
    }
    public void onNewsfeedButton(View v)
    {
        if(mViewPager.getCurrentItem() == 0)
        {
            Intent i = new Intent(MainActivity.this, LogInActivity.class);
            i.putExtra("isLogOut", true);
            startActivity(i);
        }else if(mViewPager.getCurrentItem() == 2)
        {
            // buttonCancelNewPost(v);
            mViewPager.setCurrentItem(1);
        }
    }
    public void onClickRightIcon(View v)
    {
        if(mViewPager.getCurrentItem() == 0)
        {
            mViewPager.setCurrentItem(1);
        }
        else if(mViewPager.getCurrentItem() == 1)
        {
            mViewPager.setCurrentItem(2);
        }
        else if(mViewPager.getCurrentItem() == 2)
        {
            buttonPostNewPost(v);
        }
    }
    public void buttonPostNewPost(View v)
    {
        EditText descr = (EditText) findViewById(R.id.editText);
        EditText loc = (EditText) findViewById(R.id.editText2);

        String description = descr.getText().toString();
        String location = loc.getText().toString();
        //Toast.makeText(this,"Posted",Toast.LENGTH_SHORT).show();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("New Post")
                .setContentText("Upload in progress")
                .setSmallIcon(R.mipmap.image_post_notification);
        uploadPost();
        locationText.setText("");
        descriptionText.setText("");
        pictureView.setVisibility(View.INVISIBLE);
        ImageView gallerybtn = (ImageView) findViewById(R.id.imageSelectButton);
        gallerybtn.setVisibility(View.VISIBLE);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        mViewPager.setCurrentItem(1);
    }
    public void buttonCancelNewPost(View v)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure you want to cancel?");

        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                locationText.setText("");
                descriptionText.setText("");
                pictureView.setVisibility(View.INVISIBLE);
                ImageView gallerybtn = (ImageView) findViewById(R.id.imageSelectButton);
                gallerybtn.setVisibility(View.VISIBLE);
                mViewPager.setCurrentItem(1);
            } });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            } });
        adb.show();
    }
    public void buttonOpenCamera(View v)
    {
        Intent newintent = new Intent(MainActivity.this,CameraActivity.class);
        startActivity(newintent);
    }
    private static void setInitialPosts()
    {
        FirebaseDatabaseManager.getInstance().readFromDatabase(5,null);
    }
    private void setLogoOnToolbar()
    {
        rounder.setVisibility(View.VISIBLE);
        ImageView view = (ImageView)findViewById(R.id.logo);
        try
        {
            Glide.with(this).load(UserProfile.imagePath).into(view);
        }catch(Exception e)
        {
            view.setImageResource(R.drawable.logo_24);
        }
    }
    private void setSearchBarAppearence()
    {
        if(mViewPager.getCurrentItem() == 0)
        {
            mySearchField.setEnabled(false);
            mySearchField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mySearchField.setText("           Your profile");
        }
        else if(mViewPager.getCurrentItem() == 1)
        {
            mySearchField.setEnabled(true);
            mySearchField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_black_24dp, 0, 0, 0);
            mySearchField.setText("");
        }
        else if(mViewPager.getCurrentItem() == 2)
        {
            mySearchField.setEnabled(false);
            mySearchField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mySearchField.setText("        Create new post");
        }
    }
    private void setSearchBar()
    {
        mySearchField = (EditText)findViewById(R.id.editText);
        mySearchField.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        mySearchField.setCursorVisible(false);
        mySearchField.setEnabled(false);
        mySearchField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_black_24dp, 0, 0, 0);
//        mySearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(mySearchField.getWindowToken(), 0);
//                    onSearchFunction();
//                    mySearchField.setText("");
//                    return true;
//                }
//                return false;
//            }
//        });
    }
    private void setUserProfile()
    {
        Intent i;
        try
        {
            i = getIntent();
            Bundle b = i.getExtras();
            profile.imagePath = i.getData();
            profile.displayName = b.getString("name");
            profile.email = b.getString("email");
            profile.isSetUp = true;
        }catch(Exception e)
        {profile.isSetUp = false;}

    }
    public void logoListener(View v)
    {
        if(mViewPager.getCurrentItem() == 0)
        {
//            Intent i = new Intent(MainActivity.this, LogInActivity.class);
//            i.putExtra("isLogOut", true);
//            startActivity(i);
        }
        else if(mViewPager.getCurrentItem() == 1)
        {
            mViewPager.setCurrentItem(0);
        }
        else if(mViewPager.getCurrentItem() == 2)
        {
            mViewPager.setCurrentItem(1);
        }

//        View logoView = getToolbarLogoIcon(myToolbar);
//        logoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, LogInActivity.class);
//                i.putExtra("isLogOut", true);
//                startActivity(i);
//            }
//        });
    }
    public void uploadPost()
    {
        Post newpost = new Post();
        newpost.setDescription(descriptionText.getText().toString());
        newpost.setLocation(locationText.getText().toString());
        newpost.setUserEmail(profile.email);
        PostManager.UploadPost(newpost,photoPath);
    }
    private static void refreshFeed()
    {
        FirebaseDatabaseManager.destroy();
        newsfeed.setAdapter(null);
        newsfeed_adapter.clear();
        posts.clear();
        newsfeed.setAdapter(newsfeed_adapter);
        FirebaseDatabaseManager.getInstance().readFromDatabase(5,null);
        refreshLayout.setRefreshing(false);
    }
    public void uploadImage(View v)
    {
        if(newPostPath != null)
        {
            FirebaseStorageManager manager = FirebaseStorageManager.getInstance();
            String ID ="";
            manager.uploadPhotoToCloud(ID, newPostPath, this);
            newPostPath = null;
        }
        else
        {
            Toast.makeText(this, "Choose photo first", Toast.LENGTH_SHORT).show();
        }
    }
    public View  getLogoFromToolbar(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }
    private static void createProfileScreen(View rootView)
    {
        final View root = rootView;
        name = (TextView) rootView.findViewById(R.id.userprofilename);
        email= (TextView) rootView.findViewById(R.id.userprofileemail);
        userProfilePic = (ImageView) rootView.findViewById(R.id.imageViewProfile);
        profileBio = (TextView) rootView.findViewById(R.id.userprofiledescription);
        gridViewProfile = (ExpandableGridView) rootView.findViewById(R.id.images_grid_view);
        profileScrollView = (ScrollView) rootView.findViewById(R.id.parentScrollView);
        textPostsCount = (TextView) rootView.findViewById(R.id.text_posts_count);
        initialScrollViewHeight = profileScrollView.getHeight();
        setUpProfileGridView();
        //setInitialPersonalPosts();
        name.setText(profile.displayName);
        email.setText(profile.email);
        Glide.with(MainActivity.main).load(profile.imagePath).into(userProfilePic);
        FirebaseDatabaseManager.getInstance().getProfileBio();

    }
    private static void setUpProfileGridView()
    {
        if(postsForPersonalProfile == null)
        {
            postsForPersonalProfile = new ArrayList<>();
        }
        gridAdapter = new GridImageAdapter(MainActivity.main, postsForPersonalProfile);
        gridViewProfile.setAdapter(gridAdapter);
        gridViewProfile.setExpanded(true);
        gridViewProfile.setSmoothScrollbarEnabled(true);
    }
    public static void setInitialPersonalPosts()
    {
        FirebaseDatabaseManager.getInstance().getUserPosts(3);
    }
    public void setProfileBio(View v)
    {
        //TODO Implement profile bio button function
    }
    private static void createNewsfeedScreen(View rootView)
    {
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });
        newsfeed = (ListView) rootView.findViewById(R.id.newsfeed);
        newsfeed_adapter = new CustomPostAdapter(MainActivity.main,posts);
        newsfeed.setAdapter(newsfeed_adapter);
        newsfeed.setSmoothScrollbarEnabled(true);
        newsfeed.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(newsfeed.getChildCount() > 0)
                {
                    if (newsfeed.getLastVisiblePosition() == newsfeed.getAdapter().getCount() - 1
                            && newsfeed.getChildAt(newsfeed.getChildCount() - 1).getBottom() == newsfeed.getHeight())
                    {
                        FirebaseDatabaseManager.getInstance().readFromDatabase(5,null);
                    }
                }
            }
        });
    }
    private static void createNewPostScreen(View rootView)
    {
        pictureView = (ImageView) rootView.findViewById(R.id.pictureViewPost);
        descriptionText = (EditText) rootView.findViewById(R.id.editText);
        descriptionText.setSelected(false);
        descriptionText.setFocusableInTouchMode(true);
        cameraOpen = (FloatingActionButton) rootView.findViewById(R.id.floating_open_camera);
       // cameraOpen.setBackgroundColor(R.color.colorPrimary);
        locationText = (EditText) rootView.findViewById(R.id.editText2);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_main_newsfeed, container, false);
            //  TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //  textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            View rootView;
            TextView textView;
            switch (getArguments().getInt(ARG_SECTION_NUMBER))
            {
                case 1:
                    rootView = inflater.inflate(R.layout.layout_main_profile2, container, false);
                    createProfileScreen(rootView);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_main_newsfeed, container, false);
//                    newsfeed = (ListView) rootView.findViewById(R.id.newsfeed);
//                    newsfeed_adapter = new CustomPostAdapter(getActivity(),posts);
//                    newsfeed.setAdapter(newsfeed_adapter);
                    createNewsfeedScreen(rootView);
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_main_new_post2, container, false);
//                    textView = (TextView) rootView.findViewById(R.id.section_label);
//                    textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//                    pictureView = (ImageView) rootView.findViewById(R.id.pictureView);
                    createNewPostScreen(rootView);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.layout_main_profile2, container, false);
                    textView = (TextView) rootView.findViewById(R.id.section_label);
                    textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                    break;
            }
            return rootView;
        }
    }
    public void onClickProfileBioAction(View v)
    {
        Intent i = new Intent(MainActivity.this, DescriptionEditor.class);
        startActivity(i);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
            }
            return null;
        }
    }
    public void onClickSearchBar(View v) {
        //  int n = 0;
        //  n = FirebaseDatabaseManager.getInstance().getLocations().size();
        //  Toast.makeText(main, "There are some locations " + n, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(i);
        //TODO IMplement SearchAction
    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void buttonSelectImage(View v)
    {
        //Toast.makeText(this,"Opening gallery",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_SELECTION_REQUEST);
    }
    public static void scaleAndSetProfileBio()
    {
        String n = UserProfile.userProfileBio;
        String result = "";
        try
        {
            result = n.substring(0,72);
            result+= "...";
            profileBio.setText(result);
        }catch(Exception e){profileBio.setText(UserProfile.userProfileBio);}
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case IMAGE_SELECTION_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    //MODIFICATION HERE
                    newPostPath = filePath;
                    photoPath = filePath;
                    cursor.close();
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    //scaling
                    Bitmap newbitMap = scaleBitMap(yourSelectedImage);
                    pictureView.setBackground(null);
                    pictureView.setImageBitmap(newbitMap);
                    //end scaling
                    pictureView.setVisibility(View.VISIBLE);
                    ImageView imageSelectButton = (ImageView) findViewById(R.id.imageSelectButton);
                    imageSelectButton.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }

    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refreshFeed();
            }
        }
    }
    public void loadLatestGalleryImage(String path)
    {
        Uri uri = Uri.parse(path);
        Uri.parse(uri.toString().replace("file:/", "file:///"));
        //     Bitmap bm = null;
        //     try {
        //         bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        //    }
        //    catch(Exception e){}
//        pictureView.setImageURI(Uri.fromFile(new File(path)));
        //   pictureView.setBackground(null);
        //    Bitmap newbitMap = scaleBitMap(bm);
        //pictureView.setBackground("#000000");
        //    pictureView.setImageBitmap(newbitMap);
        Glide.with(MainActivity.main).load(uri).into(pictureView);
        pictureView.setVisibility(View.VISIBLE);
        ImageView imageSelectButton = (ImageView) findViewById(R.id.imageSelectButton);
        imageSelectButton.setVisibility(View.INVISIBLE);
        //photoPath = uri.getPath();
        //ImageView imageSelectButton = (ImageView) MainActivity.findViewById(R.id.imageSelectButton);
        //imageSelectButton.setVisibility(View.INVISIBLE);
    }

    public Bitmap scaleBitMap(Bitmap bitmap)
    {
        int currentBitmapWidth = bitmap.getWidth();
        int currentBitmapHeight = bitmap.getHeight();
        int ivWidth = pictureView.getWidth();
        int ivHeight = pictureView.getHeight();
        int newWidth = ivWidth;
        int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));
        Bitmap newbitMap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return newbitMap;
    }
    public void openGalleryPicker()
    {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_SELECTION_REQUEST);
    }
    public void askForPermissions()
    {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},PERMISSIONS_REQUEST);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}




