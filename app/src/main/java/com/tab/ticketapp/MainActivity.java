package com.tab.ticketapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    SurfaceView mCameraView;
    TextRecognizer textRecognizer;
    TextView mTextView, nozzle_tv, site_tv, price_tv, plate_result;
    ImageView save;
    CameraSource mCameraSource;
    private static final String TAG = "CarscanActivity";

    private ImageButton next_to_card;
    public TextView validText, nbrText, lastText;

    Bitmap bitmap;
    String result, check;

    final int RequestCameraPermissionID = 1001;
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraView = (SurfaceView) findViewById(R.id.surfaceView);
        mTextView = (TextView) findViewById(R.id.text_view);
        validText = findViewById(R.id.validview);
        nbrText = findViewById(R.id.nbrview);
        lastText = findViewById(R.id.lastview);
        plate_result = findViewById(R.id.final_result);



        textRecognizer= new TextRecognizer.Builder(this)
                .build();


        mCameraSource = new CameraSource.Builder(this, textRecognizer)
                .setRequestedPreviewSize(1280, 1024)
                .build();

        mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.CAMERA},
                            RequestCameraPermissionID);
                    return;
                }
                try {
                    mCameraSource.start(mCameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0 ){

                    mTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i=0;i<items.size();i++){
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            mTextView.setText(stringBuilder.toString());
//                            validPlate(mTextView.getText().toString());
                            result = mTextView.getText().toString().trim();

                            if (result.length() == 9){
                                plate_result.setText(result);

                                String str = plate_result.getText().toString();
                                StringBuffer alpha = new StringBuffer();
                                StringBuffer num = new StringBuffer();
                                StringBuffer special = new StringBuffer();
                                StringBuffer lst = new StringBuffer();

//     for (int i=0; i<str.length(); i++)
//     {
//         if (Character.isDigit(str.charAt(i)))
//             num.append(str.charAt(i));
//         else if(Character.isAlphabetic(str.charAt(i)))
//             alpha.append(str.charAt(i));
//         else
//             special.append(str.charAt(i));
//     }

                                if (Character.isAlphabetic(str.charAt(0)) || Character.isAlphabetic(str.charAt(1)) || Character.isAlphabetic(str.charAt(2))) {
                                    for (int i = 0; i < 3; i++) {
                                        alpha.append(str.charAt(i));
                                    }
                                    validText.setText(alpha);
                                }
                                if (Character.isDigit(str.charAt(3)) || Character.isDigit(str.charAt(4))
                                        || Character.isDigit(str.charAt(5))|| Character.isDigit(str.charAt(6))
                                        || Character.isDigit(str.charAt(7))|| Character.isDigit(str.charAt(8))
                                        || Character.isDigit(str.charAt(9))|| Character.isDigit(str.charAt(10))) {

                                    for (int j = 3; j < 10; j++) {
                                        num.append(str.charAt(j));
                                    }

                                    char[] letters = new char[]{'G', 'Q','o','O'};
                                    StringBuilder buf = new StringBuilder(num);
                                    IntStream.range(0, buf.length()).forEach(i -> {
                                        for (char c : letters)
                                            if (buf.charAt(i) == c)
                                                buf.replace(i, i + 1, "0");
                                    });
                                    String s = buf.toString();
                                    num.append(s);

                                    nbrText.setText(s);
                                }
//                                if (Character.isAlphabetic(str.charAt(8))) {
//                                    for (int j = 8; j <= 8; j++) {
//                                        lst.append(str.charAt(j));
//                                    }
//                                    lastText.setText(lst);
//                                }

                                checkValid();
                            }
                            else {
                                plate_result.setText("");
                                validText.setText("");
                                nbrText.setText("");
                                lastText.setText("");
                            }

                        }
                    });
                }

            }
        });


    }

    public void checkValid(){
        if (validText.getText().toString() != "" ||
                validText.getText().toString()!= null||
                nbrText.getText().toString() != ""|| nbrText.getText().toString()!= null){
            validPlate();
        }
    }
    public void splitString() {

        if (plate_result.getText().toString()!=""|| plate_result.getText().toString()!=null){
            String str =plate_result.getText().toString();
            StringBuffer alpha = new StringBuffer();
            StringBuffer num = new StringBuffer();
            StringBuffer special = new StringBuffer();
            StringBuffer lst = new StringBuffer();

//     for (int i=0; i<str.length(); i++)
//     {
//         if (Character.isDigit(str.charAt(i)))
//             num.append(str.charAt(i));
//         else if(Character.isAlphabetic(str.charAt(i)))
//             alpha.append(str.charAt(i));
//         else
//             special.append(str.charAt(i));
//     }

            if (Character.isAlphabetic(str.charAt(0)) || Character.isAlphabetic(str.charAt(1)) || Character.isAlphabetic(str.charAt(2))) {
                for (int i = 0; i < 3; i++) {
                    alpha.append(str.charAt(i));
                }
                validText.setText(alpha);
            }
//        if (Character.isDigit(str.charAt(4)) || Character.isDigit(str.charAt(5)) || Character.isDigit(str.charAt(6))) {
//
//            for (int j = 4; j < 7; j++) {
//                num.append(str.charAt(j));
//            }
//
//            char[] letters = new char[]{'G', 'o','O'};
//                StringBuilder buf = new StringBuilder(num);
//                IntStream.range(0, buf.length()).forEach(i -> {
//                    for (char c : letters)
//                        if (buf.charAt(i) == c)
//                            buf.replace(i, i + 1, "0");
//                });
//                String s = buf.toString();
//                num.append(s);
//
//            nbrText.setText(s);
//        }
//        if (Character.isAlphabetic(str.charAt(8))) {
//            for (int j = 8; j <= 8; j++) {
//                lst.append(str.charAt(j));
//            }
//            lastText.setText(lst);
//        }

        } else {
            Toast.makeText(this, "Plate is Required", Toast.LENGTH_SHORT).show();
        }
    }

    public static String replaceCharacter(String w, char b, String v) {
        String result = "";
        for (int i = 0; i < w.length(); i++) {
            if (w.charAt(i) == b) {
                result += v;
            } else {
                result += w.charAt(i);
            }
        }
        return result;
    }
    public void validPlate() {
//        splitString(str);
        String xx = validText.getText().toString().trim()+ nbrText.getText().toString().trim()+ lastText.getText().toString().trim();

        if (!xx.isEmpty()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Plate No: ");
            builder.setMessage(xx);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Intent verifyIntent = new Intent(getApplicationContext(), CheckCarplateActivity.class);
//                    verifyIntent.putExtra("Check Car plate", xx);
//                    verifyIntent.putExtra("Site ID", site_tv.getText().toString().trim());
//                    verifyIntent.putExtra("Price", price_tv.getText().toString().trim());
//                    verifyIntent.putExtra("Nozzle ID", nozzle_tv.getText().toString().trim());
//                    startActivity(verifyIntent);
////                    nextToCard();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void nextToCard(){
//        String plate = validText.getText().toString().trim() + nbrText.getText().toString().trim() + lastText.getText().toString().trim();
//        Intent verifyIntent = new Intent(getApplicationContext(), CheckCarplateActivity.class);
//        verifyIntent.putExtra("Check Car plate", plate);
//        verifyIntent.putExtra("Site ID", site_tv.getText().toString().trim());
//        verifyIntent.putExtra("Price", price_tv.getText().toString().trim());
//        verifyIntent.putExtra("Nozzle ID", nozzle_tv.getText().toString().trim());
//        startActivity(verifyIntent);
    }



}
