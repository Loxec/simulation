package simulator.simulation.world;

import java.awt.*;

public class Biome {

    private String id;
    private Color color;

    public Biome(String id, Color color){
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }
    public Color getColor() {
        return color;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setColor(Color color) {
        this.color = color;
    }


}
