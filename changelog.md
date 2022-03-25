[6.1.12]
- Updated to work with latest UnionLib version
- Fix crash when breaking a temperature regulator with a plate inside
- Optimized the ServerboundThirstMovementPacket

[6.1.11]
- If the experimental temperature system is enabled, the hyperthermia and hypothermia timers should reset if the player already has the effect and the timers will no longer tick down
- Fixed plated temp regulator not dropping itself or the plates inside
- Moved ru_ru.json to its correct spot

[6.1.10]
- Made JsonHolder an interface and moved it to the API
- Re-enabled brewing recipes
- updated ru_ru.json (Credits to jolopapina)

[6.1.9]
- Fix failure to load on dedicated servers

[6.1.8]
- Formally created an API and added a TemperatureEmmiter interface
- Added an icon for the Slowness Illness effect
- Added Temperature Regulator block and Regulator plates
- Fix stamina not regenerating over time
- Fix the wellbeing module saving into the nutrition module
- Added a needs command
- Riding a stationary entity increases stamina by 1 every 20 seconds
- Riding a moving entity decreases stamina every 20 seconds
- Fix incompatibility with Quark

[6.1.7]
- Gave stamina its dedicated config folder
- Fix stink particles showing in creative and spectator
- Fix thirst and stamina overlays covering chat
- Fix Potash Solution color in cauldron not reflecting the bottles color
- Made Max Stamina configurable as an attribute

[6.1.6]
- Fix Potash Solution crafting duplicating Glass Bottles
- Gave the Charcoal Filter a durability of 10
- Restore previous charcoal filtering recipe for the bucket and bowl
- Increased Potash drop chance when left under the sun
- Fix Potash not spawning in the middle of the cauldron
- Fix washcloths and sponges not actually cleaning the player

[6.1.5]
- Fix the player becoming instantly clean when wet
- Made the squeaky clean status last for a lesser amount of time
- Fixed immediately getting hyp(o/er)thermia in experimental settings
- Updated ru_ru.json (Credits to mirkiv224)
- Charcoal filtering recipes are now shown in JEI
- Used a shapeless recipe instead of a charcoal filter one for purified water buckets and bowls
- Fixed potions being smeltable in a furnace
- Fixed purified water block model
- Cause sickness in the player if too much bad water is consumed
- Fix experimental hyp(o/er)thermia not dealing damage. It should now reduce health up to 1/4 of your max health

[6.1.4]
- Added a recipe for the thermometer
- Getting wet in a cold ocean will lower your temperature by an extra 50%
- Getting wet in a frozen ocean, frozen peaks, frozen river, ice spikes and any of the vanilla snowy biomes will drop your temperature twice as fast

[6.1.3]
- Made getting wet in a desert less effective
- Allowed the modifier for wetness to be adjustable via datapacks
- Being underwater increases the rate the player accumulates wetness
- Avoided checking the player's temperature if they're no longer alive
- Update vi_vn.json (Thanks to AnLeRIP2310) 

[6.1.2]
- Made ice cubes drop only when pickaxes are used

[6.1.1]
- Update zh_cn.json (Thanks to StarchierOrb)
- Updated translation files to include the depreciated hypo/hyperthermia effect

[6.1.0]
- Temperature, Thirst & Stamina overlay is now handled by the forge overlay system
- Removed an extra pixel in the energy bars
- Re-added support for serene seasons
- Split temperature modifiers between internal and environmental modifiers
- Added a thermometer
- Prevented Hygiene related recipes from loading if hygiene is disabled

[6.0.1]
- Fix crash when using UnionLib 6.1.5

[6.0.0]
- Made initial port to 1.18.1