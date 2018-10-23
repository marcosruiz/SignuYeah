package markens.signu;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import markens.signu.api.SignuServerService;
import markens.signu.objects.Pdf;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static markens.signu.SSAPICommonUnitTest.getPdf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static markens.signu.SSAPICommonUnitTest.getToken;
import static markens.signu.SSAPICommonUnitTest.getUser;

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
    private static final String USER_EMAIL_3 = "try@try.com";
    private static final String USER_PASS = "prueba";
    private static final String USER_NAME = "Marcos";
    private static final String USER_LASTNAME = "Ruiz";
    private static final String CLIENT_ID = "application";
    private static final String CLIENT_SECRET = "secret";
    private static final String GRANT_TYPE = "password";
    private static final String TOKEN_TYPE = "bearer";
    private static final int WAITING_TIME = 3000; //3 segs

    @Test
    public void SSS_User_API_Create_User_Success() throws Exception {

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
//        assertEquals(u1_1.getPdfsOwned().length + 1, u1_2.getPdfsOwned().length);
//        assertEquals(u1_1.getPdfsToSign().length, u1_2.getPdfsToSign().length);
//        assertEquals(u1_1.getPdfsSigned().length, u1_2.getPdfsSigned().length);
//        assertEquals(u2_1.getPdfsToSign().length + 1, u2_2.getPdfsToSign().length);
//        assertEquals(u2_1.getPdfsOwned().length, u2_2.getPdfsOwned().length);
//        assertEquals(u2_1.getPdfsSigned().length, u2_2.getPdfsSigned().length);

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

        // Get info user 1
        User u1 = getUser(sss, token);

        // Get file from system
        File file = new File("src/test/res/test.pdf");
        System.out.println(file.getAbsolutePath());
        assertTrue(file.canRead());

        // Upload file
        String auth = "Bearer " + token.getAccessToken();
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
//        assertEquals(u1.getPdfsOwned().length + 1, u2.getPdfsOwned().length);
    }

    @Test
    public void SSS_Pdf_API_Get_Info_Success() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Get info pdf
        String auth = "Bearer " + token.getAccessToken();
        String pdfId = u1.getPdfsOwned().get((u1.getPdfsOwned().size() - 1));
        Call<SSResponse> call = sss.getPdfInfo(auth, pdfId);
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssRes = response.body();
        Pdf pdf = ssRes.getData().getPdf();

        assertNotNull(pdf);
        assertEquals(pdfId, pdf.getId());
    }

    @Test
    public void SSS_Pdf_API_Download_Success() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Get info pdf
        String auth = "Bearer " + token.getAccessToken();
        String pdfId = u1.getPdfsOwned().get((u1.getPdfsOwned().size() - 1));
        Call<ResponseBody> call = sss.downloadPdf(auth, pdfId);
        Response<ResponseBody> response = call.execute();
        assertTrue(response.isSuccessful());
        ResponseBody ssRes = response.body();
        boolean writtenToDisk = writeResponseBodyToDisk(ssRes);
        assertTrue(writtenToDisk);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
//            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
            File futureStudioIconFile = new File("src/test/res/response.pdf");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    System.out.println("file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Test
    public void SSS_Pdf_API_Add_Signer_Success() throws Exception {
        SSS_Pdf_API_Upload_Success();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Get info pdf 1
        String pdfId = u1.getPdfsOwned().get((u1.getPdfsOwned().size() - 1));
        Pdf pdf1 = getPdf(sss, token, pdfId);

        // Add signer
        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.addSigner(auth, pdf1.getId(), u1.getId());
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssRes = response.body();
        Pdf pdf = ssRes.getData().getPdf();

        // Get info pdf 2
        Pdf pdf2 = getPdf(sss, token, pdfId);

        assertEquals(pdf1.getId(), pdf2.getId());
        assertEquals((pdf1.getSigners().size() + 1), pdf2.getSigners().size());
    }

    @Test
    public void SSS_Pdf_API_Sign_Success() throws Exception {
        SSS_Pdf_API_Add_Signer_Success();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Get info pdf 1
        String pdfId = u1.getPdfsOwned().get((u1.getPdfsOwned().size() - 1));
        Pdf pdf1 = getPdf(sss, token, pdfId);

        // Get file from system
        File file = new File("src/test/res/test2.pdf");
        System.out.println(file.getAbsolutePath());
        assertTrue(file.canRead());

        // Sign
        String auth = "Bearer " + token.getAccessToken();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("pdf", file.getName(), requestFile);
        MultipartBody.Part led = MultipartBody.Part.createFormData("last_edition_date", pdf1.getLastEditionDate());
        Call<SSResponse> call2 = sss.signPdf(auth, body, pdf1.getId(), led);
        Response<SSResponse> response2 = call2.execute();
        assertTrue(response2.isSuccessful());
        SSResponse ssRes = response2.body();
        assertTrue(ssRes.getCode() <= 200);
        assertNotNull(ssRes.getData().getPdf());

        // Get info user 1
        User u2 = getUser(sss, token);

        // Get info pdf 2
        Pdf pdf2 = getPdf(sss, token, pdfId);

        assertEquals(pdf1.getId(), pdf2.getId());
        assertEquals((pdf1.getSigners().size()), pdf2.getSigners().size());
        assertFalse(pdf1.getSigners().get(0).getIsSigned());
        assertTrue(pdf2.getSigners().get(0).getIsSigned());
        assertEquals(u1.getPdfsSigned().size() + 1, u2.getPdfsSigned().size());
        assertEquals(u1.getPdfsToSign().size() - 1, u2.getPdfsToSign().size());
        assertEquals(u1.getPdfsOwned().size(), u2.getPdfsOwned().size());
    }

    @Test
    public void SSS_Pdf_API_Delete_Success() throws Exception {
        SSS_Pdf_API_Upload_Success();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        // Login: Get token
        Token token = getToken(sss, USER_EMAIL, USER_PASS);

        // Get info user 1
        User u1 = getUser(sss, token);

        // Get info pdf 1
        String pdfId = u1.getPdfsOwned().get(0);
        Pdf pdf1 = getPdf(sss, token, pdfId);

        // Delete pdf
        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.deletePdf(auth, pdf1.getId());
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssResponse = response.body();
        assertTrue(ssResponse.getCode() <= 200);

        // Get info user 1
        User u2 = getUser(sss, token);

        // Get pdf 2
        Call<SSResponse> call2 = sss.getPdfInfo(auth, pdfId);
        Response<SSResponse> response2 = call2.execute();
        assertFalse(response2.isSuccessful());

        assertNotNull(pdf1);
        assertEquals(u1.getId(), u2.getId());
        assertEquals(u1.getPdfsToSign().size(), u2.getPdfsToSign().size());
        assertEquals(u1.getPdfsSigned().size(), u2.getPdfsSigned().size());
        assertEquals(u1.getPdfsOwned().size() - 1, u2.getPdfsOwned().size());
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