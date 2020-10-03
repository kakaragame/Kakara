package org.kakara.game;

import org.kakara.core.ControllerKey;

public class NameKeyUtils {
    public static final String KAKARA_NAME = "KAKARA";

    public static ControllerKey newKakaraNameKey(String key) {
        return new ControllerKey(KAKARA_NAME, key);
    }
}
