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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.FotoapparatSwitcher;
import io.fotoapparat.hardware.provider.CameraProviders;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.update.UpdateRequest;
import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

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
    private boolean isWaitingSave = false;
    private CameraView cameraView;
    private final String imagePrefix = "Scontrino_";
    private final String imageExtension = ".jpg";
    private FotoapparatSwitcher fotoapparatSwitcher;
    private Fotoapparat fotoapparat;
    private PhotoResult photoResult;

    /**
     * Creation instance
     * @author See credits
     * @author M
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_acc);
        PhotoItem lastPhotoItem;
        if (getIntent().hasExtra("PhotoItem"))
            lastPhotoItem = (PhotoItem)getIntent().getSerializableExtra("PhotoItem");
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

    /**
     * Initialize Fotoapparat and FotoapparatSwitcher
     * @author See credits
     */
    private void setupFotoapparat() {
        fotoapparat = createFotoapparat();
        fotoapparatSwitcher = FotoapparatSwitcher.withDefault(fotoapparat);
    }

    /**
     * Enable/disable flash
     * @author See credits
     */
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

    /**
     * Force autofocus when screen clicked
     * @author See credits
     */
    private void focusOnViewClick() {
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotoapparatSwitcher.getCurrentFotoapparat().autoFocus();
            }
        });
    }

    /**
     * Take picture on button clicked
     * @author See credits
     */
    private void takePictureOnButtonClick() {
        ImageButton shutterButton = (ImageButton) findViewById(R.id.CameraButton01);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    /**
     * Initialize fotoapparat
     * @author See credits
     * @author M
     */
    private Fotoapparat createFotoapparat() {
        return Fotoapparat
               .with(this)
               .cameraProvider(CameraProviders.v1())  //API2 have issues with flash
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
               .logger(loggers(
                           logcat(),
                           fileLogger(this)
                           ))
               /*.cameraErrorCallback(new CameraErrorCallback() {
                  @Override
                  public void onError(CameraException e) {
                  Toast.makeText(CameraAcc.this, e.toString(), Toast.LENGTH_LONG).show();
                  }
                  }) */
               .build();
    }

    /**
     * Take picture
     * @author See credits
     * @author M
     */
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
            isWaitingSave = true;
            permissionsHandler.requestStoragePermission();
        }
        else
            saveFile();
    }

    /**
     * Save picture
     * @author M
     */
    private void saveFile() {
        String root = Environment.getExternalStorageDirectory().toString();
        File mainDir = new File(root + "/" + ReceiptList.saveFolderName);
        boolean success = true;
        if (!mainDir.exists())
            success = mainDir.mkdirs();
        if (success)
        {
            Calendar cal = Calendar.getInstance();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(cal.getTime());
            String fname = imagePrefix + timeStamp + imageExtension;
            File file = new File(mainDir, fname);
            photoResult.saveToFile(file);
        }
        else
            Toast.makeText(this, R.string.error_saving_file, Toast.LENGTH_LONG).show();
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

    /**
     * Manage permissions
     * @author See credits
     * @author M
     */
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
            else if (requestCode == PermissionsHandler.REQUEST_STORAGE_CODE && isWaitingSave)
            {
                isWaitingSave = false;
                saveFile();
            }
        }
        else
        {
            if (requestCode == PermissionsHandler.REQUEST_STORAGE_CODE)
            {
                String text = getString(R.string.no_storage_permission) + "\n" + getString(R.string.ask_permission);
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            }
            else if (requestCode == PermissionsHandler.REQUEST_CAMERA_CODE)
            {
                String text = getString(R.string.no_camera_permission) + "\n" + getString(R.string.ask_permission);
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            }
        }
    }
}
