package com.example.mayank.rotaterotate;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //Initializing necessary parameters

    //ImageView squareImage;
    ImageView bottomImage;
    LayoutParams params;
    boolean movedOnce = false;
    boolean squareMoved = true;
    boolean movedCompletely = false;
    float newValueOfSquareY = 500;
    TextView timerTextView;
    Handler handlerSecond;
    Runnable runSecond;
    Handler handlerMiliSecond;
    Runnable runMiliSecond;

    /**
     *
     * Process JSON data from the given link to get date and time
     *
     */
    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = ""; // This will store our JSON data
            URL url;
            HttpURLConnection httpURLConnection = null;

            try {

                url = new URL(urls[0]);
                httpURLConnection =(HttpURLConnection) url.openConnection();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1) { //there is a data available

                    char currentChar = (char) data;

                    result += currentChar;

                    data = reader.read();

                }

                return result;      // Got the stream in the given link

            } catch (IOException e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                //Retrieving JSON data from the received stream

                timerTextView.setText(jsonObject.getString("datetime"));

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //squareImage = (ImageView) findViewById(R.id.squareImage);

        bottomImage = (ImageView) findViewById(R.id.bottomImage);

        timerTextView = (TextView) findViewById(R.id.timerTextView);

        bottomImage.animate().translationYBy(450f);


        // On touch listener for square to be dragged and bottom box to appear on the screen

        timerTextView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                movedOnce = true;

                //ACTION_DOWN is the event when button is clicked or square is touched
                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    float oldXvalue = event.getX();
                    float oldYvalue = event.getY();

                    squareMoved = true;
                    //Log.i("Movement", "Action Down " + view.getX() + "," + view.getY());

                }else if (event.getAction() == MotionEvent.ACTION_MOVE){

                    //Log.i("Movement", "X " + view.getX() + " Y " + view.getY());

                    params = new LayoutParams((int)view.getWidth(), (int) view.getHeight(),(int)(event.getRawX() - (view.getWidth() / 2)), (int)(event.getRawY() - (view.getHeight())));
                    view.setLayoutParams(params);

                    newValueOfSquareY = view.getY();

                }

                /**
                 *
                 * ACTION_UP is an event when button or touch is released from the square
                 * It must check whether the 25% or more portion is in the bottom drawer
                 * if not then it should go back to original position and drawer must go down
                 * if it is then it should locate itself to the center of the bottom drawer and drawer does not go down
                 *
                */
                if(event.getAction() == MotionEvent.ACTION_UP) {


                    if (newValueOfSquareY < 650.0 && movedOnce) {

                        movedOnce = false;
                        params = new LayoutParams((int) view.getWidth(), (int) view.getHeight(), 250, 460);
                        bottomImage.animate().translationYBy(450f);
                        view.setLayoutParams(params);

                    } else {

                        movedOnce = false;
                        params = new LayoutParams((int) view.getWidth(), (int) view.getHeight(), 250, 1035);
                        view.setLayoutParams(params);

                    }
                }

                if(squareMoved && movedOnce) {

                    bottomImage.animate().translationYBy(-450f);
                    squareMoved = false;

                }

                return true;

            }
        });

        // To rotate the square and increament timer

        handlerSecond = new Handler();

        runSecond =new Runnable() {
            @Override
            public void run() {

                // This will run every socond to rotate the Square

                timerTextView.animate().rotationBy(360f);
                handlerSecond.postDelayed(this, 1000);

            }
        };

        handlerSecond.post(runSecond);

        handlerMiliSecond = new Handler();

        runMiliSecond =new Runnable() {
            @Override
            public void run() {

                // This will run every Milisocond to display the time

                DownloadTask task = new DownloadTask();
                task.execute("https://dateandtimeasjson.appspot.com/");
                handlerMiliSecond.postDelayed(this, 1);

            }
        };

        handlerMiliSecond.post(runMiliSecond);

    }


}
