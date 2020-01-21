package org.kakara.engine.resources;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public interface Resource {
    InputStream getInputStream();

    URL getURL();

    byte[] getByteArray();

    ByteBuffer getByteBuffer();
}