package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TokenError  implements Serializable {
    @SerializedName("error")
    private String error;

    @SerializedName("error_description")
    private String errorDescription;

    @SerializedName("code")
    private int code;

    public TokenError(String error, String errorDescription, int code) {
        this.error = error;
        this.errorDescription = errorDescription;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
