package markens.signu;

import android.widget.ArrayAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface SignuServerService {

    /**
     * USERS
     */

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<Token> getToken(@Field("username") String email, @Field("password") String password, @Field("grant_type") String grantType, @Field("client_id") String clientId, @Field("client_secret") String clientSecret);

    @POST("api/users/logout")
    Call<SSResponse> logOut(@Header("Authorization") String authorization);

    @GET("api/users/info")
    Call<SSResponse> getUser(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @HTTP(method = "POST", path = "api/users/create", hasBody = true)
    Call<SSResponse> createUser(@Field("email") String email, @Field("password") String password, @Field("name") String name, @Field("lastname") String lastname);

    // TODO this works?
    @Multipart
    @DELETE("api/users")
    Call<SSResponse> deleteUser2(@Header("Authorization") String authorization, @Part("password") String password);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/users", hasBody = true)
    Call<SSResponse> deleteUser(@Header("Authorization") String authorization, @Field("password") String password);

    /**
     * PDFS
     */

    @Multipart
    @POST("api/pdfs/")
    Call<SSResponse> uploadPdf(@Header("Authorization") String authorization, @Part MultipartBody.Part pdf);

    @Multipart
    @POST("api/pdfs/")
    Call<SSResponse> uploadPdfWithSigners(@Header("Authorization") String authorization, @Part MultipartBody.Part pdf, @Part MultipartBody.Part signers);


}
