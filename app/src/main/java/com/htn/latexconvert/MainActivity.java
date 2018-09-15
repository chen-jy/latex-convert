package com.htn.latexconvert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button_go);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Do something in response to button click
                Log.v("htn", "Button pressed");

                try {
                    Bitmap b = null;



                    File f = new File("drawable/pic.jpg");
                    FileInputStream fis = new FileInputStream(f);



                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        //b = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                        throw new IOException("s");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.wtf("htn", "no");
                    }

                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(b);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.wtf("htn", "no 2");
                }
            }
        });
    }
}
