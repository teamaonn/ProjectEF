Philosopher's Stone (Fabric 26.3)
================================

All controls can be rebound under Options → Controls → Key Binds → ProjectE — Philosopher's Stone.

| Default | Action |
| --- | --- |
| V | Increase charge, up to level 5 |
| Sneak + V | Lower charge, down to level 1 |
| G | Cycle cube, panel and line area modes |
| R | Fire a mob-transmutation projectile; charge above level 1 first |
| C | Open the portable 3×3 crafting grid |
| Right click | Transmute the targeted block or source fluid |
| Sneak + right click | Use the alternate block conversion |

There is also an optional, initially unbound, separate Lower Stone Charge control.
Right-clicking air still opens the crafting grid when there is no eligible target.

The five charge levels affect 1×1×1 through 9×9×9 cubes, 1×1 through 9×9 panels,
or lines of 1 through 9 blocks. Only blocks matching the original target are
changed. Containers, unloaded chunks and areas where the player cannot build
are skipped. Log axes, leaf persistence and other shared block properties survive.

| Block | Right click | Sneak + right click |
| --- | --- | --- |
| Stone | Cobblestone | Grass block |
| Cobblestone | Stone | Grass block |
| Grass block | Sand | Cobblestone |
| Dirt | Sand | Sand |
| Sand | Grass block | Cobblestone |
| Gravel | Sandstone | Sandstone |
| Sandstone | — | Gravel |
| Glass | — | Sand |
| Netherrack | Cobblestone | Cobblestone |
| Short grass / fern | Dead bush | Dead bush |
| Melon / pumpkin | The other block | The other block |
| Dandelion / poppy | The other flower | The other flower |
| Red / brown mushroom | The other mushroom | The other mushroom |
| Water source | Ice | Ice |
| Lava source | Obsidian | Obsidian |
| Logs, wood, stripped logs/wood, leaves | Next wood family | Previous wood family |

Wood cycles cover oak, birch, spruce, jungle, acacia, dark oak, mangrove, cherry
and pale oak. Source fluids registered by another mod as `oil` or `crude_oil`
can also become ice; this mod does not register an oil fluid.

Mob transmutation keeps passive, neutral, hostile and arthropod groups separate.
It consumes 384 EMC of inventory fuel only after a replacement successfully
spawns. Misses, blocked shots and unsupported mobs consume nothing. Coal,
charcoal, redstone, glowstone, gunpowder and ProjectE fuels are accepted at
their configured EMC values. Overpayment is retained in the Stone for the next
conversion. That credit is included in the Stone's EMC price when selling or
recreating it. Creative players need no fuel. Tamed pets, leashed mobs and mobs
carrying passengers or riding another entity are preserved.

The Stone remains reusable in crafting, including its name, charge and fuel
credit. Existing alchemical crafting conversions and the Stone-centered table
recipe remain available.

This follows the EE2 Stone behavior described at:
https://ftb.fandom.com/wiki/Philosopher%27s_Stone_(Equivalent_Exchange_2)
Modern wood variants and cube/panel/line controls are adaptations for this port.
