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
    @SerializedName("was_locked")
    private boolean wasLocked;
    @SerializedName("was_locked_by")
    private String wasLockedBy;
    @SerializedName("when_was_locked")
    private String whenWasLocked;
    @SerializedName("add_signers_enabled")
    private boolean addSignersEnabled;

    public Pdf(String id, String originalName, String mimeType, String fileName, String lastEditionDate, String creationDate, String ownerId, List<Signer> signers, boolean withStamp, boolean wasLocked, String wasLockedBy, String whenWhasLocked) {
        this.id = id;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.fileName = fileName;
        this.lastEditionDate = lastEditionDate;
        this.creationDate = creationDate;
        this.ownerId = ownerId;
        this.signers = signers;
        this.withStamp = withStamp;
        this.wasLocked = wasLocked;
        this.wasLockedBy = wasLockedBy;
        this.whenWasLocked = whenWhasLocked;
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

    public boolean isWasLocked() {
        return wasLocked;
    }

    public void setWasLocked(boolean wasLocked) {
        this.wasLocked = wasLocked;
    }

    public String getWasLockedBy() {
        return wasLockedBy;
    }

    public void setWasLockedBy(String wasLockedBy) {
        this.wasLockedBy = wasLockedBy;
    }

    public String getWhenWhasLocked() {
        return whenWasLocked;
    }

    public void setWhenWhasLocked(String whenWhasLocked) {
        this.whenWasLocked = whenWhasLocked;
    }

    public boolean isWithStamp() {
        return withStamp;
    }

    public boolean getAddSignersEnabled() {
        return addSignersEnabled;
    }

    public void setAddSignersEnabled(boolean addSignersEnabled) {
        this.addSignersEnabled = addSignersEnabled;
    }
}
