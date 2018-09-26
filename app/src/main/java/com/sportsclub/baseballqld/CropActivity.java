package com.sportsclub.baseballqld;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.isseiaoki.simplecropview.CropImageView;

public class CropActivity extends AppCompatActivity {

    public interface CropProtocol
    {
        void didCropBitmap(Bitmap b);
        Bitmap getInputBitmap();
    }

    public static CropActivity.CropProtocol del;

    private CropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);



        cropImageView = findViewById(R.id.cropImageView);
        // final ImageView croppedImageView = (ImageView)findViewById(R.id.croppedImageView);
        cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);

        // Set image for cropping
        //cropImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon));

        cropImageView.setImageBitmap(del.getInputBitmap());


        Button cropButton = findViewById(R.id.done_button);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get cropped image, and show result.
                cropImageView.setImageBitmap(cropImageView.getCroppedBitmap());
                doneAction();
            }
        });

        Button rotateButton = findViewById(R.id.rotate_button);
        rotateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

    }

    private void doneAction()
    {
        del.didCropBitmap(cropImageView.getCroppedBitmap());
        del = null;
        this.finish();

    }

}

