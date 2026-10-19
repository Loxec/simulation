# Simulator

A small Java Swing application that generates and displays a seeded, two-dimensional world.

## Features

- **Seeded terrain:** temperature, height, and humidity are generated from smooth, deterministic noise. Reusing a seed recreates the same initial terrain.
- **Lazy chunks:** the world creates 16 × 16 tile chunks when a tile in that chunk is first requested. World coordinates, including negative coordinates, are supported.
- **Biome colors:** each tile is assigned a biome from its temperature and humidity, and the map is colored by biome.
- **Scrollable camera:** move around the generated world with the keyboard. The red center tile marks the camera position.
- **Debug view:** press **F3** to toggle tile borders and a top-left overlay showing the biome, world tile coordinates, chunk coordinates, and chunk dimensions.
- **Delta-time updates:** camera movement is based on elapsed time, at 8 tiles per second, rather than frame count. Large pauses are capped to avoid a sudden jump.

Biome assignment currently uses these thresholds:

| Temperature | Dry (<30%) | Normal (30–<70%) | Humid (≥70%) |
| --- | --- | --- | --- |
| Cold (<8) | Tundra | Snowy plain | Snowy forest |
| Temperate (8–<20) | Steppe | Plain | Forest |
| Hot (≥20) | Desert | Savanna | Jungle |

Generated values are illustrative: height is 0–1000, humidity is 0–100%, and temperature is approximately -18 to 40. They are not calibrated real-world measurements.

## Controls

- **Move:** W/A/S/D or arrow keys. Z moves up and Q moves left for AZERTY layouts.
- **Debug overlay:** F3 toggles debug mode on or off.

The window must have keyboard focus to receive these keys. Pressing F3 again hides the debug details and borders.

## Build and run

From the project root, run the PowerShell build script to create both distributable artifacts:

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1
```

The script writes `out/artifacts/Simulator.jar` and `out/artifacts/Simulator-Portable.exe`. The JAR requires Java to run. The single-file portable EXE bundles a Java runtime and extracts its files to a temporary directory when launched; it does not install the application. Creating it requires a JDK and the Windows IExpress tool.

To compile the classes manually instead, use:

PowerShell:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
```

Unix-like shell:

```sh
mkdir -p out
javac -d out $(find src -name '*.java')
```

Run with the default world ID (`upworld`) and a randomly selected seed:

```sh
java -cp out Main
```

Arguments:

```text
Main [useCustomWorldId] [worldId] [seed]
```

Set `useCustomWorldId` to `true` to use the supplied world ID. The seed is optional; if omitted, a random seed is chosen and printed at startup. Examples:

```sh
java -cp out Main
java -cp out Main true my-world 12345
java -cp out Main false ignored-id 12345
```

The application logs the world ID, seed, and generated properties of tile `(0, 0)` when it starts.

## World API

```java
World world = new World("my-world", 12345L);

Tile tile = world.getTile(-1, 0); // creates the needed chunk
System.out.println(tile.getBiome().getId());

tile.setHumidity(80.0);
world.setTile(-1, 0, tile);
```

Use `getChunk(chunkX, chunkY)` for chunk coordinates. `getTile(worldX, worldY)` and `setTile(worldX, worldY, tile)` use world tile coordinates. `world.update(deltaSeconds, keys)` advances the simulation and camera using elapsed seconds and the current keyboard state.

## Project layout

```text
src/
  Main.java
  simulator/
    displaying/
      Cell.java
      Frame.java
    simulation/
      entities/
        Camera.java
      world/
        Biome.java
        Biomes.java
        Chunk.java
        TerrainGenerator.java
        Tile.java
        World.java
    system/
      Ansi.java
      KeyListener.java
      Logger.java
```
