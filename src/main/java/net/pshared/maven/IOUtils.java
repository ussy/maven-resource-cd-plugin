package net.pshared.maven;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class IOUtils {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static void copy(File srcFile, File destFile) {
        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            FileChannel srcChannel = null;
            FileChannel destChannel = null;
            try {
                srcChannel = new FileInputStream(srcFile).getChannel();
                destChannel = new FileOutputStream(destFile).getChannel();
                destChannel.transferFrom(srcChannel, 0, srcChannel.size());
            } finally {
                close(srcChannel);
                close(destChannel);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
