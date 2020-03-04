package org.kakara.engine.resources;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public interface Resource {
    InputStream getInputStream();

    URL getURL();

    String getPath();

    byte[] getByteArray();

    ByteBuffer getByteBuffer();
}