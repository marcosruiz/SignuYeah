package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NextEmail  implements Serializable {
    @SerializedName("email")
    private String email;
    @SerializedName("code")
    private String code;
    @SerializedName("when")
    private String when;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }


}
