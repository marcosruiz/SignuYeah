package markens.signu.storage;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import okhttp3.ResponseBody;

public class StorageCtrl {
    Context context;
    String userId;

    final String pdfsNameFolder = "pdfs";
    final String keystoreNameFolder = "keystores";

    public StorageCtrl(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        // Create folders if they do not exist
        File mydir = context.getDir(userId, Context.MODE_PRIVATE);
        if (!mydir.exists()) {
            mydir.mkdirs();
        }
        mydir = context.getDir(userId + "." + pdfsNameFolder, Context.MODE_PRIVATE);
        if (!mydir.exists()) {
            mydir.mkdirs();
        }
        mydir = context.getDir(userId + "." + keystoreNameFolder, Context.MODE_PRIVATE);
        if (!mydir.exists()) {
            mydir.mkdirs();
        }
    }

    public File getPdfsFolder(){
        return context.getDir(userId + "." + pdfsNameFolder, Context.MODE_PRIVATE);
    }

    public File getKeystoreFolder(){
        return context.getDir(userId + "." + keystoreNameFolder, Context.MODE_PRIVATE);
    }

    public boolean writeResponseBodyPdfToDisk(ResponseBody body, String fileName) {
        try {
//            File file = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
            File file = new File(getPdfsFolder() + File.separator + fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    // System.out.println("file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean itExists(String fileName) {
        File file = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + fileName);
        return file.exists();
    }

    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static void delete(File src) {
        src.delete();
    }
}
