package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("user")
    private User user;
    @SerializedName("pdf")
    private Pdf pdf;

    public User getUser() {
        return user;
    }

    public Pdf getPdf() {
        return pdf;
    }
}
