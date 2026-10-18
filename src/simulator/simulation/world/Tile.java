package simulator.simulation.world;

/** Data stored for one world cell. */
public class Tile {
    private double temperature;
    private double height;
    private double humidity;
    private String biome_id;

    public Tile(double temperature, double height, double humidity, String biome_id) {
        this.temperature = temperature;
        this.height = height;
        this.humidity = humidity;
        this.biome_id = biome_id;
    }

    public double getTemperature() { return temperature; }
    public double getHeight() { return height; }
    public double getHumidity() { return humidity; }
    public String getBiomeId() { return biome_id; }

    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setHeight(double height) { this.height = height; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setBiomeId(String biome_id) { this.biome_id = biome_id; }
}
