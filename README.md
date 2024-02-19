This mod adds quality to food and certain food-related material (e.g. crops)
- Quality increases the nutrition and saturation food gives
- It also increases the duration, level and probability of beneficial effects while decreasing those values for harmful ones
  - Effects can be blacklisted from this behaviour with the mob effect tag `quality_food:blacklist`

There is a configuration per quality - aside from some normal things you can also specify which effects a quality should grant
- Example: `effect_list = ["minecraft:regeneration;0.5;120;3;0.45"]`
  - `minecraft:regeneration` is the effect
  - `0.5` is the chance for a food item to gain this effect (`1` means 100%)
  - `120` is the duration in ticks (`20` ticks means 1 second)
  - `3` is the amplifier (`0` results in an effect level of 1 (i.e. no level shown))
  - `0.45` is the probability to gain this effect when eating the item (`1` means 100%)

This does not introduce new items - the quality is based on item tags and should therefor be compatible with most items by default
- There is a whitelist item tag to allow non-food items to gain quality: `quality_food:material_whitelist`, it contains:
  - `#forge:eggs`
  - `#forge:crops`
  - `#forge:dough`
  - `#farmersdelight:wild_crops`
  - `minecraft:sugar`
- Certain block (items) are supported by default
  - `CakeBlock`
  - `CandleCakeBlock`
  - `StemGrownBlock` (e.g. `MelonBlock` or `PumpkinBlock`)
  - `FeastBlock` (e.g. `farmersdelight:roast_chicken_block`)

You can give yourself a quality item like this: `/give @s farmersdelight:roast_chicken_block{quality_food:{quality:2}}`
- `quality` is a value between `0` (`NONE`) and `3` (`DIAMOND`)
- Setting `NONE` does not make sense since it will not affect anything and just makes the item unstackable with non-quality items

Quality of items can impact the result of a newly crafted item (if said newly crafted item is applicable to quality)
- the luck stat also improves the chance for (higher) quality

![Example](https://i.imgur.com/hUnpNUh.png)

Credits for the quality icons go to https://twitter.com/concernedape