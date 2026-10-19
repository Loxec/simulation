package simulator.displaying;

import javax.swing.*;
import java.awt.*;

public class Cell extends JPanel {

    private static final Color DEBUG_BORDER = new Color(20, 20, 20);

    public Cell(Color color){
        setColor(color);
    }

    public void setColor(Color color) {
        setBackground(color);
    }

    public void setDebugBorderVisible(boolean visible) {
        setBorder(visible ? BorderFactory.createLineBorder(DEBUG_BORDER) : null);
    }

}
