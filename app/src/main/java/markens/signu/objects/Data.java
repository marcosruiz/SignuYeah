package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import markens.signu.objects.ext.UserExt;

public class Data implements Serializable{
    @SerializedName("user")
    private User user;
    @SerializedName("users")
    private List<User> users;
    @SerializedName("pdf")
    private Pdf pdf;
    @SerializedName("user_ext")
    private UserExt userExt;

    public Data(User user, List<User> users, Pdf pdf, UserExt userExt) {
        this.user = user;
        this.users = users;
        this.pdf = pdf;
        this.userExt = userExt;
    }

    public User getUser() {
        return user;
    }

    public UserExt getUserExt() {
        return userExt;
    }

    public Pdf getPdf() {
        return pdf;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
