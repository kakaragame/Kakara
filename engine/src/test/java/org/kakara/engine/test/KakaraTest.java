package org.kakara.engine.test;

import org.kakara.engine.GameHandler;
import org.kakara.engine.Game;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnKeyPressEvent;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.sound.SoundBuffer;
import org.kakara.engine.sound.SoundListener;
import org.kakara.engine.sound.SoundSource;
import org.lwjgl.openal.AL11;

import static org.lwjgl.glfw.GLFW.*;

public class KakaraTest implements Game {

    private GameHandler gInst;
    private MainGameScene gameScene;

    @Override
    public void start(GameHandler handler) throws Exception {
        gInst = handler;
        gameScene = new MainGameScene(handler);
        gInst.getSceneManager().setScene(gameScene);
        try {
         gInst.getSoundManager().init();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        gInst.getSoundManager().setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
//        gInst.getSoundManager().setListener(new SoundListener());
//
//        SoundBuffer buffBack = new SoundBuffer(handler.getResourceManager().getResource("sounds/background.ogg"));
//        gInst.getSoundManager().addSoundBuffer(buffBack);
//        SoundSource sourceBack = new SoundSource(true, true);
//        sourceBack.setBuffer(buffBack.getBufferId());
//        gInst.getSoundManager().addSoundSource("MUSIC", sourceBack);
//        gInst.getSoundManager().playSoundSource("MUSIC");
    }


    @Override
    public void update() {

    }

    @EventHandler
    public void onMouseClick(OnMouseClickEvent evt) {
        System.out.println("clicked1");
    }

    @EventHandler
    public void onKeyEvent(OnKeyPressEvent evt) {
        if (evt.isKeyPressed(GLFW_KEY_TAB)) {
            //Engine API replaced GLFW methods.
            gInst.getWindow().setCursorVisibility(!gInst.getWindow().isCursorVisable());
            gInst.getMouseInput().setCursorPosition(gInst.getWindow().getWidth() / 2, gInst.getWindow().getHeight() / 2);
        }
    }
}
