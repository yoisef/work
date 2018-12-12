package customer.barcode.barcodewebx;

import customer.barcode.barcodewebx.Models.Productroot;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Endpoints {

    @Headers("Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNTZCQkM2MS1CODkyLTQ5NEEtQUU0Qy02Mzg3NUI2OEEwRTciLCJpc3MiOiJodHRwOi8vd3d3LndlcnB4Lm5ldC9hcGkvdjEvbG9naW4iLCJpYXQiOjE1NDQ2MDkwNzksImV4cCI6MTU0NDcxNzA3OSwibmJmIjoxNTQ0NjA5MDc5LCJqdGkiOiJEQlp6WDY3dmFhNjhZYW1qIn0.h2Wz966xQFh9Vq54KVAuS1s0DEkL_VxdC4SggCD9qRs")
    @POST("barcode")
    Call<Productroot> addbarcode(@Query("barcode") String barcode);

}
