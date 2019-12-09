package org.kakara.engine.input;

import org.joml.Vector2d;
import org.kakara.engine.gui.Window;
import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
    private final Vector2d previousPos;
    private final Vector2d currentPos;
    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    public Mouse(){
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
    }

    public void init(Window window){
        glfwSetCursorPosCallback(window.getWindowHandler(), (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        glfwSetCursorEnterCallback(window.getWindowHandler(), (windowHandle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(window.getWindowHandler(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public boolean isLeftButtonPressed(){
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed(){
        return rightButtonPressed;
    }

    public Vector2d getPosition(){
        return currentPos;
    }
}
