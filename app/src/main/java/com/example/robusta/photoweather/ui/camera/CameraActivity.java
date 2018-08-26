package com.example.robusta.photoweather.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private Fotoapparat fotoapparat;

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

        // photoResult.saveToFile(new File(getExternalFilesDir("photos"), "photo.jpg"));

        photoResult
                .toBitmap(scaled(0.25f))
                .whenDone(new WhenDoneListener<BitmapPhoto>() {
                    @Override
                    public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
                        if (bitmapPhoto == null) {
                            Log.e(TAG, "Couldn't capture photo.");
                            return;
                        }

                        showPreview();
                        Bitmap rotatedBitmap = rotateBitmap(bitmapPhoto.bitmap, -bitmapPhoto.rotationDegrees);
                        previewImg.setImageBitmap(rotatedBitmap);
                        Log.d(TAG, "[takePicture] rotationDegree" + bitmapPhoto.rotationDegrees);
                    }
                });
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    private Bitmap rotateBitmap(Bitmap source, float rotationDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegree);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, source.getWidth(), source.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
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
}
