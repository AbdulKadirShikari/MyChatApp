package com.example.mychatapp;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {
    private ImageView imageView;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        imageView=(ImageView)findViewById(R.id.image_viever);
        imageUrl = getIntent().getStringExtra("url");
        Picasso.get().load(imageUrl).into(imageView);
    }
}
