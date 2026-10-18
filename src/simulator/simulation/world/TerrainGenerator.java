package simulator.simulation.world;

/** Generates smooth, repeatable environment values from world coordinates. */
final class TerrainGenerator {
    private final long seed;

    TerrainGenerator(long seed) {
        this.seed = seed;
    }

    Tile generate(int worldX, int worldY) {
        // The larger the scale, the more slowly the field changes across the map.
        double heightNoise = noise(worldX, worldY, 12, 1);
        double temperatureNoise = noise(worldX, worldY, 20, 2);
        double humidityNoise = noise(worldX, worldY, 16, 3);

        double height = heightNoise * 1000.0;
        // Higher elevations tend to be cooler, with a broad regional temperature variation.
        double temperature = -10.0 + temperatureNoise * 50.0 - heightNoise * 8.0;
        double humidity = humidityNoise * 100.0;
        return new Tile(temperature, height, humidity,generateBiome(temperature,humidity));
    }

    private double noise(int x, int y, int scale, long channel) {
        double sampleX = (double) x / scale;
        double sampleY = (double) y / scale;
        int x0 = (int) Math.floor(sampleX);
        int y0 = (int) Math.floor(sampleY);
        double tx = smooth(sampleX - x0);
        double ty = smooth(sampleY - y0);

        double top = lerp(valueAt(x0, y0, channel), valueAt(x0 + 1, y0, channel), tx);
        double bottom = lerp(valueAt(x0, y0 + 1, channel), valueAt(x0 + 1, y0 + 1, channel), tx);
        return lerp(top, bottom, ty);
    }

    private static String generateBiome(double temperature,double humidity) {
        boolean cold = temperature < 8.0;
        boolean hot = temperature >= 20.0;
        boolean dry = humidity < 30.0;
        boolean humid = humidity >= 70.0;

        if (cold) {
            if (dry) return "tundra";
            if (humid) return "snowy forest";
            return "snowy plain";
        }

        if (hot) {
            if (dry) return "desert";
            if (humid) return "mangrove";
            return "savanna";
        }

        if (dry) return "steppe";
        if (humid) return "forest";
        return "plain";
    }

    private double valueAt(int x, int y, long channel) {
        long value = seed + channel * 0x9E3779B97F4A7C15L;
        value ^= (long) x * 0xC2B2AE3D27D4EB4FL;
        value ^= (long) y * 0x165667B19E3779F9L;
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        value ^= value >>> 31;
        return (value >>> 11) * 0x1.0p-53;
    }

    private static double smooth(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
