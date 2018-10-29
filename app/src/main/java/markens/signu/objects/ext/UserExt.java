package markens.signu.objects.ext;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import markens.signu.objects.Activation;
import markens.signu.objects.NextEmail;

public class UserExt implements Serializable{
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
    private List<PdfExt> pdfsToSign;
    @SerializedName("pdfs_signed")
    private List<PdfExt> pdfsSigned;
    @SerializedName("pdfs_owned")
    private List<PdfExt> pdfsOwned;
    @SerializedName("users_related")
    private List<markens.signu.objects.User> usersRelated;
    @SerializedName("activation")
    private Activation activation;
    @SerializedName("next_email")
    private NextEmail nextEmail;

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

    public List<PdfExt> getPdfsToSign() {
        return pdfsToSign;
    }

    public void setPdfsToSign(List<PdfExt> pdfsToSign) {
        this.pdfsToSign = pdfsToSign;
    }

    public List<PdfExt> getPdfsSigned() {
        return pdfsSigned;
    }

    public void setPdfsSigned(List<PdfExt> pdfsSigned) {
        this.pdfsSigned = pdfsSigned;
    }

    public List<PdfExt> getPdfsOwned() {
        return pdfsOwned;
    }

    public void setPdfsOwned(List<PdfExt> pdfsOwned) {
        this.pdfsOwned = pdfsOwned;
    }

    public List<markens.signu.objects.User> getUsersRelated() {
        return usersRelated;
    }

    public void setUsersRelated(List<markens.signu.objects.User> usersRelated) {
        this.usersRelated = usersRelated;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public NextEmail getNextEmail() {
        return nextEmail;
    }

    public void setNextEmail(NextEmail nextEmail) {
        this.nextEmail = nextEmail;
    }
}
