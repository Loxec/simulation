# Simulator

A small Java project that generates a seeded, two-dimensional world as chunks of environmental tiles.

## Features

- **Lazy chunk creation:** the world creates a chunk the first time it is requested. Each chunk contains a 16 × 16 grid of tiles.
- **Continuous terrain fields:** temperature, height, and humidity come from smooth, seeded noise sampled at world coordinates. Adjacent tiles change gradually, including across chunk borders.
- **Repeatable worlds:** passing the same seed generates the same initial terrain and biomes. If no seed is supplied, the program chooses one and prints it.
- **Biome assignment:** each generated tile receives a biome based on its temperature and humidity:

  | Temperature | Dry (<30%) | Normal (30–<70%) | Humid (≥70%) |
  | --- | --- | --- | --- |
  | Cold (<8) | Tundra | Snowy plain | Snowy forest |
  | Temperate (8–<20) | Steppe | Plain | Forest |
  | Hot (≥20) | Desert | Savanna | Mangrove |

- **World-coordinate access:** get or change a tile by its world X/Y coordinates. Negative coordinates are supported.

Generated values currently use illustrative ranges: height from 0 to 1000, humidity from 0 to 100 percent, and temperature approximately -18 to 40. These are simulation values, not calibrated real-world measurements.

## Project layout

```text
src/
  Main.java
  simulator/
    simulation/world/
      Chunk.java
      TerrainGenerator.java
      Tile.java
      World.java
    system/
      Ansi.java
      Logger.java
```

## Build and run

From the project root, compile the Java sources. In PowerShell:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
```

In a Unix-like shell:

```sh
mkdir -p out
javac -d out $(find src -name '*.java')
```

Run with the default world ID (`upworld`) and a random seed:

```sh
java -cp out Main
```

The optional arguments are:

```text
Main [useCustomWorldId] [worldId] [seed]
```

The current `Main` uses `worldId` only when `useCustomWorldId` is `true`. Examples:

```sh
java -cp out Main false
java -cp out Main true my-world 12345
```

The program prints the world ID, seed, and generated properties of tile `(0, 0)`. Reuse the printed seed to recreate the same initial world.

## Using the world API

```java
World world = new World("my-world", 12345L);

Tile tile = world.getTile(-1, 0); // chunks are created as needed
System.out.println(tile.getBiomeId());

tile.setHumidity(80.0);
world.setTile(-1, 0, tile);
```

`getChunk(chunkX, chunkY)` addresses chunks directly. `getTile(worldX, worldY)` and `setTile(worldX, worldY, tile)` use world tile coordinates.
