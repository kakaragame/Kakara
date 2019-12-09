package org.kakara.engine.render;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.w3c.dom.Text;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int id;

    /**
     * Load a texture
     * @param inputStream the inputStream for the file.
     * @throws Exception
     */
    public Texture(InputStream inputStream) throws Exception {
        this(loadTexture(inputStream));
    }

    public Texture(int id) {
        this.id = id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    private static int loadTexture(InputStream inputStream) throws Exception {
        int width;
        int height;
        ByteBuffer buf;
        // Load Texture file
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            InputStream imageFile = inputStream;
            System.out.println(imageFile);
            byte[] imageData = imageFile.readAllBytes();
            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
            imageBuffer.put(imageData);
            imageBuffer.flip();

            buf = stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file could not loaded: " + stbi_failure_reason());
            }

            /* Get width and height of image */
            width = w.get();
            height = h.get();
        }

        // Create a new OpenGL texture
        int textureId = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(buf);

        return textureId;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}
