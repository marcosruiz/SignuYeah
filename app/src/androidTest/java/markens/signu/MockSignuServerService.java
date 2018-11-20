package markens.signu;

import com.google.gson.Gson;

import java.util.List;

import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class MockSignuServerService implements SignuServerService{

    private final BehaviorDelegate<SignuServerService> delegate;

    public MockSignuServerService(BehaviorDelegate<SignuServerService> service) {
        this.delegate = service;
    }

    @Override
    public Call<Token> getToken(String email, String password, String grantType, String clientId, String clientSecret) {
        return null;
    }

    @Override
    public Call<SSResponse> logOut(String authorization) {
        return null;
    }

    @Override
    public Call<SSResponse> searchUsers(String authorization, String email) {
        return null;
    }

    @Override
    public Call<SSResponse> getUserExt(String authorization) {
        // TODO this is only a try
        Gson gSon = new Gson();
        SSResponse ssResponse = new SSResponse(0,"Success", null);
        return delegate.returningResponse(ssResponse).getUserExt(authorization);
    }

    @Override
    public Call<SSResponse> getUser(String authorization) {
        return null;
    }

    @Override
    public Call<SSResponse> createUser(String email, String password, String name, String lastname) {
        return null;
    }

    @Override
    public Call<SSResponse> deleteUser(String authorization, String password) {
        return null;
    }

    @Override
    public Call<SSResponse> addRelatedUser(String authorization, String userId) {
        return null;
    }

    @Override
    public Call<SSResponse> editUser(String authorization, String name, String lastname) {
        return null;
    }

    @Override
    public Call<SSResponse> editUserPassword(String authorization, String password) {
        return null;
    }

    @Override
    public Call<SSResponse> editUserEmail(String authorization, String email) {
        return null;
    }

    @Override
    public Call<SSResponse> deleteRelatedUser(String auth, String userId) {
        return null;
    }

    @Override
    public Call<SSResponse> uploadPdf(String authorization, MultipartBody.Part pdf) {
        return null;
    }

    @Override
    public Call<SSResponse> uploadPdfWithSigners(String authorization, MultipartBody.Part pdf, List<MultipartBody.Part> signers, boolean addSignersEnabed, boolean withStamp) {
        return null;
    }

    @Override
    public Call<SSResponse> signPdf(String authorization, MultipartBody.Part pdf, String pdfId, MultipartBody.Part lastEditionDate) {
        return null;
    }

    @Override
    public Call<SSResponse> addSigner(String authorization, String pdfId, String signerId) {
        return null;
    }

    @Override
    public Call<SSResponse> lockPdf(String authorization, String pdfId) {
        return null;
    }

    @Override
    public Call<SSResponse> getPdfInfo(String authorization, String pdfId) {
        return null;
    }

    @Override
    public Call<ResponseBody> downloadPdf(String authorization, String pdfId) {
        return null;
    }

    @Override
    public Call<SSResponse> deletePdf(String authorization, String pdfId) {
        return null;
    }
}
