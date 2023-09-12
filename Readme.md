# CropChance

[EN](Readme.md)|[中文](Readme_zh.md)

A mod for getting IC2 crop related information.

## Dependencies

- industrialcraft-2(https://www.curseforge.com/minecraft/mc-mods/industrial-craft)
- UniMixins(https://www.curseforge.com/minecraft/mc-mods/unimixins)

## Usage

### Tick

`/crop tick <tick count>`

Use this command to speed up the crop's hybridization, growing process,
or you can speed up the spread of the weed.

![Tick.png](docs/Tick.png)

Previously, there were four wheat crops surrounding the hybrid rack.
After 100 ticks, it grew weeds and spread to the nearby crop racks.

By examining the internal code, I found a bug where the weeds would only
spread to the crop racks at `z -1` or `x -1` relative to it.

```java
class TileEntityCrop {
  public void generateWeed() {
    int x = this.xCoord;
    int y = this.yCoord;
    int z = this.zCoord;
    switch (IC2.random.nextInt(4)) {
      case 0:
        ++x;
        //Missing a break here
      case 1:
        --x;
        //Missing a break here
      case 2:
        ++z;
        //Missing a break here
      case 3:
        --z;
        //Missing a break here
    }
  }
}
```

### Crop Set

`/crop set <growth | gain | resistance | nutrient | water | weedex | size | scan>`

Use this command to set various parameters for the crop.

### Crop Info

`/crop show`

Use this command to show various parameters for the crop.

![ShowInfo.png](docs/ShowInfo.png)

In addition to the information that can be viewed through the crop analyzer, you can also view information such as the
crop’s demand points, the number of growth points provided by the environment, and information about the crop’s growth
rate and growth time.

### Biomes info

`/crop info biome`

Use this command to view the information of current biomes.

![BiomeInfo.png](docs/BiomeInfo.png)

`/crop info biomes`

Use this command to view the information of all biomes.

![BiomesInfo.png](docs/BiomesInfo.png)

`/crop info types`

Use this command to view information of all biome types.

![BiomeTypesInfo.png](docs/BiomeTypesInfo.png)

### TickRate info

`/crop info tickrate`

Use this command to check how many game ticks it takes for crops to update each time.

![TickRate.png](docs/TickRate.png)

### CropCard info

`/crop cropcard`

Use this command to view information of all crop-cards.

![CropCard.png](docs/CropCard.png)

### Crop Chance

`/crop chance <try>`

Use this command to try to do crossbreeding on your targeted hybrid frame.

![Chance.png](docs/Chance.png)

### Cross Chance info

`/crop cross <try> <growth> <surround>`

Use this command to simulate crop hybridization;
this command is only used to analyze whether hybridization occurs, and cannot analyze the situation of hybrid products.

- try - Number of simulation attempts;
- growth - Sets the `growth` of the parent plants;
- surround - Sets how many plants surrounds;

The higher the `growth` attribute, the higher the probability of hybridization;
the more crops nearby, the higher the probability of hybridization.

| Growth | Chance |
|:------:|:------:|
|  <16   |  20%   |
|  <30   |  25%   |
|  >=30  |  30%   |

Note: When the `resistance` attribute of the parent crop exceeds `27` points,
each additional point reduces the probability of hybridization by `5%`.

Note: When the `growth` attribute of a crop is >=`24`, it will be considered as a `weed`,
that is, it will produce `weeds` like `weeds` on nearby crop racks;
but if the crops on the nearby crop racks also have a `growth` attribute >=`24`, they will not be transformed into
real `weeds`.

Growth = 15, Cross is about 2%

![Cross-15-2.png](docs/Cross-15-2.png)

Growth = 16, Cross is about 3%

![Cross-16-2.png](docs/Cross-16-2.png)

The following chart shows the change in the probability of hybridization over time.

The legend is the number of crops near the hybridization rack.

Growth < 16

![Growth<16.png](docs/Growth-16.png)

Growth>=16

![Growth>=16.png](docs/Growth+16.png)

Growth>=30

![Growth>=30.png](docs/Growth+30.png)
