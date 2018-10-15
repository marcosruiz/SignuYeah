package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

public class Pdf {
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
    @SerializedName("singers")
    private User[] signers;
}
