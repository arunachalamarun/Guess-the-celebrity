package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> celebUrl = new ArrayList<String>();
    ArrayList<String> celebName = new ArrayList<String>();
    Bitmap image1;
    ImageView celebImage;
    String[] answers = new String[4];
    Button b1, b2, b3, b4;
    int correctAnswer;
    int chose = 0;

    /*
    Class to extract the link from the source page
    ex:<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
<meta name="application-name" content="JIRA" data-name="jira" data-version="7.6.14"><meta name="ajs-viewissue-use-history-api" content="false">
<link rel="shortcut icon" href="/s/-jwv2r9/76016/b50fc5a931dfb9ddf8f724bf45be3e79/_/favicon.ico">
To extract this link from the website
     */
    public class Download extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            URL url;
            String result = "";

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result = result + current;
                    data = reader.read();

                }
                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /*
    Class to dwonload the image from the url
    return image;
     */
    public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                return image;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public void onClick(View view) {
        if (view.getTag().toString().equals(Integer.toString(correctAnswer))) {
            Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
            createRandom();
        } else {
            Toast.makeText(this, "INCORRECT it was" + celebName.get(chose), Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        Download dn = new Download();
        String result = null;
        try {
            result = dn.execute("http://www.posh24.se/kandisar").get();
            String[] split = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(split[0]);
            while (m.find()) {
                celebUrl.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(split[0]);
            while (m.find()) {
                celebName.add(m.group(1));
            }
            b1 = (Button) findViewById(R.id.button1);
            b2 = (Button) findViewById(R.id.button2);
            b3 = (Button) findViewById(R.id.button3);
            b4 = (Button) findViewById(R.id.button4);

            createRandom();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }//end of oncreate


    /*
    Fuction used to generate the random image each time
    and generate the correct answer and the wrong answer each time
     */
    public void createRandom() {
        try {
            Random rand = new Random();
            chose = rand.nextInt(celebName.size());//random number to get the image from the arraylist

            celebImage = (ImageView) findViewById(R.id.imageView1);
            ImageLoader im = new ImageLoader();
            String url = celebUrl.get(chose);
            System.out.println(url);
            image1 = im.execute(url).get();
            celebImage.setImageBitmap(image1);
            correctAnswer = rand.nextInt(4);// fixes the correct anser
            int incorrectAnswers = 0;
            for (int i = 0; i < 4; i++) {
                if (i == correctAnswer) {
                    answers[i] = celebName.get(chose);
                } else {
                    incorrectAnswers = rand.nextInt(celebUrl.size());

                    while (incorrectAnswers == correctAnswer) {
                        incorrectAnswers = rand.nextInt(celebUrl.size());
                    }//end of while loop
                    answers[i] = celebName.get(incorrectAnswers);
                }//end of if statement
            }//end of for loop

            b1.setText(answers[0]);
            b2.setText(answers[1]);
            b3.setText(answers[2]);
            b4.setText(answers[3]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }//end of the method



}
