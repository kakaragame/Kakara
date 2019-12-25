package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.ItemHandler;
import org.kakara.engine.render.Shader;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.glClearColor;

public class SimpleLoadingScene implements Scene {
    private boolean mouseStatus;
    private ItemHandler itemHandler = new ItemHandler();

    public SimpleLoadingScene() {

    }

    @Override
    public void render(GameHandler handler) {
        handler.getGameEngine().getRenderer().clear();
        GL11.glClearColor(0.0f, 0.5f, 0.5f, 1.0f);
        GLFW.glfwSwapBuffers(handler.getWindow().getWindowHandler());
    }

    @Override
    public void setMouseStatus(boolean status) {
        this.mouseStatus = status;
    }

    @Override
    public boolean getMouseStatus() {
        return mouseStatus;
    }

    @Override
    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public void unload() {

    }
}
