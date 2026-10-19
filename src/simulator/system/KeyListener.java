package simulator.system;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class KeyListener implements java.awt.event.KeyListener {

    private final Set<Integer> pressedKeys = new HashSet<>();
    private boolean debugMode;

    public boolean isForwardPressed() {
        return isPressed(KeyEvent.VK_W, KeyEvent.VK_UP, KeyEvent.VK_Z);
    }

    public boolean isBackwardPressed() {
        return isPressed(KeyEvent.VK_S, KeyEvent.VK_DOWN);
    }

    public boolean isLeftPressed() {
        return isPressed(KeyEvent.VK_A, KeyEvent.VK_LEFT, KeyEvent.VK_Q);
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public boolean isRightPressed() {
        return isPressed(KeyEvent.VK_D, KeyEvent.VK_RIGHT);
    }

    private boolean isPressed(int... keyCodes) {
        for (int keyCode : keyCodes) {
            if (pressedKeys.contains(keyCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Typed events do not reliably provide key codes; movement uses press/release state.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        boolean newlyPressed = pressedKeys.add(keyCode);
        if (newlyPressed && keyCode == KeyEvent.VK_F3) {
            debugMode = !debugMode;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    public void clearPressedKeys() {
        pressedKeys.clear();
    }
}
