package customer.barcode.barcodewebx;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import customer.barcode.barcodewebx.Models.Product;
import customer.barcode.barcodewebx.Models.Productroot;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


   private RecyclerView myrecycle;
    private ImageView scan, barcodimg;
   private Toolbar mytoolbar;
    private Button enterbarcode;
   private android.app.AlertDialog.Builder builder,builder1;
    private android.app.AlertDialog alertDialog,alertDialog1;
    private Call<Productroot> mcall;
    private Recycleadapter mAdapter;
    private List<Product> dbList;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private productmodel mymodel;
    private TextView pricetotal;
    private LinearLayout paylinear;
    private Float total;
    private List<String> allprices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbList = new ArrayList<>();
        /**
         *
         */

        FirebaseApp.initializeApp(this);

        // request camera permission
        requestPermission();

        // intilize Ui objects
        pricetotal=findViewById(R.id.totalprice);
        paylinear=findViewById(R.id.paylayout);
        enterbarcode =  findViewById(R.id.barcodenumber);
        myrecycle =  findViewById(R.id.productrecycle);
        myrecycle.setHasFixedSize(true);
        myrecycle.setLayoutManager(new LinearLayoutManager(this));
        myrecycle.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new Recycleadapter(this);
        myrecycle.setAdapter(mAdapter);


        //set Toolbar with 2 button scancamera and about us

        mytoolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setCustomView(R.layout.cutom_action_bar);
        View view = getSupportActionBar().getCustomView();






        scan = view.findViewById(R.id.camerascan);
        barcodimg = view.findViewById(R.id.aboutus);

        barcodimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Aboutactivity.class));
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, Camera_activity.class);
                startActivityForResult(intent1, 0);

            }
        });



        //initialize pay button

        paylinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder1 = new android.app.AlertDialog.Builder(MainActivity.this);


                View view = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.payayout, null);
             TextView transtext=view.findViewById(R.id.transfer);
                builder1.setView(view);
                alertDialog1= builder1.create();
                alertDialog1.show();
                transtext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.cancel();
                        Toast toast=Toast.makeText(MainActivity.this,"Verifiying$Transfering the cart item... ",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER,0,50);
                        toast.show();
                    }
                });

            }
        });





      //initialize Enterbarcode Button insted scan with camera

        enterbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText myedit;
                TextView ok, cancel;

                builder = new android.app.AlertDialog.Builder(MainActivity.this);

                View myview = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.layoutenterbar, null);
                myedit = myview.findViewById(R.id.barcodedittext);
                ok = myview.findViewById(R.id.okk);
                cancel = myview.findViewById(R.id.cancell);
                builder.setView(myview);
                alertDialog = builder.create();
                alertDialog.show();


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //when user press ok call request to api by barcode number to get info about barcode and display it

                        OkHttpClient.Builder builderr = new OkHttpClient.Builder();

                        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                        builderr.addInterceptor(loggingInterceptor);


                        Retrofit retrofitt = new Retrofit.Builder()
                                .baseUrl("https://www.werpx.net/api/v1/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(builderr.build())
                                .build();

                        final Endpoints myendpoints = retrofitt.create(Endpoints.class);

                        mcall = myendpoints.addbarcode(myedit.getText().toString());
                        mcall.enqueue(new Callback<Productroot>() {
                            @Override
                            public void onResponse(Call<Productroot> call, Response<Productroot> response) {

                                //push details to firebase database

                                if (response.isSuccessful()) {
                                    if (response.body().getProduct()!=null)
                                    {
                                        String numberbarcode = response.body().getProduct().getBarcode();
                                        String nameproduct=response.body().getProduct().getName();
                                        String description=response.body().getProduct().getDescription();
                                        String urlimage=response.body().getProduct().getImage().getUrl();
                                        String price=response.body().getProduct().getPrice();
                                        mymodel = new productmodel(nameproduct, numberbarcode, description, urlimage, null, null,price);
                                        myRef.push().setValue(mymodel);
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this,"Not Record in Database",Toast.LENGTH_LONG).show();
                                        mymodel = new productmodel(null, null, null, null, null, null,null);
                                        myRef.push().setValue(mymodel);

                                    }


                                } else {
                                    Toast.makeText(MainActivity.this,"Not Successful Response",Toast.LENGTH_LONG).show();

                                    mymodel = new productmodel(null, null, null, null, null, null,null);
                                    myRef.push().setValue(mymodel);


                                }


                            }

                            @Override
                            public void onFailure(Call<Productroot> call, Throwable t) {
                                Toast.makeText(MainActivity.this,"Connection Failed",Toast.LENGTH_LONG).show();

                                mymodel = new productmodel(null, null, null, null, null, null,null);
                                myRef.push().setValue(mymodel);


                            }
                        });

                        alertDialog.cancel();
                    }

                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();

                    }
                });

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        //get Firebase Database for total price
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("products");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                productmodel mypro= dataSnapshot.getValue(productmodel.class);
                String pricerow= mypro.getPriceproduct();
                if (pricerow!=null)
                {
                   // total=total+Float.parseFloat(pricerow);
                  //  pricetotal.setText(String.valueOf(total));
                }
                else{
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long u =dataSnapshot.getChildrenCount();
                            long virtualtotal=u*2;
                            pricetotal.setText(String.valueOf(virtualtotal));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //animation move btw activites

        overridePendingTransition(R.anim.downtocenter, R.anim.centertoup);
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.uptocenter,R.anim.centertodown);

    }

   // request camera permission method
    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Call<Productroot> mycall;

        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final String barcodedata = String.valueOf(data.getStringExtra("open"));

                    OkHttpClient.Builder builderr = new OkHttpClient.Builder();

                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    builderr.addInterceptor(loggingInterceptor);


                    Retrofit retrofitt = new Retrofit.Builder()
                            .baseUrl("https://api.barcodelookup.com/v2/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(builderr.build())
                            .build();

                    final Endpoints myendpoints = retrofitt.create(Endpoints.class);

                    mycall = myendpoints.addbarcode(barcodedata);
                    mycall.enqueue(new Callback<Productroot>() {
                        @Override
                        public void onResponse(Call<Productroot> call, Response<Productroot> response) {

                            if (response.isSuccessful()) {
                                String numberbarcode = response.body().getProduct().getBarcode();
                                String nameproduct=response.body().getProduct().getName();
                                String description=response.body().getProduct().getDescription();
                                String urlimage=response.body().getProduct().getImage().getUrl();
                                String price=response.body().getProduct().getPrice();
                                mymodel = new productmodel(nameproduct, numberbarcode, description, urlimage, null, null,price);
                                myRef.push().setValue(mymodel);

                            } else {
                                mymodel = new productmodel(null, barcodedata, null, null, null, null,null);
                                myRef.push().setValue(mymodel);

                            }


                        }

                        @Override
                        public void onFailure(Call<Productroot> call, Throwable t) {

                            mymodel = new productmodel(null, barcodedata, null, null, null, null,null);
                            myRef.push().setValue(mymodel);


                        }
                    });
                    builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    TextView doneit, scananotherr, cardinformation;

                    View myview = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.additemdialog, null);
                    doneit = myview.findViewById(R.id.Donee);
                    scananotherr = myview.findViewById(R.id.other);
                    cardinformation = myview.findViewById(R.id.cardinfo);
                    cardinformation.setText(String.valueOf(barcodedata));
                    doneit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();

                        }
                    });
                    scananotherr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();
                            Intent intent1 = new Intent(MainActivity.this, Camera_activity.class);
                            startActivityForResult(intent1, 0);

                        }
                    });

                    builder.setView(myview);
                    alertDialog = builder.create();
                    alertDialog.show();


                } else {
                    Toast.makeText(MainActivity.this, "No barcode found", Toast.LENGTH_LONG).show();
                }


            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }









    }

















