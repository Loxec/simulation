package simulator.simulation.world;

import simulator.simulation.entities.Camera;
import simulator.system.KeyListener;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final String worldId;
    private final long seed;
    private final Map<Long, Chunk> chunks = new HashMap<>();
    private Camera camera;
    private double simulationTimeSeconds;
    private double cameraMoveRemainderX;
    private double cameraMoveRemainderY;
    private static final double CAMERA_SPEED_TILES_PER_SECOND = 8.0;

    public World(String worldId, long seed) {
        this.worldId = worldId;
        this.seed = seed;
        this.camera = new Camera(0,0);
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

    public void update(double deltaSeconds) {
        update(deltaSeconds, null);
    }

    public void update(double deltaSeconds, KeyListener keys) {
        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
            throw new IllegalArgumentException("Delta time must be a finite, non-negative number");
        }
        simulationTimeSeconds += deltaSeconds;

        if (keys == null) {
            return;
        }

        int horizontal = (keys.isRightPressed() ? 1 : 0) - (keys.isLeftPressed() ? 1 : 0);
        int vertical = (keys.isBackwardPressed() ? 1 : 0) - (keys.isForwardPressed() ? 1 : 0);
        cameraMoveRemainderX += horizontal * CAMERA_SPEED_TILES_PER_SECOND * deltaSeconds;
        cameraMoveRemainderY += vertical * CAMERA_SPEED_TILES_PER_SECOND * deltaSeconds;

        int moveX = (int) cameraMoveRemainderX;
        int moveY = (int) cameraMoveRemainderY;
        cameraMoveRemainderX -= moveX;
        cameraMoveRemainderY -= moveY;
        camera.setX(camera.getX() + moveX);
        camera.setY(camera.getY() + moveY);

    }

    public double getSimulationTimeSeconds() {
        return simulationTimeSeconds;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
