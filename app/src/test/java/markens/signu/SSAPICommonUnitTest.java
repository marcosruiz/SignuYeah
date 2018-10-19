package markens.signu;

import java.io.IOException;

import markens.signu.objects.Pdf;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SSAPICommonUnitTest {

    private static final String CLIENT_ID = "application";
    private static final String CLIENT_SECRET = "secret";
    private static final String GRANT_TYPE = "password";
    private static final String TOKEN_TYPE = "bearer";

    public static Token getToken(SignuServerService sss, String email, String password) throws IOException {
        Call<Token> call = sss.getToken(email, password, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<Token> response = call.execute();
        assertTrue(response.isSuccessful());
        Token token = response.body();
        assertNotNull(token);
        assertTrue(token.getExpiresIn() == 3600);
        assertTrue(token.getTokenType().equals(TOKEN_TYPE));
        return token;
    }

    public static User getUser(SignuServerService sss, Token token) throws IOException {
        String auth = "Bearer " + token.getAccessToken();
        // Get info user
        Call<SSResponse> call = sss.getUser(auth);
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssRes = response.body();
        User user = ssRes.getData().getUser();
        return user;
    }

    public static Pdf getPdf(SignuServerService sss, Token token, String pdfId) throws IOException {
        String auth = "Bearer " + token.getAccessToken();
        // Get info user
        Call<SSResponse> call = sss.getPdfInfo(auth, pdfId);
        Response<SSResponse> response = call.execute();
        assertTrue(response.isSuccessful());
        SSResponse ssRes = response.body();
        Pdf pdf = ssRes.getData().getPdf();
        return pdf;
    }

}
