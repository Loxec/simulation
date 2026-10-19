package simulator.displaying;

import simulator.simulation.world.Chunk;
import simulator.simulation.world.World;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class Frame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final World world;
    private final Cell[][] cells = new Cell[Chunk.SIZE][Chunk.SIZE];
    private final JPanel gridPanel = new JPanel(new GridLayout(Chunk.SIZE, Chunk.SIZE));
    private final JLabel debugText = new JLabel();
    private final JLayeredPane layers = new JLayeredPane() {
        private static final long serialVersionUID = 1L;

        @Override
        public void doLayout() {
            gridPanel.setBounds(0, 0, getWidth(), getHeight());
            debugText.setBounds(8, 8, 330, 100);
        }
    };

    public Frame(World world) {
        super(world.getWorldId());
        this.world = world;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (int y = 0; y < Chunk.SIZE; y++) {
            for (int x = 0; x < Chunk.SIZE; x++) {
                cells[y][x] = new Cell(Color.BLACK);
                gridPanel.add(cells[y][x]);
            }
        }

        gridPanel.setPreferredSize(new Dimension(640, 640));
        layers.setPreferredSize(new Dimension(640, 640));
        debugText.setOpaque(true);
        debugText.setBackground(new Color(0, 0, 0, 190));
        debugText.setForeground(Color.WHITE);
        debugText.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        layers.add(gridPanel, JLayeredPane.DEFAULT_LAYER);
        layers.add(debugText, JLayeredPane.PALETTE_LAYER);
        setContentPane(layers);
        pack();
        setResizable(true);
        setLocationRelativeTo(null);
        render(false);
        setVisible(true);
    }

    public void render(boolean debug_mode) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> render(debug_mode));
            return;
        }

        int cameraX = world.getCamera().getX();
        int cameraY = world.getCamera().getY();
        int viewLeft = cameraX - Chunk.SIZE / 2;
        int viewTop = cameraY - Chunk.SIZE / 2;
        int chunkX = Math.floorDiv(cameraX, Chunk.SIZE);
        int chunkY = Math.floorDiv(cameraY, Chunk.SIZE);
        Chunk chunk = world.getChunk(chunkX, chunkY);
        for (int y = 0; y < Chunk.SIZE; y++) {
            for (int x = 0; x < Chunk.SIZE; x++) {
                Color color = world.getTile(viewLeft + x, viewTop + y).getBiome().getColor();
                if (x == Chunk.SIZE / 2 && y == Chunk.SIZE / 2) {
                    color = Color.RED;
                }
                cells[y][x].setColor(color);
                cells[y][x].setDebugBorderVisible(debug_mode);
            }
        }
        debugText.setVisible(debug_mode);
        if (debug_mode) {
            String biome = world.getTile(cameraX, cameraY).getBiome().getId();
            debugText.setText(String.format(Locale.ROOT,
                    "<html>Biome: %s<br>Tile: (x=%d, y=%d)<br>Chunk coordinates: (x=%d, y=%d)<br>Chunk: %d x %d tiles</html>",
                    biome, cameraX, cameraY, chunk.getChunkX(), chunk.getChunkY(), Chunk.SIZE, Chunk.SIZE));
        }
        repaint();
    }

}
