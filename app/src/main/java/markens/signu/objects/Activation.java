package markens.signu.objects;

import com.google.gson.annotations.SerializedName;

public class Activation {
    @SerializedName("is_activated")
    private boolean isActivated;
    @SerializedName("when")
    private String when;

    public boolean isActivated() {
        return isActivated;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }
}
