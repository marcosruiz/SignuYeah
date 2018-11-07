package markens.signu.objects.ext;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import markens.signu.objects.User;

public class SignerExt implements Serializable{
    @SerializedName("_id")
    private User user;
    @SerializedName("signature_date")
    private String signatureDate;
    @SerializedName("is_signed")
    private boolean isSigned;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean getIsSigned() {
        return isSigned;
    }

    public void setIsSigned(boolean isSigned) {
        this.isSigned = isSigned;
    }

    public String getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(String signatureDate) {
        this.signatureDate = signatureDate;
    }

    public SignerExt(User user, boolean isSigned, String signatureDate) {
        this.user = user;
        this.isSigned = isSigned;
        this.signatureDate = signatureDate;
    }
}
