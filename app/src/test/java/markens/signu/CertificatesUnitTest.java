package markens.signu;

import org.junit.Test;

import java.security.KeyStore;

import markens.signu.itext.Signature;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CertificatesUnitTest {
    private static final String TAG = "TEST";

    private static final String URL_HEROKU = "https://signu-server.herokuapp.com/";
    private static final String URL_LOCAL = "http://localhost:3000/";
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
     * CERTIFICATES
     */

    @Test
    public void SSS_User_API_Login_Success_Async() throws Exception {
        KeyStore ks = Signature.getKeyStore("src/test/res/personal.p12", "password");
        assertNotNull(ks);
        assertNotNull(ks.aliases());
        assertNotNull(ks.aliases().nextElement());


    }
}