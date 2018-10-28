package markens.signu.objects.ext;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import markens.signu.objects.User;

public class SignerExt implements Serializable{
    @SerializedName("_id")
    private User id;
    @SerializedName("signature_date")
    private String signatureDate;
    @SerializedName("is_signed")
    private boolean isSigned;

    public User getId() {
        return id;
    }

    public void setId(User id) {
        this.id = id;
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

    public SignerExt(User id, boolean isSigned, String signatureDate) {
        this.id = id;
        this.isSigned = isSigned;
        this.signatureDate = signatureDate;
    }
}
