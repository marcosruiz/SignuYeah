package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Signer  implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("signature_date")
    private String signatureDate;
    @SerializedName("is_signed")
    private boolean isSigned;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Signer(String id, boolean isSigned, String signatureDate) {
        this.id = id;
        this.isSigned = isSigned;
        this.signatureDate = signatureDate;
    }
}
