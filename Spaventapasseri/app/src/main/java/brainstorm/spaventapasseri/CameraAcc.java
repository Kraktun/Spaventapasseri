package brainstorm.spaventapasseri;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Random;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.FotoapparatSwitcher;
import io.fotoapparat.error.CameraErrorCallback;
import io.fotoapparat.hardware.CameraException;
import io.fotoapparat.hardware.provider.CameraProviders;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.update.UpdateRequest;
import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;

import static io.fotoapparat.log.Loggers.fileLogger;
import static io.fotoapparat.log.Loggers.logcat;
import static io.fotoapparat.log.Loggers.loggers;
import static io.fotoapparat.parameter.selector.AspectRatioSelectors.standardRatio;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoRedEye;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.torch;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.autoFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.continuousFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.fixed;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.PreviewFpsRangeSelectors.rangeWithHighestFps;
import static io.fotoapparat.parameter.selector.Selectors.firstAvailable;
import static io.fotoapparat.parameter.selector.SensorSensitivitySelectors.highestSensorSensitivity;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;
import static io.fotoapparat.result.transformer.SizeTransformers.scaled;

/**
 * Inspired by https://github.com/Fotoapparat/Fotoapparat/blob/master/sample/src/main/java/io/fotoapparat/sample/MainActivity.java
 */

public class CameraAcc extends AppCompatActivity {

    private final PermissionsHandler permissionsHandler = new PermissionsHandler(this);
    private boolean hasCameraPermission;
    private CameraView cameraView;

    private FotoapparatSwitcher fotoapparatSwitcher;
    private Fotoapparat fotoapparat;
    private PhotoResult photoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_acc);

        cameraView = (CameraView) findViewById(R.id.camera_view);
        hasCameraPermission = permissionsHandler.hasCameraPermission();

        if (hasCameraPermission)
        {
            cameraView.setVisibility(View.VISIBLE);
        }
        else
        {
            permissionsHandler.requestCameraPermission();
        }

        setupFotoapparat();
        takePictureOnButtonClick();
        focusOnViewClick();
        toggleTorchOnSwitch();
    }

    private void setupFotoapparat() {
        fotoapparat = createFotoapparat();
        fotoapparatSwitcher = FotoapparatSwitcher.withDefault(fotoapparat);
    }

    private void toggleTorchOnSwitch() {
        SwitchCompat torchSwitch = (SwitchCompat) findViewById(R.id.torchSwitch);

        torchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fotoapparatSwitcher
                .getCurrentFotoapparat()
                .updateParameters(
                    UpdateRequest.builder()
                    .flash(
                        isChecked
                        ? torch()
                        : off()
                        )
                    .build()
                    );
            }
        });
    }

    private void focusOnViewClick() {
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotoapparatSwitcher.getCurrentFotoapparat().autoFocus();
            }
        });
    }

    private void takePictureOnButtonClick() {
        ImageButton shutterButton = (ImageButton) findViewById(R.id.CameraButton01);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private Fotoapparat createFotoapparat() {
        return Fotoapparat
               .with(this)
               .cameraProvider(CameraProviders.v2(this))  // Min SDK è 21, quindi si va di api2
               .into(cameraView)
               .previewScaleType(ScaleType.CENTER_CROP)
               .photoSize(standardRatio(biggestSize()))
               .lensPosition(back())
               .focusMode(firstAvailable(
                              continuousFocus(),
                              autoFocus(),
                              fixed()
                              ))
               .flash(firstAvailable(
                          autoRedEye(),
                          autoFlash(),
                          torch(),
                          off()
                          ))
               .previewFpsRange(rangeWithHighestFps())
               .sensorSensitivity(highestSensorSensitivity())
               .frameProcessor(new SampleFrameProcessor())
               .logger(loggers(
                           logcat(),
                           fileLogger(this)
                           ))
               .cameraErrorCallback(new CameraErrorCallback() {
            @Override
            public void onError(CameraException e) {
                Toast.makeText(CameraAcc.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        })
               .build();
    }

    private void takePicture() {
        this.photoResult = fotoapparatSwitcher.getCurrentFotoapparat().takePicture();
        photoResult
        .toBitmap(scaled(0.25f))
        .whenAvailable(new PendingResult.Callback<BitmapPhoto>() {
            @Override
            public void onResult(BitmapPhoto result) {
                ImageView imageView = (ImageView) findViewById(R.id.result);

                imageView.setImageBitmap(result.bitmap);
                imageView.setRotation(-result.rotationDegrees);
            }
        });
        if (!permissionsHandler.hasStoragePermission())
        {
            permissionsHandler.requestStoragePermission();
        }
        saveFile();

    }

    //Prova con salvataggio file casuale
    private void saveFile() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File (myDir, fname);
        photoResult.saveToFile(file);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasCameraPermission)
        {
            fotoapparatSwitcher.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (hasCameraPermission)
        {
            fotoapparatSwitcher.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsHandler.resultGranted(requestCode, permissions, grantResults))
        {
            if (requestCode == PermissionsHandler.REQUEST_CAMERA_CODE)
            {
                fotoapparatSwitcher.start();
                cameraView.setVisibility(View.VISIBLE);
            }
            else if (requestCode == PermissionsHandler.REQUEST_STORAGE_CODE)
            {
                saveFile();
            }
        }
    }

    private class SampleFrameProcessor implements FrameProcessor {

        @Override
        public void processFrame(Frame frame) {
            // Perform frame processing, if needed
        }

    }

}
