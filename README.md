# General
This mod adds quality to food and certain food-related material (e.g. crops) through data components

Quality can:
- Increase nutrition and saturation
- Improve positive effects and diminish (or outright remove) negative effects gained from eating food
- Impact the result of crafted (quality applicable) items (the `minecraft:generic.luck` will increase the amount of rolls)

---

Quality Types are defined per datapack (`/<namespace>/quality_types/`), syntax example:

```json
{
  "amplifier_modifier": 1,
  "chance": 0.1,
  "cooking_bonus": 0.00390625,
  "crafting_bonus": 0.15,
  "duration_multiplier": 1.5,
  "effects": [
    {
      "applicable_to": "#c:foods/fruit",
      "effects": [
        {
          "effect": {
            "effect": {
              "amplifier": 2,
              "duration": 100,
              "id": "minecraft:absorption",
              "neoforge:cures": [
                "protected_by_totem",
                "milk"
              ],
              "show_icon": true
            },
            "probability": 0.6
          },
          "chance": 0.5
        },
        {
          "effect": {
            "effect": {
              "duration": 40,
              "id": "minecraft:regeneration",
              "neoforge:cures": [
                "protected_by_totem",
                "milk"
              ],
              "show_icon": true
            },
            "probability": 0.2
          },
          "chance": 1
        }
      ]
    }
  ],
  "icon": "quality_food:quality_icon/iron",
  "level": 1,
  "nutrition_multiplier": 1.5,
  "probability_multiplier": 1.25,
  "saturation_multiplier": 1.25
}
```
- `level`: The level of the effect - higher level ones will be rolled first
- `chance`: Chance of this quality being applied
  - Value between `0` and `1` (1 being 100%)
- `nutrition_multiplier`: The amount the nutrition will be multiplied by
- `saturation_multiplier`: The amount the saturation will be multiplied by
- `icon`: The icon shown for the quality (texture needs to be in the `textures/gui/sprites` directory)
- (Optional) `duration_multiplier`: The amount the duration of existing beneficial effects will be multiplied by (or divided if the effect is harmful)
  - If the duration reaches 0 during modifications the effect will be removed
- (Optional) `probability_multiplier`: The amount the probability of the beneficial effect will be multiplied by (or divided if the effect is harmful)
  - If the probability reaches 0 during modifications the effect will be removed
- (Optional) `amplifier_multiplier`: The amount the amplifier of the beneficial effect will be increased by (or decreased if the effect is harmful)
  - If the amplifier goes below 0 during modifications the effect will be removed
- (Optional) `crafting_bonus`: Additive bonus to the chance of rolling for quality (actual bonus will be `crafting_bonus / quality_applicable_ingredients`)
  - **Example**: 1x diamond quality (bonus of 0.7) + 1x iron quality (bonus of 0.15) ingredients = (0.7 / 2) + (0.15 / 2) = total bonus of 0.425
  - Value between `0` and `1` (1 being 100%)
- (Optional) `cooking_bonus`: The bonus each cooked item of this quality will provide for the stored bonus of the cooking block entity
  - Value between `0` and `1` (1 being 100%)
- (Optional) `effects`: List of effect configurations
  - (Optional) `applicable_to`: The item (or item tag) these effects will be applied to - if empty it will be applied to all items
  - `effects`: List of effects which will be applied
    - `effect`: The effect (see the single effect [here](https://minecraft.wiki/w/Data_component_format#food))
    - (Optional) `chance`: The chance this effect will be applied to the item when the quality is applied
      - Value between `0` and `1` (1 being 100%)
      - If not supplied the value will be `1` 

The actual quality data component which gets attached to items looks like this:

```json
{
  components: {
    "quality_food:quality": {
      level: 2,
      type: "quality_food:gold"
    }
  },
  count: 1,
  id: "minecraft:apple"
}
```

# Commands
- `/quality_food give`: Gives you an item with the defined quality (only lists item which are applicable to quality)
- `/quality_food apply`: Applies a quality to an item
- `/quality_food remove`: Removes quality from an item

# Configuration
There is a farmland configuration which allows you to define a bonus (can also be negative, i.e. 0.5) based on the farm block the crop is planted on
- Example: `farmland_config = ["3;#minecraft:crops;farmersdelight:rich_soil_farmland;1.25"]`
  - `3` is the index (configurations are tested with the lowest one first - the first matching one will be applied) (needs to be positive)
  - `#minecraft:crops` is the crop block (can be a tag or a single block)
  - `farmersdelight:rich_soil_farmland` is the farmland block (can be a tag or a single block)
  - `1.25` is the multiplier to be applied (needs to be positive - values below `1` will reduce the chance)

---

All items with food properties should be applicable to quality by default
- For other items (e.g. materials) the item tag `quality_food:material_whitelist` is used
- In case some items should not have quality applied the item tag `quality_food:blacklist` is used

Only blocks within the block tag `quality_food:quality_blocks` will support (i.e. retain) quality
- If a block is applicable to quality the item for said block will be too

---

For crafting (crafting table) there are three configs:
- `retain_quality_recipes`: The result will retain the quality of the ingredients, examples:
  - If all items are `diamond` quality the result will be `diamond`
  - If three items are `gold` quality and two are `diamond` the result will be `gold`
  - If two items are `iron` quality and the rest have none then the result will also have none
- `no_quality_recipes`: Entries will not roll for quality (useful in case items can be crated back and forth)
- `handle_compacting`: If enabled then (de)compacting results should retain quality automatically without having to specify the relevant recipes

Cooking quality items will store a quality bonus within the furnace / cooking pot / ...
- The bonus is defined in the quality type with `cooking_bonus`
- Once enough bonus is stored particles will start to show
  - A higher bonus results in more particles
  - This can be disabled through the client config
- Once you take out the result the stored bonus will be used up and grant a higher chance to a quality result

# Compatibility
- [Jade](https://www.curseforge.com/minecraft/mc-mods/jade)
  - Quality tooltip
- [Tom's Simple Storage Mod](https://www.curseforge.com/minecraft/mc-mods/toms-storage)
  - Apply quality when crafting
- [Harvest with ease](https://www.curseforge.com/minecraft/mc-mods/harvest-with-ease)
  - Properly roll quality when auto harvesting
- [RightClickHarvest](https://www.curseforge.com/minecraft/mc-mods/rightclickharvest)
  - Properly roll quality when auto harvesting
- [FastWorkbench](https://www.curseforge.com/minecraft/mc-mods/fastworkbench)
  - Quality gets properly handled when using the crafting bench

This is mostly about block interaction / quality application through crafting
- If a mod adds a new crafting block then quality may not apply correctly
- Items should generally be fine

# Misc

![Example](https://i.imgur.com/hUnpNUh.png)

Credits for the quality icons go to https://twitter.com/concernedape