package com.example.stefandy_2201789536;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import helper.DatabaseHelper;

public class DetailActivity extends AppCompatActivity {

    DatabaseHelper db;
    private Context mContext;
    private String savedImagePath = null;
    private String alt_title,title,year,id,url;
    private TextView detail_title,detail_year,detail_id;
    private Button btn;
    private ImageView detail_thumbnail;
    RequestOptions option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mContext = getApplicationContext();
        db = new DatabaseHelper(this);
        btn = findViewById(R.id.save_button);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Movie Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
        option = new RequestOptions().centerCrop().placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_foreground);
//
        detail_title = findViewById(R.id.tv_detail_title);
        detail_year = findViewById(R.id.tv_detail_year);
        detail_id  = findViewById(R.id.tv_detail_id);
        detail_thumbnail = findViewById(R.id.iv_detail);

        Intent intent = getIntent();
        alt_title = intent.getStringExtra("Title");
        title = alt_title.replaceFirst(":","");
        year = intent.getStringExtra("Year");
        id = intent.getStringExtra("Id");
        url = intent.getStringExtra("Image");

        detail_title.setText("Title  : " + title);
        detail_year.setText("Year   : " + year);
        detail_id.setText("IMDB ID : " + id);
        Glide.with(this).load(url).apply(option).into(detail_thumbnail);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.checkMovie(title) == false) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(url)
                            .into(new CustomTarget<Bitmap>(480, 720) {

                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    saveImage(resource);
                                    db.AddMovie(title, year, id, savedImagePath);
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    mContext.startActivity(intent);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }
                else
                {
                    Toast.makeText(mContext,"Movie already saved",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String saveImage(Bitmap image) {

        String imageFileName = title + ".jpeg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/MOVIE");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG,100,fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            galleryAddPic(savedImagePath);
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onBackPressed() {

    }
}