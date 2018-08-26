package com.example.robusta.photoweather.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robusta.photoweather.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.view.CameraView;

import static com.example.robusta.photoweather.util.BitmapUtil.createBitmapFromView;
import static com.example.robusta.photoweather.util.BitmapUtil.overlay;
import static com.example.robusta.photoweather.util.BitmapUtil.rotateBitmap;
import static io.fotoapparat.log.LoggersKt.fileLogger;
import static io.fotoapparat.log.LoggersKt.logcat;
import static io.fotoapparat.log.LoggersKt.loggers;
import static io.fotoapparat.result.transformer.ResolutionTransformersKt.scaled;
import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.FlashSelectorsKt.autoFlash;
import static io.fotoapparat.selector.FlashSelectorsKt.autoRedEye;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;
import static io.fotoapparat.selector.FocusModeSelectorsKt.autoFocus;
import static io.fotoapparat.selector.FocusModeSelectorsKt.continuousFocusPicture;
import static io.fotoapparat.selector.FocusModeSelectorsKt.fixed;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;
import static io.fotoapparat.selector.SelectorsKt.firstAvailable;

public class CameraActivity extends AppCompatActivity implements CameraContract.View {

    private static final String TAG = "CameraActivity";

    private CameraPresenter presenter;
    @BindView(R.id.cameraView)
    CameraView cameraView;
    @BindView(R.id.capture)
    FloatingActionButton captureFab;

    @BindView(R.id.previewImg)
    ImageView previewImg;
    @BindView(R.id.addWeatherBtn)
    Button addWeatherBtn;

    @BindView(R.id.weatherCard)
    ConstraintLayout weatherCard;
    @BindView(R.id.temperatureTv)
    TextView temperatureTv;
    @BindView(R.id.summaryTv)
    TextView summaryTv;

    @BindView(R.id.savePictureBtn)
    Button savePictureBtn;
    @BindView(R.id.cancelPictureBtn)
    Button cancelPictureBtn;

    private Fotoapparat fotoapparat;

    private Bitmap picture;
    private Bitmap weatherPicture;

    private CameraConfiguration cameraConfiguration = CameraConfiguration
            .builder()
            .photoResolution(standardRatio(
                    highestResolution()
            ))
            .focusMode(firstAvailable(
                    continuousFocusPicture(),
                    autoFocus(),
                    fixed()
            ))
            .flash(firstAvailable(
                    autoRedEye(),
                    autoFlash(),
                    torch(),
                    off()
            ))
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        // create presenter
        presenter = new CameraPresenter(this);

        fotoapparat = createFotoapparat();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
        fotoapparat.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stop();
        fotoapparat.stop();
    }


    private Fotoapparat createFotoapparat() {
        return Fotoapparat
                .with(this)
                .into(cameraView)
                .previewScaleType(ScaleType.CenterCrop)
                .lensPosition(back())
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .cameraErrorCallback(new CameraErrorListener() {
                    @Override
                    public void onError(@NotNull CameraException e) {
                        Toast.makeText(CameraActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .build();
    }

    @OnClick(R.id.capture)
    public void onCaptureClicked() {
        Log.d(TAG, "[onCaptureClicked]");
        takePicture();
    }

    private void takePicture() {
        PhotoResult photoResult = fotoapparat.takePicture();

        photoResult
                .toBitmap(scaled(0.5f))
                .whenDone(new WhenDoneListener<BitmapPhoto>() {
                    @Override
                    public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
                        if (bitmapPhoto == null) {
                            Log.e(TAG, "Couldn't capture photo.");
                            return;
                        }

                        showPreview();
                        picture = rotateBitmap(bitmapPhoto.bitmap, -bitmapPhoto.rotationDegrees);
                        previewImg.setImageBitmap(picture);
                        Log.d(TAG, "[takePicture] rotationDegree" + bitmapPhoto.rotationDegrees);
                    }
                });
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void showWeatherCard(String temperature, String summary) {
        weatherCard.setVisibility(View.VISIBLE);
        temperatureTv.setText(temperature);
        summaryTv.setText(summary);

        Bitmap card = createBitmapFromView(weatherCard);
        weatherPicture = overlay(picture, card);
        previewImg.setImageBitmap(weatherPicture);
        weatherCard.setVisibility(View.GONE);

        addWeatherBtn.setVisibility(View.GONE);
        savePictureBtn.setVisibility(View.VISIBLE);
        cancelPictureBtn.setVisibility(View.VISIBLE);
    }

    private void showPreview() {
        captureFab.setVisibility(View.GONE);

        previewImg.setVisibility(View.VISIBLE);
        addWeatherBtn.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.addWeatherBtn)
    public void addWeatherClicked() {
        presenter.addWeather();
    }

    @OnClick(R.id.savePictureBtn)
    public void savePictureClicked() {
        presenter.savePicture(weatherPicture);
    }

    @OnClick(R.id.cancelPictureBtn)
    public void cancelPictureClicked() {
        onBackPressed();
    }
}
