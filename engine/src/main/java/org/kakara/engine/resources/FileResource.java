package org.kakara.engine.resources;

import org.apache.commons.io.IOUtils;
import org.kakara.engine.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class FileResource implements Resource {
    private URL url;

    public FileResource(URL url) {
        this.url = url;
    }


    public InputStream getInputStream() {
        try {
            return url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] getByteArray() {
        try {
            return getInputStream().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return ByteBuffer.wrap(getByteArray());
    }

    public URL getURL() {
        return url;
    }

    @Override
    public String getPath() {
        return url.getPath();
    }

    @Override
    public String toString() {
        return url.getPath();
    }
}
