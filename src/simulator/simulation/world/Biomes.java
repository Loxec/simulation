package simulator.simulation.world;

import java.awt.*;

public enum Biomes {

    TUNDRA(new Color(220, 235, 255), "tundra"),
    SNOWY_PLAIN(new Color(235, 245, 255), "snowy plain"),
    SNOWY_FOREST(new Color(190, 220, 205), "snowy forest"),
    STEPPE(new Color(190, 170, 95), "steppe"),
    PLAIN(new Color(120, 190, 80), "plain"),
    FOREST(new Color(44, 153, 71), "forest"),
    DESERT(new Color(230, 195, 110), "desert"),
    SAVANNA(new Color(205, 175, 70), "savanna"),
    MANGROVE(new Color(45, 125, 100), "mangrove"),
    JUNGLE(new Color(13, 93, 4), "jungle");

    private final Biome biome;

    Biomes(Color color, String id) {
        this.biome = new Biome(id, color);
    }

    public Biome getBiome() {
        return biome;
    }

}
