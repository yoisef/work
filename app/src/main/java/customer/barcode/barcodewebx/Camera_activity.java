package customer.barcode.barcodewebx;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class Camera_activity extends AppCompatActivity {
    private BarcodeDetector detector;
    private TextView resultscan;

    private SurfaceView cameraView;
    private customer.barcode.barcodewebx.CameraSource cameraSource;
    CameraSource mycamerasource;
    private android.app.AlertDialog.Builder builder;
    private android.app.AlertDialog alertDialog;
    private Button cancel;
    private FrameLayout myframe;
    private MediaPlayer ring;
    private View leaser;
    private android.hardware.Camera camera = null;
    boolean flashmode = false;
    Boolean result;
    LinearLayout changelay;
  String detect= null;
   private Switch myswitch;
   String conditionn,finalcon;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_activity);

        leaser=findViewById(R.id.leaserline);





        ring = MediaPlayer.create(Camera_activity.this, R.raw.notif);

        resultscan = (TextView) findViewById(R.id.resultmyscan);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        cancel = (Button) findViewById(R.id.backtomain);
        myframe = (FrameLayout) findViewById(R.id.myframecamera);
        changelay=findViewById(R.id.mydetect);
        myswitch=findViewById(R.id.switchtorch);








        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {


           myswitch.post(new Runnable() {
               @Override
               public void run() {

                   myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                       public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                           // do something, the isChecked will be
                           if (isChecked) {


                               conditionn ="true";
                               SharedPreferences prefs = getSharedPreferences("condition", Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor=prefs.edit();
                               editor.putString("mycon", conditionn);
                               editor.apply();
                               restart();

                           }
                           else {

                               conditionn ="false";
                               SharedPreferences prefs = getSharedPreferences("condition", Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor=prefs.edit();
                               editor.putString("mycon", conditionn);
                               editor.apply();
                               restart();

                           }





                       }
                   });



               }
           });

           }



       else{
           myswitch.setVisibility(View.GONE);
       }





        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
            }
        });






    }

    @Override
    protected void onResume() {
        super.onResume();


        SharedPreferences prefs = getSharedPreferences("condition", Context.MODE_PRIVATE);
        String conde= prefs.getString("mycon","false");

        if (conde.equals("true"))
        {
           //myswitch.setOnCheckedChangeListener (null);
            myswitch.setChecked(true);
            instalcamerawithflash();
        }
        else{
            instalcamerawirhoutflash();
        }
    }


    public void instalcamerawithflash()
 {

     detector =
             new BarcodeDetector.Builder(getApplicationContext())
                     .setBarcodeFormats(0)
                     .build();
     if (!detector.isOperational()) {
         //  txtView.setText("Could not set up the detector!");
         Toast.makeText(Camera_activity.this, "Could not set up the detector!", Toast.LENGTH_LONG).show();

         return;
     }







     cameraSource = new customer.barcode.barcodewebx.CameraSource
             .Builder(Camera_activity.this, detector)
             .setRequestedPreviewSize(640, 480)
             .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
             .setscanmode( Camera.Parameters.SCENE_MODE_BARCODE)
             .setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
             .build();




     cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
         @Override
         public void surfaceCreated(SurfaceHolder holder) {

             try {


                 if (ActivityCompat.checkSelfPermission(Camera_activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                     // TODO: Consider calling
                     //    ActivityCompat#requestPermissions
                     // here to request the missing permissions, and then overriding
                     //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                     //                                          int[] grantResults)
                     // to handle the case where the user grants the permission. See the documentation
                     // for ActivityCompat#requestPermissions for more details.
                     return;
                 }
                 cameraSource.start(cameraView.getHolder());



             } catch (IOException ie) {
                 Log.e("CAMERA SOURCE", ie.getMessage());
             }
         }

         @Override
         public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
         }

         @Override
         public void surfaceDestroyed(SurfaceHolder holder)
         {

             cameraSource.stop();
           //  cameraSource.release();
         }
     });

     detector.setProcessor(new Detector.Processor<Barcode>() {
         @Override
         public void release() {
         }

         @Override
         public void receiveDetections(Detector.Detections<Barcode> detections) {

             final SparseArray<Barcode> barcodes = detections.getDetectedItems();


             if (barcodes.size() != 0) {
                 changelay.post(new Runnable() {
                     @Override
                     public void run() {

                         changelay.setVisibility(View.VISIBLE);
                         leaser.setVisibility(View.GONE);
                         cameraView.setBackground(getResources().getDrawable(R.drawable.camerashapedark));
                         ring.start();
                         Intent myintent=new Intent();
                         myintent.putExtra("open", barcodes.valueAt(0).displayValue);
                         setResult(CommonStatusCodes.SUCCESS,myintent);
                         finish();

                     }
                 });




             }
         }
     });



 }

 public void instalcamerawirhoutflash()
 {

     detector =
             new BarcodeDetector.Builder(getApplicationContext())
                     .setBarcodeFormats(0)
                     .build();
     if (!detector.isOperational()) {
         //  txtView.setText("Could not set up the detector!");
         Toast.makeText(Camera_activity.this, "Could not set up the detector!", Toast.LENGTH_LONG).show();

         return;
     }







     cameraSource = new customer.barcode.barcodewebx.CameraSource
             .Builder(Camera_activity.this, detector)
             .setRequestedPreviewSize(640, 480)
             .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
             .setscanmode(Camera.Parameters.SCENE_MODE_BARCODE)
             .build();




     cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
         @Override
         public void surfaceCreated(SurfaceHolder holder) {

             try {


                 if (ActivityCompat.checkSelfPermission(Camera_activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                     // TODO: Consider calling
                     //    ActivityCompat#requestPermissions
                     // here to request the missing permissions, and then overriding
                     //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                     //                                          int[] grantResults)
                     // to handle the case where the user grants the permission. See the documentation
                     // for ActivityCompat#requestPermissions for more details.
                     return;
                 }
                 cameraSource.start(cameraView.getHolder());

             } catch (IOException ie) {
                 Log.e("CAMERA SOURCE", ie.getMessage());
             }
         }

         @Override
         public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
         }

         @Override
         public void surfaceDestroyed(SurfaceHolder holder)
         {

             cameraSource.stop();
//             cameraSource.release();
         }
     });


     detector.setProcessor(new Detector.Processor<Barcode>() {
         @Override
         public void release() {
         }

         @Override
         public void receiveDetections(Detector.Detections<Barcode> detections) {

             final SparseArray<Barcode> barcodes = detections.getDetectedItems();


             if (barcodes.size() != 0) {
                 changelay.post(new Runnable() {
                     @Override
                     public void run() {

                         changelay.setVisibility(View.VISIBLE);
                         cameraView.setBackground(getResources().getDrawable(R.drawable.camerashapedark));
                         cameraSource.stop();
                         ring.start();
                         Intent myintent=new Intent();
                         myintent.putExtra("open", barcodes.valueAt(0).displayValue);
                         setResult(CommonStatusCodes.SUCCESS,myintent);
                         finish();

                     }
                 });




             }
         }
     });

 }





    public  void  restart()
    {
        this.recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.stop();
        cameraSource.release();
    }
}














