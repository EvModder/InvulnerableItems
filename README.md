# InvulnerableItems
Prevent certain items from taking damage from certain damage causes.\
**TODO**: Add an NBT tag per damage-cause that can be used for more specific items (instead of applying to all items of a given type).

#### Example config:
<pre>
invulnerable-to-lava: [head:strider, nether_quartz_ore]
invulnerable-to-cactus: [netherite_sword, netherite_axe]

# lists can also be written like this
invulnerable-to-fire:
  - magma_cream
  - netherrack
  - blaze_rod
</pre>
