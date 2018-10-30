package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Pdf  implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("original_name")
    private String originalName;
    @SerializedName("mime_type")
    private String mimeType;
    @SerializedName("file_name")
    private String fileName;
    @SerializedName("last_edition_date")
    private String lastEditionDate;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("owner_id")
    private String ownerId;
    @SerializedName("signers")
    private List<Signer> signers;
    @SerializedName("with_stamp")
    private boolean withStamp;

    public Pdf(String id, String originalName, String mimeType, String fileName, String lastEditionDate, String creationDate, String ownerId, List<Signer> signers) {
        this.id = id;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.fileName = fileName;
        this.lastEditionDate = lastEditionDate;
        this.creationDate = creationDate;
        this.ownerId = ownerId;
        this.signers = signers;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<Signer> getSigners() {
        return signers;
    }

    public void setSigners(List<Signer> signers) {
        this.signers = signers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
