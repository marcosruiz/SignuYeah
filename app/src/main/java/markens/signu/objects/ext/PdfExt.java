package markens.signu.objects.ext;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import markens.signu.objects.Pdf;

public class PdfExt implements Serializable {
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
    private markens.signu.objects.User ownerId;
    @SerializedName("signers")
    private List<SignerExt> signers;
    @SerializedName("with_stamp")
    private boolean withStamp;

    public PdfExt(String id, String originalName, String mimeType, String fileName, String lastEditionDate, String creationDate, markens.signu.objects.User ownerId, List<SignerExt> signers) {
        this.id = id;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.fileName = fileName;
        this.lastEditionDate = lastEditionDate;
        this.creationDate = creationDate;
        this.ownerId = ownerId;
        this.signers = signers;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        PdfExt p = (PdfExt) object;
        if (id.equals(p.getId()) && lastEditionDate.equals(p.getLastEditionDate()) && signers.size() == p.getSigners().size()) {
            result =  true;
        }
        return result;
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

    public markens.signu.objects.User getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(markens.signu.objects.User ownerId) {
        this.ownerId = ownerId;
    }

    public List<SignerExt> getSigners() {
        return signers;
    }

    public void setSigners(List<SignerExt> signers) {
        this.signers = signers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isWithStamp() {
        return withStamp;
    }

    public void setWithStamp(boolean withStamp) {
        this.withStamp = withStamp;
    }
}
