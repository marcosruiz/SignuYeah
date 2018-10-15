package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("email")
    private String email;
    @SerializedName("last_edition_date")
    private String lastEditionDate;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("pdfs_to_sign")
    private Pdf[] pdfsToSign;
    @SerializedName("pdfs_signed")
    private Pdf[] pdfsSigned;
    @SerializedName("pdfs_owned")
    private Pdf[] pdfsOwned;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastEditionDate() {
        return lastEditionDate;
    }

    public void setLastEditionDate(String lastEditionDate) {
        this.lastEditionDate = lastEditionDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Pdf[] getPdfsToSign() {
        return pdfsToSign;
    }

    public void setPdfsToSign(Pdf[] pdfsToSign) {
        this.pdfsToSign = pdfsToSign;
    }

    public Pdf[] getPdfsSigned() {
        return pdfsSigned;
    }

    public void setPdfsSigned(Pdf[] pdfsSigned) {
        this.pdfsSigned = pdfsSigned;
    }

    public Pdf[] getPdfsOwned() {
        return pdfsOwned;
    }

    public void setPdfsOwned(Pdf[] pdfsOwned) {
        this.pdfsOwned = pdfsOwned;
    }
}
