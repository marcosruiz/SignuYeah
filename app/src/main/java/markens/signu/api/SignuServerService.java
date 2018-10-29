package markens.signu.api;

import android.widget.ArrayAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
import retrofit2.http.PUT;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SignuServerService {

    /**
     * USERS
     */

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<Token> getToken(@Field("username") String email, @Field("password") String password,
                         @Field("grant_type") String grantType, @Field("client_id") String clientId,
                         @Field("client_secret") String clientSecret);

    @FormUrlEncoded
    @POST("api/users/logout")
    Call<SSResponse> logOut(@Header("Authorization") String authorization);

    @GET("api/users/search")
    Call<SSResponse> searchUsers(@Header("Authorization") String authorization, @Query("email") String email);

    @GET("api/users/info/ext")
    Call<SSResponse> getUserExt(@Header("Authorization") String authorization);

    @GET("api/users/info")
    Call<SSResponse> getUser(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @HTTP(method = "POST", path = "api/users/create", hasBody = true)
    Call<SSResponse> createUser(@Field("email") String email, @Field("password") String password,
                                @Field("name") String name, @Field("lastname") String lastname);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/users", hasBody = true)
    Call<SSResponse> deleteUser(@Header("Authorization") String authorization,
                                @Field("password") String password);

    @FormUrlEncoded
    @PUT("api/users/related")
    Call<SSResponse> addRelatedUser(@Header("Authorization") String authorization,
                                @Field("related_id") String userId);

    @FormUrlEncoded
    @PUT("api/users/")
    Call<SSResponse> editUser(@Header("Authorization") String authorization,
                              @Field("name") String name, @Field("lastname") String lastname);

    @FormUrlEncoded
    @PUT("api/users/password/")
    Call<SSResponse> editUserPassword(@Header("Authorization") String authorization,
                                      @Field("password") String password);

    @FormUrlEncoded
    @PUT("api/users/email/")
    Call<SSResponse> editUserEmail(@Header("Authorization") String authorization,
                                   @Field("email") String email);

    /**
     * PDFS
     */

    @Multipart
    @POST("api/pdfs/")
    Call<SSResponse> uploadPdf(@Header("Authorization") String authorization,
                               @Part MultipartBody.Part pdf);

    @Multipart
    @POST("api/pdfs/")
    Call<SSResponse> uploadPdfWithSigners(@Header("Authorization") String authorization,
                                          @Part MultipartBody.Part pdf,
                                          @Part MultipartBody.Part signers);

    @Multipart
    @PUT("api/pdfs/{pdf_id}")
    Call<SSResponse> signPdf(@Header("Authorization") String authorization,
                             @Part MultipartBody.Part pdf, @Path("pdf_id") String pdfId,
                             @Part MultipartBody.Part lastEditionDate);

    @FormUrlEncoded
    @PUT("api/pdfs/addsigner/{pdf_id}")
    Call<SSResponse> addSigner(@Header("Authorization") String authorization,
                               @Path("pdf_id") String pdfId,
                               @Field("signer_id") String signerId);

    @GET("api/pdfs/info/{pdf_id}")
    Call<SSResponse> getPdfInfo(@Header("Authorization") String authorization,
                                @Path("pdf_id") String pdfId);

    @GET("api/pdfs/{pdf_id}")
    Call<ResponseBody> downloadPdf(@Header("Authorization") String authorization,
                                   @Path("pdf_id") String pdfId);

    @DELETE("api/pdfs/{pdf_id}")
    Call<SSResponse> deletePdf(@Header("Authorization") String authorization,
                               @Path("pdf_id") String pdfId);


}
