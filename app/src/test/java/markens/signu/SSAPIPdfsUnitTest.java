package markens.signu;

import android.provider.MediaStore;

import com.google.gson.Gson;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.TokenError;
import markens.signu.objects.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SSAPIPdfsUnitTest {
    private static final String TAG = "TEST";
    private static final String URL_LOCAL = "http://localhost:3000/";
    private static final String URL_HEROKU = "https://signu-server.herokuapp.com/";
    private static final String USER_EMAIL = "marcosruizgarcia@gmail.com";
    private static final String USER_EMAIL_2 = "sobrenombre@gmail.com";
    private static final String USER_PASS = "prueba";
    private static final String USER_NAME = "Marcos";
    private static final String USER_LASTNAME = "Ruiz";
    private static final String CLIENT_ID = "application";
    private static final String CLIENT_SECRET = "secret";
    private static final String GRANT_TYPE = "password";
    private static final String TOKEN_TYPE = "bearer";
    private static final int WAITING_TIME = 3000; //3 segs

    @Test
    public void SSS_User_API_Create_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Create user
        Call<SSResponse> call = signuServerService.createUser(USER_EMAIL, USER_PASS, USER_NAME, USER_LASTNAME);
        Response<SSResponse> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        SSResponse ssRes = response1.body();
        assertNotNull(ssRes);
        assertEquals(0, ssRes.getCode());
        assertEquals("Email was sent to " + USER_EMAIL, ssRes.getMessage());
    }

    @Test
    public void SSS_User_API_Create_Signer_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Create user
        Call<SSResponse> call = signuServerService.createUser(USER_EMAIL_2, USER_PASS, USER_NAME, USER_LASTNAME);
        Response<SSResponse> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        SSResponse ssRes = response1.body();
        assertNotNull(ssRes);
        assertEquals(0, ssRes.getCode());
        assertEquals("Email was sent to " + USER_EMAIL_2, ssRes.getMessage());
    }

    public User getUser(SignuServerService sss, Token token) throws IOException {
        String auth = "Bearer " + token.getAccessToken();
        // Get info user
        Call<SSResponse> callUser = sss.getUser(auth);
        Response<SSResponse> responseUser = callUser.execute();
        assertTrue(responseUser.isSuccessful());
        SSResponse ssResUser = responseUser.body();
        User u = ssResUser.getData().getUser();
        return u;
    }

    public Token getToken(SignuServerService sss, String email, String password) throws IOException {
        // Login: Get token
        Call<Token> call = sss.getToken(email, password, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        Token token = response1.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));
        return token;
    }

    @Test
    public void SSS_Pdf_API_Upload_With_Signers_Success() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Get token 1
        Token token1 = getToken(sss, USER_EMAIL, USER_PASS);
        String auth1 = "Bearer " + token1.getAccessToken();

        // Get info user 1
        User u1_1 = getUser(sss, token1);

        // Get token 2
        Token token2 = getToken(sss, USER_EMAIL_2, USER_PASS);
        String auth2 = "Bearer " + token2.getAccessToken();

        // Get info user 2
        User u2_1 = getUser(sss, token2);

        // Get file from system
        File file = new File("src/test/res/test.pdf");
        System.out.println(file.getAbsolutePath());
        assertTrue(file.canRead());

        // Upload file with 1 signer
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("pdf", file.getName(), requestFile);

        // Signers
        MultipartBody.Part signers = MultipartBody.Part.createFormData("signers[0]", u2_1.getId());

        Call<SSResponse> call2 = sss.uploadPdfWithSigners(auth1, body, signers);
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssRes = response2.body();
        assertEquals(101, ssRes.getCode());
        assertEquals("PDF created successfully", ssRes.getMessage());
        assertNotNull(ssRes.getData().getPdf());

        // Get info user
        User u1_2 = getUser(sss, token1);
        User u2_2 = getUser(sss, token2);

        assertEquals(u1_1.getId(), u1_2.getId());
        assertEquals(u2_1.getId(), u2_2.getId());
        assertEquals(u1_1.getPdfsOwned().length + 1, u1_2.getPdfsOwned().length);
        assertEquals(u1_1.getPdfsToSign().length, u1_2.getPdfsToSign().length);
        assertEquals(u1_1.getPdfsSigned().length, u1_2.getPdfsSigned().length);
        assertEquals(u2_1.getPdfsToSign().length + 1, u2_2.getPdfsToSign().length);
        assertEquals(u2_1.getPdfsOwned().length, u2_2.getPdfsOwned().length);
        assertEquals(u2_1.getPdfsSigned().length, u2_2.getPdfsSigned().length);

    }

    @Test
    public void SSS_Pdf_API_Upload_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);
        String auth = "Bearer " + token.getAccessToken();

        // Get info user 2
        User u1 = getUser(sss, token);

        // Get file from system
        File file = new File("src/test/res/test.pdf");
        System.out.println(file.getAbsolutePath());
        assertTrue(file.canRead());

        // Upload file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("pdf", file.getName(), requestFile);
        Call<SSResponse> call2 = sss.uploadPdf(auth, body);
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssRes = response2.body();
        assertEquals(101, ssRes.getCode());
        assertEquals("PDF created successfully", ssRes.getMessage());
        assertNotNull(ssRes.getData().getPdf());

        // Get info user 2
        User u2 = getUser(sss, token);

        assertEquals(u1.getId(), u2.getId());
        assertEquals(u1.getPdfsOwned().length + 1, u2.getPdfsOwned().length);
    }


    /**
     * MOCKING
     */

    @Test
    public void testLoginSuccessMocked() {
        SignuServerService mockedSss = Mockito.mock(SignuServerService.class);
        final Call<Token> mockedCall = Mockito.mock(Call.class);

        Mockito.when(mockedSss.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET)).thenReturn(mockedCall);

        Mockito.doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Callback<Token> callback = invocation.getArgument(0);

                callback.onResponse(mockedCall, Response.success(new Token("accessToken", TOKEN_TYPE, 3600)));
                // or callback.onResponse(mockedCall, Response.error(404. ...);
                // or callback.onFailure(mockedCall, new IOException());

                return null;
            }
        }).when(mockedCall).enqueue(any(Callback.class));


        Call<Token> call = mockedSss.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Token token = response.body();
                assertTrue(response.isSuccessful());
                assertNotNull(token);
                assertTrue(token.getExpiresIn() == 3600);
                assertTrue(token.getTokenType().equals(TOKEN_TYPE));
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                assertTrue(false);
            }
        });
    }
}