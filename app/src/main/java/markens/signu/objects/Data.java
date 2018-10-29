package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import markens.signu.objects.ext.UserExt;

public class Data implements Serializable{
    @SerializedName("user")
    private User user;
    @SerializedName("pdf")
    private Pdf pdf;
    @SerializedName("user_ext")
    private UserExt userExt;

    public User getUser() {
        return user;
    }

    public UserExt getUserExt() {
        return userExt;
    }

    public Pdf getPdf() {
        return pdf;
    }
}
