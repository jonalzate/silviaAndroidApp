package com.neurala.silvia;

import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    // Tag for the log
    private static final String TAG = MainActivity.class.getSimpleName();

    // drawing view
    private DrawingView mDrawView;

    // current color selection
    private Button mCurrentColor;

    // video fragment
    private VideoFragment mVideoFrag;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the drawing view
        mDrawView = (DrawingView) findViewById(R.id.drawing);
        if(mDrawView != null){
            mDrawView.setBackgroundColor(Color.TRANSPARENT);
        }

        // color palette at bottom of screen... Candidate to be removed
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        mCurrentColor = (Button) paintLayout.getChildAt(0);



        Button mEraseBtn = (Button) findViewById(R.id.erase_btn);
        mEraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.erase_btn) {
                    mDrawView.eraseCanvas(true);
                    Toast.makeText(MainActivity.this, "Erase button clicked", Toast.LENGTH_SHORT).show();

                }

            }
        });

        mVideoFrag = new VideoFragment();
        FragmentManager fManager = getSupportFragmentManager();
        fManager.beginTransaction().replace(R.id.frag_container, mVideoFrag).commit();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void paintClicked(View view) {
        // use chosen color
        if (view != mCurrentColor) {
            //update color
            Button colorBtn = (Button) view;
            String color = view.getTag().toString();

            mDrawView.setColor(color);
        }
    }



}
