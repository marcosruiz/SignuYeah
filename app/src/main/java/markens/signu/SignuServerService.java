package markens.signu;

import org.json.JSONObject;

import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
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
import retrofit2.http.Query;

public interface SignuServerService {

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<Token> getToken(@Field("username") String email, @Field("password") String password, @Field("grant_type") String grantType, @Field("client_id") String clientId, @Field("client_secret") String clientSecret);

    @GET("api/users/info")
    Call<SSResponse> getUser(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @HTTP(method = "POST", path = "api/users/create", hasBody = true)
    Call<SSResponse> createUser(@Field("email") String email, @Field("password") String password, @Field("name") String name, @Field("lastname") String lastname);

    // TODO this works?
//    @Multipart
//    @DELETE("api/users")
//    Call<SSResponse> deleteUser(@Header("Authorization") String authorization, @Query("password") String password);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/users", hasBody = true)
    Call<SSResponse> deleteUser(@Header("Authorization") String authorization, @Field("password") String password);


}
