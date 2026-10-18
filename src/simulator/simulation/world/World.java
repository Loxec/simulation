package simulator.simulation.world;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final String worldId;
    private final long seed;
    private final Map<Long, Chunk> chunks = new HashMap<>();

    public World(String worldId, long seed) {
        this.worldId = worldId;
        this.seed = seed;
    }

    public Chunk getChunk(int chunkX, int chunkY) {
        long key = ((long) chunkX << 32) | (chunkY & 0xffffffffL);
        return chunks.computeIfAbsent(key, ignored -> new Chunk(chunkX, chunkY, seed));
    }

    public Tile getTile(int worldX, int worldY) {
        int chunkX = Math.floorDiv(worldX, Chunk.SIZE);
        int chunkY = Math.floorDiv(worldY, Chunk.SIZE);
        int localX = Math.floorMod(worldX, Chunk.SIZE);
        int localY = Math.floorMod(worldY, Chunk.SIZE);
        return getChunk(chunkX, chunkY).getTile(localX, localY);
    }

    public void setTile(int worldX, int worldY, Tile tile) {
        int chunkX = Math.floorDiv(worldX, Chunk.SIZE);
        int chunkY = Math.floorDiv(worldY, Chunk.SIZE);
        int localX = Math.floorMod(worldX, Chunk.SIZE);
        int localY = Math.floorMod(worldY, Chunk.SIZE);
        getChunk(chunkX, chunkY).setTile(localX, localY, tile);
    }

    public String getWorldId() {
        return worldId;
    }

    public long getSeed() {
        return seed;
    }
}
