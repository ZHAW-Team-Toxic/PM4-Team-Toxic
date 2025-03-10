# Tiled Documentation Kevin

## Map

### What is a Map?

## Layers

### What are Layers?

Layers are the different levels of the map. They are used to separate different parts of the map. For example, the ground floor, the decorations, the resources, the enemies, etc.
This makes it easier to get a list of all the objects in a specific layer.
Also when for example constructing a building you can easily select the building layer and draw the building on that layer.
While enemies can be drawn on the enemy layer.

### How to select a Layer in Java?

To select a layer in Java, you can use the following code:

```java

TiledMap map = new TmxMapLoader().load("map.tmx");
TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("layerName");

//or

TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);

```
If you have a set-up where you have multiple layers and you want a specific tile in a layer you can do this:

```java

TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("layerName");
Cell cell = layer.getCell(x, y);

``` 
You can also do this in a for-loop:

```java

TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("layerName");
for (int x = 0; x < layer.getWidth(); x++) {
    for (int y = 0; y < layer.getHeight(); y++) {
        Cell cell = layer.getCell(x, y);
    }
}

```

## Tilesets

### What are Tilesets?

Tilesets are the images that contain the tiles. They are used to draw the map. The tiles are the images that are used to draw the map.


## Objects


### What are Objects?


## Properties


### Map Properties


### Tile Properties



## Automapping



## Terrain-Sets








