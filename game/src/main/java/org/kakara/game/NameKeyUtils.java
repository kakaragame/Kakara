package org.kakara.game;

import org.kakara.core.NameKey;

public class NameKeyUtils {
    public static final String KAKARA_NAME = "KAKARA";

    public static NameKey newKakaraNameKey(String key) {
        return new NameKey(KAKARA_NAME, key);
    }
}
