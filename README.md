This mod adds quality to food and certain food-related material (e.g. crops)
- Quality increases the nutrition and saturation food gives
- It also increases the duration, level and probability of beneficial effects while decreasing those values for harmful ones
  - Effects can be blacklisted from this behaviour with the effect tags `quality_food:beneficial_blacklist` and `quality_food:harmful_blacklist`

This does not introduce new items - the quality is based on item tags and should therefor be compatible with most items by default
- There is a whitelist item tag to allow non-food items to gain quality: `quality_food:material_whitelist`, it contains:
  - `#forge:eggs`
  - `#forge:crops`
  - `#forge:dough`
  - `#farmersdelight:wild_crops`
  - `minecraft:sugar`

You can give yourself a quality item like this: `/give @s farmersdelight:roast_chicken_block{food_quality.quality:{has_quality:1b,quality:2}}`
- `has_quality` is used to make sure there was actually a quality set
- `quality` is a value between `0` (`NONE`) and `3` (`DIAMOND`)

Quality of items can impact the result of a newly crafted item (if said newly crafted item is applicable to quality)
- the luck stat also improves the chance for (higher) quality

![Example](https://i.imgur.com/hUnpNUh.png)

Credits for the quality icons go to https://twitter.com/concernedape