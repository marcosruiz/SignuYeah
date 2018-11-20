package markens.signu;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestCase;

import org.junit.Assert;
import org.junit.Test;

import androidx.test.filters.SmallTest;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MainInstrumentedTest extends InstrumentationTestCase {

    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        retrofit = new Retrofit.Builder().baseUrl("http://test.com")
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
    }

    /**
     * Dumb test of MockSignuServerService
     * @throws Exception
     */
    @SmallTest
    public void testRandomQuoteRetrieval() throws Exception {
        BehaviorDelegate<SignuServerService> delegate = mockRetrofit.create(SignuServerService.class);
        SignuServerService sss = new MockSignuServerService(delegate);


        //Actual Test
        Call<SSResponse> ssRes = sss.getUserExt("Bearer 123");
        Response<SSResponse> response = ssRes.execute();

        //Asserting response
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(0, response.body().getCode());
        Assert.assertEquals("Success", response.body().getMessage());
    }

    /**
     * Dumb test to check the enviroment is ok
     * @throws Exception
     */
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("markens.signu", appContext.getPackageName());
    }



}
