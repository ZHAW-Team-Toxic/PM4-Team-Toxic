# Conventions for Tiled

This document describes the conventions used in the Tiled map editor for the for the project Frontier.
Since we'll be working with multiple people on the same maps, it's important to have a consistent way of working.

## Layers

The amount of layers may change as further knowledge has been gained. But for now I suggest for now 6 layers:

1. `BottomLayer:` This layer is for the background of the map. It should contain the ground, water, etc.
   - **Concerned Tiles:** Grass Tiles and variations of it, water tiles, Forest Grass Tiles, etc.
   

2. `DecorationLayer:` This layer is for the decoration of the map. It should contain trees, rocks, etc. (which are not resources)
   - **Concerned Tiles:** Signe Trees, small rocks, grass, flowers, etc.
   

3. `ResourceLayer:` This layer is for the resources of the map. It should contain trees, rocks, etc. (which are resources)
   - **Concerned Tiles:** Trees, rocks, iron ores / mountains, etc.
   

4. `BuildingLayer:` This layer is for the buildings of the map. It should contain resource-buildings, HQ, defense buildings, etc.
   - **Concerned Tiles:** Towers, HQ, Mine, etc.
   

5. `EnemyLayer:` This layer is for the enemies of the map. It should contain the enemies, their paths, etc.
   - **Concerned Tiles:** Enemies
   

6. `TopLayer:` This layer is for the top of the map. It should only me a marker layer with nothing in it.
    - **Concerned Tiles:** None

## Files

All files should be named in a consistent way. This makes it easier to find the files you need.
Put all files matching with the same ending in the same folder.

## Tilesets

The tilesets are the images that contain the tiles. The tilesets should be named as follows:

- `bottomLayerTileset`
- `decorationLayerTileset`
- `resourceLayerTileset`
- `buildingLayerTileset`
- `enemyLayerTileset`
- `topLayerTileset`

If more are needed and for the sake of visibility numbers can be added to the end of the name.

- `bottomLayerTileset1, bottomLayerTileset2, etc.`

### Sprite Sheets (PNG)

Sprite sheets are the images that contain the tiles. They should be named as follows:

- `tilesetName.png`

### TMX Files

TMX files are the files that contain the map data. It is native to the Tiled map editor and there exists a TMX-Loader for libGDX.
The TMX files should be named as follows:

- `mapName.tmx`

### TSX Files

TSX files are the files that contain the tileset data. It is native to the Tiled map editor and there exists a TMX-Loader for libGDX.
The TSX files should be named as follows:

- `tilesetName.tsx`

It should have the same name as the sprite sheet.

## Objects

Objects are the entities that are placed on the map. They should be named as follows:

- `objectName`

If more are needed and for the sake of visibility numbers can be added to the end of the name.

- `objectName1, objectName2, etc.`

## Properties

Properties are the data that is attached to the objects. They should be named as follows:

- `propertyName`

If more are needed and for the sake of visibility numbers can be added to the end of the name.

- `propertyName1, propertyName2, etc.`






