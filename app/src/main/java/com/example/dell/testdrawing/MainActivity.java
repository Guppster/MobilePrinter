package com.example.dell.testdrawing;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStream;

public class MainActivity extends Activity implements OnClickListener,
        OnTouchListener {

    ImageView choosenImageView;
    Button choosePicture;
    Button savePicture;

    Bitmap bmp;
    Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choosenImageView = (ImageView) this.findViewById(R.id.ChoosenImageView);
        //choosePicture = (Button) this.findViewById(R.id.ChoosePictureButton);
        savePicture = (Button) this.findViewById(R.id.SavePictureButton);

        savePicture.setOnClickListener(this);
        //choosePicture.setOnClickListener(this);
        choosenImageView.setOnTouchListener(this);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.white2);

            alteredBitmap = Bitmap.createScaledBitmap(bmp,500,500,false);
            canvas = new Canvas(alteredBitmap);
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(9);
            matrix = new Matrix();
            canvas.drawBitmap(bmp, matrix, paint);

            choosenImageView.setImageBitmap(alteredBitmap);
            choosenImageView.setOnTouchListener(this);
    }

    public void onClick(View v) {

        if (v == choosePicture) {
            Intent choosePictureIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent, 0);
        } else if (v == savePicture) {

            if (alteredBitmap != null) {
                ContentValues contentValues = new ContentValues(3);
                contentValues.put(Media.DISPLAY_NAME, "Draw On Me");

                Uri imageFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
                    alteredBitmap.compress(CompressFormat.JPEG, 90, imageFileOS);
                    Toast t = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
                    t.show();

                } catch (Exception e) {
                    Log.v("EXCEPTION", e.getMessage());
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {

        }
    }
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                choosenImageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                choosenImageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }
}