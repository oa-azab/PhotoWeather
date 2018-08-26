package com.example.robusta.photoweather.ui.main;

import android.Manifest;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.robusta.photoweather.R;
import com.example.robusta.photoweather.ui.main.adapter.PictureAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private static final String TAG = "MainActivity";

    private MainPresenter presenter;

    @BindView(R.id.requestPermissionsBtn)
    Button requestPermissionsBtn;
    @BindView(R.id.takePictureFab)
    FloatingActionButton takePictureFab;

    @BindView(R.id.emptyStateTv)
    TextView emptyStateTv;
    @BindView(R.id.picturesRv)
    RecyclerView picturesRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MainPresenter(this);

        picturesRv.setLayoutManager(new GridLayoutManager(this, 2));
        picturesRv.setHasFixedSize(true);
    }

    private void checkPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Log.d(TAG, "[checkPermissions] PermissionsGranted");
                    presenter.onPermissionsGranted();
                } else {
                    Log.d(TAG, "[checkPermissions] PermissionsDenied");
                    presenter.onPermissionsDenied();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
        checkPermissions();
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void showRequestPermissions() {
        takePictureFab.setVisibility(View.GONE);
        requestPermissionsBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMainView() {
        takePictureFab.setVisibility(View.VISIBLE);
        requestPermissionsBtn.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyState(boolean show) {
        if (show) {
            emptyStateTv.setVisibility(View.VISIBLE);
            picturesRv.setVisibility(View.GONE);
        } else {
            emptyStateTv.setVisibility(View.GONE);
            picturesRv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setAdapter(PictureAdapter adapter) {
        picturesRv.setAdapter(adapter);
    }

    @OnClick(R.id.takePictureFab)
    public void onTakePictureClicked() {
        presenter.onTakePictureClicked();
    }

    @OnClick(R.id.requestPermissionsBtn)
    public void requestPermissionsClicked() {
        checkPermissions();
    }
}
