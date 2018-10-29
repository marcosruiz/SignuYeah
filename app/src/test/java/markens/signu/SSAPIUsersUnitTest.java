package markens.signu;

import com.google.gson.Gson;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.TokenError;
import markens.signu.objects.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static markens.signu.SSAPICommonUnitTest.getToken;
import static markens.signu.SSAPICommonUnitTest.getUser;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SSAPIUsersUnitTest {
    private static final String TAG = "TEST";
    private static final String URL_LOCAL = "http://192.168.1.6:3000/";
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

    /**
     * ASYNC
     */

    @Test
    public void SSS_User_API_Login_Success_Async() throws Exception {
        final Object syncObject = new Object();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://signu-server.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService signuServerService = retrofit.create(SignuServerService.class);
        Call<Token> call = signuServerService.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);

        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Token token = response.body();
                assertTrue(response.isSuccessful());
                assertNotNull(token);
                assertTrue(token.getExpiresIn() == 3600);
                assertTrue(token.getTokenType().equals(TOKEN_TYPE));
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                assertTrue(false);
            }
        });
        synchronized (syncObject) {
            syncObject.wait(WAITING_TIME);
        }
    }

    @Test
    public void SSS_User_API_Login_Fail_Async() throws Exception {
        final Object syncObject = new Object();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService signuServerService = retrofit.create(SignuServerService.class);
        Call<Token> call = signuServerService.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Token token = response.body();
                assertFalse(response.isSuccessful());
                assertNull(token);
                assertEquals(response.message(), "Service Unavailable");
                assertEquals(response.code(), 503);
                try {
                    String errBody = response.errorBody().string();
                    // GSON
                    Gson g = new Gson();
                    TokenError tokenError = g.fromJson(errBody, TokenError.class);
                    assertEquals(tokenError.getCode(), 503);
                    assertEquals(tokenError.getError(), "server_error");
                    assertEquals(tokenError.getErrorDescription(), "server_error");
                    synchronized (syncObject) {
                        syncObject.notify();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                assertTrue(false);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });
        synchronized (syncObject) {
            syncObject.wait(WAITING_TIME);
        }
    }

    @Test
    public void SSS_User_API_Get_Info_Success_Async() throws Exception {
        final Object syncObject = new Object();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);
        Call<Token> call = signuServerService.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Token token = response.body();
                assertTrue(response.isSuccessful());
                assertNotNull(token);
                assertTrue(token.getExpiresIn() == 3600);
                assertTrue(token.getTokenType().equals(TOKEN_TYPE));

                //Call to get info user
                Call<SSResponse> call2 = signuServerService.getUser("Bearer " + token.getAccessToken());
                call2.enqueue(new Callback<SSResponse>() {
                    @Override
                    public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                        SSResponse ssResponse = response.body();
                        User myUser = ssResponse.getData().getUser();
                        assertNotNull(myUser);
                        assertNotNull(myUser.getEmail());
                        assertNotNull(myUser.getId());

                        synchronized (syncObject) {
                            syncObject.notify();
                        }
                    }

                    @Override
                    public void onFailure(Call<SSResponse> call, Throwable t) {
                        assertTrue(false);
                    }
                });

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                assertTrue(false);
            }
        });
        synchronized (syncObject) {
            syncObject.wait(WAITING_TIME);
        }
    }

    @Test
    public void SSS_User_API_Create_Fail_Async() throws Exception {
        final Object syncObject = new Object();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);
        Call<SSResponse> call = signuServerService.createUser(USER_EMAIL, USER_PASS, USER_NAME, USER_LASTNAME);
        call.enqueue(new Callback<SSResponse>() {

            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                assertFalse(response.isSuccessful());
                Gson g = new Gson();

                try {
                    String resString = response.errorBody().string();
                    System.out.println(resString);
                    SSResponse ssRes = g.fromJson(resString, SSResponse.class);
                    assertEquals(ssRes.getCode(), 1003);
                    assertEquals(ssRes.getMessage(), "Not Found");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                assertTrue(false);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });
        synchronized (syncObject) {
            syncObject.wait(WAITING_TIME);
        }
    }

    /**
     * SYNC
     */

    @Test
    public void SSS_User_API_Login_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Call<Token> call = sss.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        Token token = response1.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));
    }

    @Test
    public void SSS_User_API_Search_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        Token token = getToken(sss, USER_EMAIL, USER_PASS);
        String auth = "Bearer " + token.getAccessToken();

        Call<SSResponse> call =  sss.searchUsers(auth, "sobre");
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        assertNotNull(response.body().getData().getUsers());

    }

    @Test
    public void SSS_User_API_Login_Fail() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Login: Get token
        Call<Token> call = signuServerService.getToken("wrong@wrong.com", USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertFalse(response1.isSuccessful());
        Gson g = new Gson();
        TokenError tokenError = g.fromJson(response1.errorBody().string(), TokenError.class);
        assertEquals(503, tokenError.getCode());
        assertEquals("server_error", tokenError.getError());
    }

    @Test
    public void SSS_User_API_Delete_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Call<Token> call = sss.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        Token token = response1.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));

        // Delete user
        Call<SSResponse> call2 = sss.deleteUser("Bearer " + token.getAccessToken(), USER_PASS);
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssResponse = response2.body();
        System.out.println(ssResponse.getCode());
        assertEquals(3, ssResponse.getCode());
        assertEquals("PdfExt deleted successfully", ssResponse.getMessage());
    }

    @Test
    public void SSS_User_API_Delete_Fail() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Login: Get token
        Call<Token> call = signuServerService.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        Token token = response1.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));

        // Delete user
        Call<SSResponse> call2 = signuServerService.deleteUser("Bearer " + token.getAccessToken(), "wrongPass");
        Response<SSResponse> response2 = call2.execute();
        assertFalse(response2.isSuccessful());

        String r = response2.errorBody().string();
        Gson g = new Gson();
        SSResponse ssResponse = g.fromJson(r, SSResponse.class);
        System.out.println(ssResponse.getCode());
        assertEquals(1002, ssResponse.getCode());
        assertEquals("", ssResponse.getMessage());
    }

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
    public void SSS_Pdf_API_Add_Related_Success() throws Exception {

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

        Call<SSResponse> call = sss.addRelatedUser(auth1, u2_1.getId());
        Response<SSResponse> response2 = call.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssRes = response2.body();
        assertTrue(ssRes.getCode() <= 200);
        assertNotNull(ssRes.getData().getUser());

        // Get info user
        User u1_2 = getUser(sss, token1);
        User u2_2 = getUser(sss, token2);

        assertEquals(u1_1.getId(), u1_2.getId());
        assertEquals(u2_1.getId(), u2_2.getId());
        assertEquals(u1_1.getUsersRelated().size() + 1, u1_2.getUsersRelated().size());

    }

    @Test
    public void SSS_User_API_Info_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Login: Get token
        Call<Token> call = signuServerService.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        Token token = response1.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));

        // Get info user
        Call<SSResponse> call2 = signuServerService.getUser("Bearer " + token.getAccessToken());
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssResponse = response2.body();
        assertEquals(0, ssResponse.getCode());
        assertEquals("Success", ssResponse.getMessage());
        assertNotNull(ssResponse.getData().getUser());
    }

    @Test
    public void SSS_User_API_Info_Ext_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Login: Get token
        Call<Token> call = signuServerService.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        Token token = response1.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));

        // Get info user
        Call<SSResponse> call2 = signuServerService.getUserExt("Bearer " + token.getAccessToken());
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssResponse = response2.body();
        assertEquals(0, ssResponse.getCode());
        assertEquals("Success", ssResponse.getMessage());
        assertNotNull(ssResponse.getData().getUserExt());
    }

    @Test
    public void SSS_User_API_Info_Fail() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Login: Get token
        Call<Token> call = signuServerService.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        Token token = response1.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));

        // Get info user
        Call<SSResponse> call2 = signuServerService.getUser("Bearer " + token.getAccessToken() + "fdjskl");
        Response<SSResponse> response2 = call2.execute();
        assertFalse(response2.isSuccessful());
        Gson g = new Gson();
        SSResponse ssRes = g.fromJson(response2.errorBody().string(), SSResponse.class);
        assertEquals(1003, ssRes.getCode());
        assertEquals("The access token provided is invalid.", ssRes.getMessage());
    }

    @Test
    public void SSS_User_API_LogOut_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Login: Get token
        Call<Token> call = signuServerService.getToken(USER_EMAIL, USER_PASS, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response1 = call.execute();
        assertTrue(response1.isSuccessful());
        Token token = response1.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));

        // Log out
        Call<SSResponse> call2 = signuServerService.logOut("Bearer " + token.getAccessToken());
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssResponse = response2.body();
        assertEquals(7, ssResponse.getCode());
        assertEquals("PdfExt logged out successfully", ssResponse.getMessage());
    }

    @Test
    public void SSS_User_API_LogOut_Fail() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);

        // Log out
        Call<SSResponse> call2 = signuServerService.logOut("Bearer aaaaaaab");
        Response<SSResponse> response2 = call2.execute();
        assertFalse(response2.isSuccessful());
        Gson g = new Gson();
        SSResponse ssRes = g.fromJson(response2.errorBody().string(), SSResponse.class);
        assertEquals(1003, ssRes.getCode());
        assertEquals("The access token provided is invalid.", ssRes.getMessage());
    }

    @Test
    public void SSS_User_API_Edit_Name_And_Lastname_Success() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Edit user
        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.editUser(auth, u1.getName() + "E", u1.getLastname() + "E");
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssResponse = response.body();
        assertTrue(ssResponse.getCode() <= 200);

        // Get info user 1
        User u2 = getUser(sss, token);

        assertNotNull(u1);
        assertNotNull(u2);
        assertEquals(u1.getId(), u2.getId());
        assertEquals(u1.getName() + "E", u2.getName());
        assertEquals(u1.getLastname() + "E", u2.getLastname());

        // Edit user
        String auth2 = "Bearer " + token.getAccessToken();
        Call<SSResponse> call2 = sss.editUser(auth, USER_NAME, USER_LASTNAME);
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssResponse2 = response2.body();
        assertTrue(ssResponse2.getCode() <= 200);
    }

    @Test
    public void SSS_User_API_Edit_Email_Fail_Cause_It_exist() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Edit user email
        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.editUserEmail(auth, "sobrenombre@gmail.com");
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssResponse = response.body();
        assertTrue(ssResponse.getCode() <= 200);

        // Get info user 1
        User u2 = getUser(sss, token);

        assertNotNull(u1);
        assertNotNull(u2);
        assertEquals(u1.getId(), u2.getId());
        assertEquals(u1.getName() + "E", u2.getName());
        assertEquals(u1.getLastname() + "E", u2.getLastname());
    }

    @Test
    public void SSS_User_API_Edit_Email_Fail_Cause_Wrong() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Edit user email
        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.editUserEmail(auth, "wrong@wrong");
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssResponse = response.body();
        assertTrue(ssResponse.getCode() <= 200);

        // Get info user 1
        User u2 = getUser(sss, token);

        assertNotNull(u1);
        assertNotNull(u2);
        assertEquals(u1.getId(), u2.getId());
        assertEquals(u2.getNextEmail().getEmail(), "wrong@wrong");
    }

    @Test
    public void SSS_User_API_Edit_Email_Success() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Edit user email
        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.editUserEmail(auth, "novel.in.this@gmail.com");
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssResponse = response.body();
        assertTrue(ssResponse.getCode() <= 200);

        // Get info user 1
        User u2 = getUser(sss, token);

        assertNotNull(u1);
        assertNotNull(u2);
        assertEquals(u1.getId(), u2.getId());
        assertEquals(u1.getName(), u2.getName());
        assertEquals(u1.getLastname(), u2.getLastname());
        assertEquals(u1.getEmail(), u2.getEmail());
        assertEquals("novel.in.this@gmail.com", u1.getNextEmail().getEmail());
    }

    @Test
    public void SSS_User_API_Edit_Password_Success() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Edit user password
        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.editUserPassword(auth, "pruebaE");
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssResponse = response.body();
        assertTrue(ssResponse.getCode() <= 200);

        // Get info user 2
        Token token2 = getToken(sss, USER_EMAIL, "pruebaE");
        User u2 = getUser(sss, token2);

        assertNotNull(u1);
        assertNotNull(u2);
        assertEquals(u1.getId(), u2.getId());
        assertEquals(u1.getName(), u2.getName());

        // Edit user password again
        String auth2 = "Bearer " + token2.getAccessToken();
        Call<SSResponse> call2 = sss.editUserPassword(auth2, USER_PASS);
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssResponse2 = response2.body();
        assertTrue(ssResponse2.getCode() <= 200);
    }

    /**
     * MOCK
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