import simulator.displaying.Frame;
import simulator.simulation.world.Tile;
import simulator.simulation.world.World;
import simulator.simulation.world.Chunk;
import simulator.system.KeyListener;
import simulator.system.Logger;

import java.util.Random;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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
                + ", biome=" + tile.getBiome().getId());
        Logger.info("Chunk (0, 0) contains " + Chunk.SIZE + " x " + Chunk.SIZE + " tiles.");

        Logger.info("Creating world...");

        Logger.info("Creating Key Listener...");

        KeyListener kl = new KeyListener();

        Logger.info("Starting world...");
        SwingUtilities.invokeLater(() -> {
            Frame frame = new Frame(world);
            frame.addKeyListener(kl);
            long[] previousFrameNanos = {System.nanoTime()};
            Timer timer = new Timer(16, event -> {
                long currentFrameNanos = System.nanoTime();
                double deltaSeconds = (currentFrameNanos - previousFrameNanos[0]) / 1_000_000_000.0;
                previousFrameNanos[0] = currentFrameNanos;
                world.update(Math.min(deltaSeconds, 0.25), kl);
                frame.render(kl.isDebugMode());
            });
            timer.start();
            frame.addWindowFocusListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowLostFocus(java.awt.event.WindowEvent event) {
                    kl.clearPressedKeys();
                }
            });
            frame.setFocusable(true);
            frame.requestFocusInWindow();
        });

    }

}
