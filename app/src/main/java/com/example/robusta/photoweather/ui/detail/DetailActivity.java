package com.example.robusta.photoweather.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.robusta.photoweather.R;
import com.example.robusta.photoweather.local.LocalDataStore;
import com.example.robusta.photoweather.model.Picture;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.robusta.photoweather.util.Consts.EXTRA_PICTURE_ID;

public class DetailActivity extends AppCompatActivity {

    private String pictureId;
    private LocalDataStore localDataStore;
    private Picture currentPicture;

    @BindView(R.id.pictureImg)
    ImageView pictureImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        pictureId = getIntent().getStringExtra(EXTRA_PICTURE_ID);

        localDataStore = new LocalDataStore(this);
        localDataStore.loadPicture(pictureId, new LocalDataStore.LoadItemCallback() {
            @Override
            public void onItemLoaded(Picture picture) {
                currentPicture = picture;
                Picasso.get().load(new File(picture.getImageFilePath())).into(pictureImg);
            }
        });
    }

    @OnClick(R.id.shareFab)
    public void onShareClicked() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(currentPicture.getImageFilePath()));
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share picture"));
    }
}
