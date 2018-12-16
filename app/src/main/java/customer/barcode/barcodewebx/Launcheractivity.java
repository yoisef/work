package customer.barcode.barcodewebx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Launcheractivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcheractivity);


        Handler myhandler=new Handler();
        myhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("logintoken", Context.MODE_PRIVATE);

                String tokencondition= prefs.getString("token","default");


                if (tokencondition.equals("default"))
                {
                    startActivity(new Intent(Launcheractivity.this,Login_activity.class));
                }
       else
                {
                    startActivity(new Intent(Launcheractivity.this,MainActivity.class));

                }

                finish();

            }
        },3000);
    }


    }

