package markens.signu.storage;

import android.net.Uri;
import android.support.v4.content.FileProvider;

public class GenericFileProvider extends FileProvider {
    public static final Uri CONTENT_URI=
            Uri.parse("content://markens.signu.storage/");

}
