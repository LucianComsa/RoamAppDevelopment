package com.example.luci.roamappdevelopment;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    EditText searchBar;
    Toolbar toolbar;
    ConstraintLayout root;
    //fields for result list
    static CustomPostAdapter resultsAdapter;
    static ArrayList<Post> postsForResult;
    static ListView resultList;
    public static Activity searchActivity;

    //fields for no result
    private static ImageView noLocationFound;
    private static TextView noLocationFoundText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setToolbar();
        root = (ConstraintLayout) findViewById(R.id.rootViewSearchActivity);
        resultList = (ListView) findViewById(R.id.listViewSearchActivity);
        postsForResult = new ArrayList<>();
        resultsAdapter = new CustomPostAdapter(SearchActivity.this, postsForResult);
        resultList.setAdapter(resultsAdapter);
        resultList.setSmoothScrollbarEnabled(true);
        setSearchBar();
        searchActivity = SearchActivity.this;
        noLocationFound = (ImageView) findViewById(R.id.nothing_found);
        noLocationFoundText = (TextView) findViewById(R.id.nothing_found_text);
        setViewsInvisible();
        root.setOnTouchListener(new OnSwipeTouchListener(SearchActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {
                finish();
            }
            public void onSwipeLeft() {

            }
            public void onSwipeBottom() {

            }

        });
    }
    private void setToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar_for_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    private void setSearchBar()
    {
        searchBar = (EditText)findViewById(R.id.editTextOnSearchActivity);
        searchBar.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        searchBar.setCursorVisible(false);
        searchBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_black_24dp, 0, 0, 0);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                    setViewsInvisible();
                    onSearchFunction(searchBar.getText().toString());
                    searchBar.setText("");
                    return true;
                }
                return false;
            }
        });
    }
    private void onSearchFunction(String search)
    {
        FirebaseDatabaseManager.getInstance().searchForResults(search);
    }
    public static void setResultVisibility(boolean hasFound)
    {
        if(hasFound)
        {
            resultList.setVisibility(View.VISIBLE);
        }else
        {
            noLocationFound.setVisibility(View.VISIBLE);
            noLocationFoundText.setVisibility(View.VISIBLE);
        }
    }
    private void setViewsInvisible()
    {
        resultList.setVisibility(View.INVISIBLE);
        noLocationFound.setVisibility(View.INVISIBLE);
        noLocationFoundText.setVisibility(View.INVISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    class OnSwipeTouchListener implements View.OnTouchListener {

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
