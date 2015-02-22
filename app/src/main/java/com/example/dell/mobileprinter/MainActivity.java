package com.example.dell.mobileprinter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    Bitmap bmp;
    Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;
    private ImageView imgView;
    private Button loadPicture;
    private Button drawPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) this.findViewById(R.id.imgView);

    }

    public void loadImagefromGallery(View view) {

    }

    public void drawImage(View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri path = Uri.parse("android.resource://com.example.dell.mobileprinter/" + R.drawable.white);

        intent.setData(path);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //Once an image is selected, retrieve the data
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                //Get the cursor and move it to the first row
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);

                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            }
            else if(requestCode == 2)
            {
                Uri imageFileUri = data.getData();
                try {
                    BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                    bmpFactoryOptions.inJustDecodeBounds = true;
                    bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                            imageFileUri), null, bmpFactoryOptions);

                    bmpFactoryOptions.inJustDecodeBounds = false;
                    bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                            imageFileUri), null, bmpFactoryOptions);

                    alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
                            .getHeight(), bmp.getConfig());
                    canvas = new Canvas(alteredBitmap);
                    paint = new Paint();
                    paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(5);
                    matrix = new Matrix();
                    canvas.drawBitmap(bmp, matrix, paint);

                    imgView.setImageBitmap(alteredBitmap);
                    imgView.setOnTouchListener(this);
                } catch (Exception e) {
                    Log.v("ERROR", e.toString());
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    public boolean onTouch(View v, MotionEvent event)
    {
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
                imgView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                imgView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        if (v == loadPicture)
        {
            // Create the intent
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // Start intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        } else if (v == drawPicture)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri path = Uri.parse("android.resource://com.example.dell.mobileprinter/" + R.drawable.white);

            intent.setData(path);
            startActivityForResult(intent, 2);

            /*

            if (alteredBitmap != null) {
                ContentValues contentValues = new ContentValues(3);
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Draw On Me");

                Uri imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
                    alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 90, imageFileOS);
                    Toast t = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
                    t.show();

                } catch (Exception e) {
                    Log.v("EXCEPTION", e.getMessage());
                }
            }
            */
        }

    }
}