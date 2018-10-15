package markens.signu;

import com.google.gson.Gson;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.TokenError;
import markens.signu.objects.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SSAPIUnitTest {
    private static final String TAG = "TEST";
    private static final String URL_LOCAL = "http://localhost:3000/";
    private static final String URL_HEROKU = "https://signu-server.herokuapp.com/";
    private static final String USER_EMAIL = "marcosruizgarcia@gmail.com";
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
    public void SSS_User_API_Login_Success() throws Exception {
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
                //System.out.println(response.body().getAccessToken());
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
    public void SSS_User_API_Login_Fail() throws Exception {
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
    public void SSS_User_API_Get_Info_Success() throws Exception {
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
    public void SSS_User_API_Create_Fail() throws Exception {
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

    @Test
    public void SSS_User_API_Delete_Success() throws Exception {

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
        Call<SSResponse> call2 = signuServerService.deleteUser("Bearer " + token.getAccessToken(), USER_PASS);
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssResponse = response2.body();
        System.out.println(ssResponse.getCode());
        assertEquals(ssResponse.getCode(), 3);
        assertEquals(ssResponse.getMessage(), "User deleted successfully");
    }

    @Test
    public void SSS_User_API_Create_Success() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_HEROKU)
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