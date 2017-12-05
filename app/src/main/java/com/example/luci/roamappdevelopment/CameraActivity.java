package com.example.luci.roamappdevelopment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;

public class CameraActivity extends AppCompatActivity {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private ImageView mImageView;
    private Button mSaveBtn = null;
    private byte[] picData;
    public static Activity camera;
    private ImageButton savebtn = null;
    private ImageButton deletebtn = null;
    private Button takepicB = null;

    private boolean facing;
    int camBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
    int camFrontId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private boolean cameraSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        camera = this;
        getSupportActionBar().hide();
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

        mImageView = (ImageView) findViewById(R.id.imagepreview);
        mImageView.setSystemUiVisibility(SYSTEM_UI_FLAG_FULLSCREEN);

        savebtn = (ImageButton) findViewById(R.id.buttonSavePhoto);
        savebtn.setVisibility(View.INVISIBLE);

        deletebtn = (ImageButton) findViewById(R.id.buttonCancelPhoto);
        deletebtn.setVisibility(View.INVISIBLE);

        takepicB = (Button) findViewById(R.id.takepicbtn);
        takepicB.setVisibility(View.VISIBLE);

        SharedPreferences score = getSharedPreferences("CameraAppFace", 0);
        int face = score.getInt("CameraAppFace", 0);

        if(face == 2)
        {
            facing = false;
        }
        else
        {
            facing = true;
        }
        startUpCamera();

    }

    public void takePictureStatic()
    {
        mCamera.takePicture(null,null,mPicture);
    }


    public void takePicture(View v) throws InterruptedException {

        mCamera.takePicture(null,null,mPicture);
        showSaveDeleteButtons();

    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try{
                        if(facing == true) {
                            mCamera = Camera.open(camBackId);//you can use open(int) to use different cameras
                        }
                        else
                        {
                            mCamera = Camera.open(camFrontId);
                        }
                    } catch (Exception e){
                        Log.d("ERROR", "Failed to get camera: " + e.getMessage());
                    }
                    //setCameraDisplayOrientation(mCamera,this);
                    mCamera.setDisplayOrientation(90);

                    Camera.Parameters params = mCamera.getParameters();
                    params.setRotation(90);
                    params.set("orientation","portrait");
                    mCamera.setParameters(params);

                    mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
                    FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
                    camera_view.addView(mCameraView);//add the SurfaceView to the layout
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/
    public void startUpCamera()
    {
        try{
            /*if(facing == true) {
                mCamera = Camera.open(camBackId);
            }
            else
            {
                mCamera = Camera.open(camFrontId);
            }*/
            mCamera = Camera.open(camBackId);
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }
        //setCameraDisplayOrientation(mCamera,this);
        mCamera.setDisplayOrientation(90);

        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(90);
        params.set("orientation","portrait");
        mCamera.setParameters(params);

        mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
        FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
        camera_view.removeAllViews();
        camera_view.addView(mCameraView);//add the SurfaceView to the layout
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            picData = data;
            mCamera.stopPreview();

            /*Bitmap bitmap = BitmapFactory.decodeByteArray(picData, 0, picData.length);
            Bitmap bitmapGood;
            if(facing) {
                bitmapGood = RotateBitmap(bitmap, 90);
            }
            else{
                bitmapGood = RotateBitmap(bitmap, 270);
            }
            ImageView picturePreview = (ImageView) findViewById(R.id.previewImage);
            picturePreview.setVisibility(View.VISIBLE);
            picturePreview.setImageBitmap(bitmapGood);
            picturePreview.bringToFront();*/
            showSaveDeleteButtons();

        }
    };

    public void savePicture(View v)
    {
        //File pictureFile = getOutputMediaFile();

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(picData, 0, picData.length);
            Bitmap bitmapGood;
            if(facing) {

                bitmapGood = RotateBitmap(bitmap, 90);
            }
            else{
                bitmapGood = RotateBitmap(bitmap, 270);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            String format = simpleDateFormat.format(new Date());
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmapGood, format , "");
            MainActivity.hasNewPhoto = true;
            MainActivity.path = path;
            MainActivity.photoPath = path;
            ImageView picturePreview = (ImageView) findViewById(R.id.previewImage);
            picturePreview.setVisibility(View.INVISIBLE);
            finish();
        } catch (Exception e) {

        }

        //mCameraView.surfaceChanged(mCameraView.getSurfaceHolder(),1,1,1);

    }
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void deletePicture(View v)
    {
        recreate();
        showCameraButton();
    }

    /*public void buttonSwitchCamera(View v)
    {
        SharedPreferences score = getSharedPreferences("CameraAppFace",0);
        SharedPreferences.Editor edit = score.edit();
        if(facing)
        {
            edit.putInt("CameraAppFace", 2);
            facing = false;
            edit.commit();
            //recreate();
            //return;
            Intent i = getIntent();
            finish();
            startActivity(i);
        }
        else
        {
            edit.putInt("CameraAppFace",1);
            facing = true;
            edit.commit();
            Intent i = getIntent();
            finish();
            startActivity(i);
        }
    }*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            takePictureStatic();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return true;
    }
    public void showCameraButton()
    {
        savebtn.setVisibility(View.INVISIBLE);
        deletebtn.setVisibility(View.INVISIBLE);
        takepicB.setVisibility(View.VISIBLE);
        takepicB.bringToFront();
    }
    public void showSaveDeleteButtons()
    {
        savebtn.setVisibility(View.VISIBLE);
        deletebtn.setVisibility(View.VISIBLE);
        takepicB.setVisibility(View.INVISIBLE);

        savebtn.bringToFront();
        deletebtn.bringToFront();
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


}
