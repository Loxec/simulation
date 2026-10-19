package simulator.simulation.world;

import java.awt.*;

/** Data stored for one world cell. */
public class Tile {
    private double temperature;
    private double height;
    private double humidity;
    private Biome biome;

    public Tile(double temperature, double height, double humidity, Biome biome) {
        this.temperature = temperature;
        this.height = height;
        this.humidity = humidity;
        this.biome= biome;
    }

    public double getTemperature() { return temperature; }
    public double getHeight() { return height; }
    public double getHumidity() { return humidity; }
    public Biome getBiome() { return biome; }

    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setHeight(double height) { this.height = height; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setBiome(Biome biome_id) { this.biome = biome_id; }
}
