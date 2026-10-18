package simulator.simulation.world;

public class Chunk {
    public static final int SIZE = 16;

    private final int chunkX;
    private final int chunkY;
    private final Tile[][] tiles = new Tile[SIZE][SIZE];

    public Chunk(int chunkX, int chunkY, long worldSeed) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        TerrainGenerator generator = new TerrainGenerator(worldSeed);
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                int worldX = chunkX * SIZE + x;
                int worldY = chunkY * SIZE + y;
                tiles[x][y] = generator.generate(worldX, worldY);
            }
        }
    }

    public Tile getTile(int localX, int localY) {
        checkLocalCoordinates(localX, localY);
        return tiles[localX][localY];
    }

    public void setTile(int localX, int localY, Tile tile) {
        checkLocalCoordinates(localX, localY);
        tiles[localX][localY] = tile;
    }

    private static void checkLocalCoordinates(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            throw new IndexOutOfBoundsException("Local tile coordinates must be between 0 and " + (SIZE - 1));
        }
    }

    public int getChunkX() { return chunkX; }
    public int getChunkY() { return chunkY; }
}
