# General
This mod adds quality to food and certain food-related material (e.g. crops) through NBT data (meaning no new items)

1.21 documentation is currently located [here](https://github.com/SiverDX/quality_food/tree/1.21)

Quality can:
- Increase nutrition and saturation
- Improve positive effects and diminish (or outright remove) negative effects gained from eating food
- Impact the result of crafted (quality applicable) items (the `minecraft:generic.luck` attribute increases the chance as well)

## Animals
Can be fed with quality food (that they would normally eat), improving their potential

Said potential will increase the chances for quality loot (and also the outcome of milking cows or chickens laying egggs)

Baby animals will gain (using default values) ~ 60% to 90% of the parents' potential

---

You can give yourself a quality item like this: `/give @s farmersdelight:roast_chicken_block{quality_food:{quality:2}}`
- `quality` is a value between `0` (`NONE`) and `3` (`DIAMOND`)
- Setting `NONE` does not make sense since it will not affect anything and just makes the item unstackable with non-quality items

You can also use the following commands 
- `/quality_food give`
- `/quality_food apply`
- `/quality_food remove`

# Configuration
There is a configuration per quality - aside from some normal things you can also specify which item should gain which effects
- Example: `effect_list = ["minecraft:apple;minecraft:regeneration;0.5;120;3;0.45"]`
  - `minecraft:apple` is the item this effect should apply to (can also be a tag, e.g. `#minecraft:cat_food`)
  - `minecraft:regeneration` is the effect
  - `0.5` is the chance for a food item to gain this effect (`1` means 100%)
  - `120` is the duration in ticks (`20` ticks means 1 second)
  - `3` is the amplifier (`0` results in an effect level of 1 (i.e. no level shown))
  - `0.45` is the probability to gain this effect when eating the item (`1` means 100%)

The tag of an item with effects applied to it looks like this:
```json
{
  quality_food: {
    effects: [
      {
        "forge:id": "minecraft:regeneration", 
        chance: 0.45d, 
        Ambient: 0b, 
        CurativeItems: [
          {
            id: "minecraft:milk_bucket", 
            Count: 1b
          }
        ], 
        ShowIcon: 1b, 
        ShowParticles: 1b, 
        Duration: 120, 
        Id: 10, 
        Amplifier: 3b
      }
    ], 
    quality: 1
  }
}
```

I don't recommend manually creating such items - the `quality_food` commands will apply the configured effects

---

There is a farmland configuration which allows you to define a bonus (can also be negative, i.e. 0.5) based on the farm block the crop is planted on
- Example: `farmland_config = ["3;#minecraft:crops;farmersdelight:rich_soil_farmland;1.25"]`
  - `3` is the index (configurations are tested with the lowest one first - the first matching one will be applied) (needs to be positive)
  - `#minecraft:crops` is the crop block (can be a tag or a single block)
  - `farmersdelight:rich_soil_farmland` is the farmland block (can be a tag or a single block)
  - `1.25` is the multiplier to be applied (needs to be positive - values below `1` will reduce the chance)

---

Non-food items can be made applicable to quality by adding them to the `quality_food:material_whitelist` item tag

---

Only blocks within the block tag `quality_food:quality_blocks` will support (i.e. retain) quality
- If a block is applicable to quality the item for said block will be too

---

For crafting (crafting table) there are these configs:
- `retain_quality_recipes`: The result will retain the quality of the ingredients, examples:
  - If all items are `diamond` quality the result will be `diamond`
  - If three items are `gold` quality and two are `diamond` the result will be `gold`
  - If two items are `iron` quality and the rest have none then the result will also have none
- `no_quality_recipes`: Entries will not roll for quality (useful in case items can be crated back and forth)
- `storage_recipe_blacklist`: Entries will be excluded from the automatic storage block detection

The bonus quality a quality ingredient provides is configurable for the crafting table (`crafting_bonus`)

Cooking quality items will store a quality bonus within the furnace / cooking pot / ...
- Higher quality will store a higher bonus per cooked item
- Once enough bonus is stored particles will start to show
  - A higher bonus results in more particles
  - This can be disabled through the client config
- Once you take out the result the stored bonus will be used up and grant a higher chance to a quality result

---

For compatibility’s sake certain blocks (and their item variant) are supported in a broader way than needed 

If you find some items having quality where it doesn't make much sense you can blacklist them using the item tag `quality_food:blacklist`

# Compatibility
There are a lot of mod-specific compatibilities already added

Usually the problems are related to:
- Crafting
- Blocks (crops e.g.) drop items in a specific way
- Crops that grow in a special way (e.g., two blocks high)

These things are handled automatically
- Food items (others need to be added to the tags mentioned above)
- Most recipes for compacting (storage-blocks)

# Misc

![Example](https://i.imgur.com/hUnpNUh.png)

Credits for the quality icons go to https://twitter.com/concernedape