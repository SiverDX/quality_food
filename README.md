# General
This mod adds quality to food and certain food-related material (e.g. crops) through NBT data (meaning no new items)

Quality can:
- Increase nutrition and saturation
- Improve positive effects and diminish (or outright remove) negative effects gained from eating food
- Impact the result of crafted (quality applicable) items (the `minecraft:generic.luck` attribute increases the chance as well)

---

You can give yourself a quality item like this: `/give @s farmersdelight:roast_chicken_block{quality_food:{quality:2}}`
- `quality` is a value between `0` (`NONE`) and `3` (`DIAMOND`)
- Setting `NONE` does not make sense since it will not affect anything and just makes the item unstackable with non-quality items

Or by using the quality command `/quality_food give`

# Configuration
There is a configuration per quality - aside from some normal things you can also specify which effects a quality should grant
- Example: `effect_list = ["minecraft:regeneration;0.5;120;3;0.45"]`
  - `minecraft:regeneration` is the effect
  - `0.5` is the chance for a food item to gain this effect (`1` means 100%)
  - `120` is the duration in ticks (`20` ticks means 1 second)
  - `3` is the amplifier (`0` results in an effect level of 1 (i.e. no level shown))
  - `0.45` is the probability to gain this effect when eating the item (`1` means 100%)

---

Non-food items can be made applicable to quality by adding them to the `quality_food:material_whitelist` item tag

---

There is support for a guaranteed quality application when crafting (e.g. turning a quality `minecraft:melon_slice` into `minecraft:melon_seed`) through the item tag `quality_food:recipe_conversion`

(De)compacting recipes are handled separately from this tag

---

For compatibility’s sake certain blocks (and their item variant) are supported in a broader way than needed 

If you find some items having quality where it doesn't make much sense you can blacklist them using the item tag `quality_food:blacklist`

# Compatibility
- [Farmer's Delight](https://www.curseforge.com/minecraft/mc-mods/farmers-delight)
- [Fast Entity Transfer](https://www.curseforge.com/minecraft/mc-mods/fastentitytransfer)
- [Tom's Simple Storage Mod](https://www.curseforge.com/minecraft/mc-mods/toms-storage)
- [Sophisticated Core](https://www.curseforge.com/minecraft/mc-mods/sophisticated-core)
  - No Furnace / Smoker upgrade support
- [Create](https://www.curseforge.com/minecraft/mc-mods/create)
  - Milling and Mechanical Mixer should apply the quality of the ingredients
- [Harvest with ease](https://www.curseforge.com/minecraft/mc-mods/harvest-with-ease)

This is mostly about block interaction / quality application through crafting
- If a mod adds a new crafting block then quality may not apply correctly
- Items should generally be fine

# Misc

![Example](https://i.imgur.com/hUnpNUh.png)

Credits for the quality icons go to https://twitter.com/concernedape