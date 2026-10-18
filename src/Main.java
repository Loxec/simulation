import simulator.simulation.world.Tile;
import simulator.simulation.world.World;
import simulator.simulation.world.Chunk;
import simulator.system.Logger;

import java.util.Random;

public class Main{

    public static void main(String[] args){

        Logger.info("Taking args...");

        // needed arguments: default_option:bool,world_id:string


        boolean default_option = args.length > 0 && Boolean.parseBoolean(args[0]);
        String world_id = "upworld";
        if (default_option && args.length > 1) {
            world_id = args[1];
        }

        Logger.info("Initializing the world...");
        long seed = args.length > 2 ? Long.parseLong(args[2]) : new Random().nextLong();
        World world = new World(world_id, seed);

        Logger.info("World '" + world.getWorldId() + "' created with seed " + world.getSeed());
        Chunk chunk = world.getChunk(0, 0);
        Tile tile = chunk.getTile(0, 0);
        Logger.info("Tile at (0, 0): temperature=" + tile.getTemperature()
                + ", height=" + tile.getHeight()
                + ", humidity=" + tile.getHumidity() + "%"
                + ", biome=" + tile.getBiomeId());
        Logger.info("Chunk (0, 0) contains " + Chunk.SIZE + " x " + Chunk.SIZE + " tiles.");


    }

}
