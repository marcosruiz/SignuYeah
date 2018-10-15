package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }
}
