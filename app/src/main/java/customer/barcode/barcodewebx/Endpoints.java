package customer.barcode.barcodewebx;

import customer.barcode.barcodewebx.Models.Productroot;
import customer.barcode.barcodewebx.usermodels.Userroot;
import customer.barcode.barcodewebx.usermodels.loginbody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Endpoints {

   // @Headers("Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNTZCQkM2MS1CODkyLTQ5NEEtQUU0Qy02Mzg3NUI2OEEwRTciLCJpc3MiOiJodHRwOi8vd3d3LndlcnB4Lm5ldC9hcGkvdjEvbG9naW4iLCJpYXQiOjE1NDQ2MDkwNzksImV4cCI6MTU0NDcxNzA3OSwibmJmIjoxNTQ0NjA5MDc5LCJqdGkiOiJEQlp6WDY3dmFhNjhZYW1qIn0.h2Wz966xQFh9Vq54KVAuS1s0DEkL_VxdC4SggCD9qRs")
    @POST("barcode")
    Call<Productroot> addbarcode(@Header("Authorization")String authtoken ,@Query("barcode") String barcode);


    @FormUrlEncoded
    @POST("login")
    Call<Userroot> loginuser(@Header("Content-Type") String content_type, @Field("email") String email,@Field("password") String password);

}
