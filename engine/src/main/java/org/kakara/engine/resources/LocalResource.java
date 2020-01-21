package org.kakara.engine.resources;

import org.apache.commons.io.IOUtils;
import org.kakara.engine.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class LocalResource implements Resource {
    private URL url;

    public LocalResource(URL url) {
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
            return IOUtils.toByteArray(getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ByteBuffer getByteBuffer() {
        try {
            return Utils.ioResourceToByteBuffer(url.getPath(), getByteArray().length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public URL getURL() {
        return url;
    }

    @Override
    public String toString() {
        return url.getPath();
    }
}
