package customer.barcode.barcodewebx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import customer.barcode.barcodewebx.Models.Productroot;
import customer.barcode.barcodewebx.usermodels.Userroot;
import customer.barcode.barcodewebx.usermodels.loginbody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login_activity extends AppCompatActivity {


    EditText useremail, userpass;
    TextView signin,signup;
    Call<Userroot> mycall;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        progressBar=findViewById(R.id.loginprogress);
        signup=findViewById(R.id.signupuser);

        useremail = findViewById(R.id.email);
        userpass = findViewById(R.id.password);
        signin = findViewById(R.id.login);



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = useremail.getText().toString();
                String pass = userpass.getText().toString();

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
                loginbody mybody = new loginbody(email,pass);

                mycall = myendpoints.loginuser("application/x-www-form-urlencoded",email,pass);
                mycall.enqueue(new Callback<Userroot>() {
                    @Override
                    public void onResponse(Call<Userroot> call, Response<Userroot> response) {

                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.VISIBLE);
                            String usertoken=response.body().getData().getToken();
                            SharedPreferences prefs = getSharedPreferences("logintoken", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=prefs.edit();
                            editor.putString("token",usertoken);
                            editor.apply();
                            Toast.makeText(Login_activity.this,"Successful Login" , Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Login_activity.this,MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(Login_activity.this, "wrong email or pass", Toast.LENGTH_LONG).show();

                        }


                    }

                    @Override
                    public void onFailure(Call<Userroot> call, Throwable t) {

                    }
                });


            }
        });
    }
}

