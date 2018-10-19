package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> pdfsToSign;
    @SerializedName("pdfs_signed")
    private List<String> pdfsSigned;
    @SerializedName("pdfs_owned")
    private List<String> pdfsOwned;
    @SerializedName("users_related")
    private List<String> usersRelated;
    @SerializedName("activation")
    private Activation activation;

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

    public List<String> getUsersRelated() {
        return usersRelated;
    }

    public void setUsersRelated(List<String> usersRelated) {
        this.usersRelated = usersRelated;
    }

    public List<String> getPdfsOwned() {
        return pdfsOwned;
    }

    public void setPdfsOwned(List<String> pdfsOwned) {
        this.pdfsOwned = pdfsOwned;
    }

    public List<String> getPdfsSigned() {
        return pdfsSigned;
    }

    public void setPdfsSigned(List<String> pdfsSigned) {
        this.pdfsSigned = pdfsSigned;
    }

    public List<String> getPdfsToSign() {
        return pdfsToSign;
    }

    public void setPdfsToSign(List<String> pdfsToSign) {
        this.pdfsToSign = pdfsToSign;
    }
}
