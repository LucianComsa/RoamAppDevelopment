package com.example.luci.roamappdevelopment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoShowActivity extends Activity{
  //  Toolbar bar;
    PhotoView photoToShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_show);
        //set up bar
       // bar = (Toolbar) findViewById(R.id.toolbar_with_back_key);
      //  bar.setTitle("");
        //setSupportActionBar(bar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setDisplayShowHomeEnabled(true);
        photoToShow = (PhotoView) findViewById(R.id.photo_show);
        Intent i = getIntent();
        try
        {
            File receive = (File) i.getSerializableExtra("image");
         //  getSupportActionBar().setTitle(i.getStringExtra("title"));
            Glide.with(this).load(receive).into(photoToShow);


        }catch(Exception e){finish();}
        ConstraintLayout root = (ConstraintLayout) findViewById(R.id.layout_photo_show);
        root.setOnTouchListener(new OnSwipeTouchListener(PhotoShowActivity.this)
        {
            public void onSwipeRight() {
                PhotoShowActivity.this.finish();
            }
            public void onSwipeLeft() {
                //        PhotoShowActivity.this.finish();
            }
            public void onSwipeTop() {
                //       PhotoShowActivity.this.finish();
            }
            public void onSwipeBottom() {
                //      PhotoShowActivity.this.finish();
            }
        });
//        photoToShow.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                PhotoViewAttacher zoomer = new PhotoViewAttacher((ImageView)v);
//                zoomer.update();
//                return false;
//            }
//        });
        PhotoViewAttacher zoomer = new PhotoViewAttacher(photoToShow);
        zoomer.update();
    }
//    @Override
//    public boolean onSupportNavigateUp() {
//        finish();
//        onBackPressed();
//        return true;
//    }
    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

}
