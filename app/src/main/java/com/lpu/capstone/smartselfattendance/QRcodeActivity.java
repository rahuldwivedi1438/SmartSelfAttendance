package com.lpu.capstone.smartselfattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRcodeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView qrImage;
    private String data;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        qrImage = findViewById(R.id.qr_image);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Attendance Qr Code");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data = getIntent().getStringExtra("otp");
        createQRCode();
    }

    private void createQRCode() {
        WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width<height ? width:height;
        smallerDimension = smallerDimension/3*4;
        qrgEncoder = new QRGEncoder(data,null , QRGContents.Type.TEXT,smallerDimension);

        try{
            bitmap = qrgEncoder.encodeAsBitmap();
            qrImage.setImageBitmap(bitmap);
        }
        catch (WriterException e){
            e.printStackTrace();
        }
    }

    public void doneClicked(View view)
    {
        setResult(RESULT_OK,new Intent());
        finish();
    }
}
