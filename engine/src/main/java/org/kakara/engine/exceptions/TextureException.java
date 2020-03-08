package org.kakara.engine.exceptions;

import org.kakara.engine.item.Texture;

public class TextureException extends RuntimeException {
    public TextureException(String s) {
        super(s);
    }

    public TextureException(Exception e) {
        super(e);
    }

    public TextureException(String s, Exception e) {
        super(s, e);
    }
}
