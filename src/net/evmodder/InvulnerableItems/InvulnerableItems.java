package net.evmodder.InvulnerableItems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.meta.SkullMeta;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import net.evmodder.EvLib.bukkit.HeadUtils;
import net.evmodder.EvLib.bukkit.EvPlugin;

public final class InvulnerableItems extends EvPlugin implements Listener{
	private HashMap<DamageCause, Set<Material>> typeMap;
	private HashMap<DamageCause, Set<UUID>> headMap;
	private final Set<Material> EMPTY_TYPE_MAP = ImmutableSet.of();
	private final Set<UUID> EMPTY_HEAD_MAP = ImmutableSet.of();


	@Override public void onEvEnable(){
		typeMap = new HashMap<>();
		headMap = new HashMap<>();
		for(DamageCause cause : DamageCause.values()){
			final List<String> itemNames = config.getStringList("invulnerable-to-"+cause.name().toLowerCase());
			if(itemNames == null || itemNames.isEmpty()) continue;
			Set<Material> types = new HashSet<>();
			Set<UUID> uuids = new HashSet<>();
			for(String itemName : itemNames){
				itemName = itemName.toUpperCase();
				if(itemName.startsWith("HEAD:")){
					uuids.add(UUID.nameUUIDFromBytes(itemName.substring(5).getBytes()));
				}
				else{
					try{types.add(Material.valueOf(itemName));}
					catch(IllegalArgumentException ex){getLogger().warning("Unknown item: "+itemName);}
				}
			}
			if(!types.isEmpty()) typeMap.put(cause, types);
			if(!uuids.isEmpty()) headMap.put(cause, uuids);
		}

		getServer().getPluginManager().registerEvents(this, this);
	}

	boolean isProtectedFrom(Item item, DamageCause damage){
		if(item == null || item.getItemStack() == null) return false;
		if(typeMap.getOrDefault(damage, EMPTY_TYPE_MAP).contains(item.getItemStack().getType())) return true;
		if(item.getItemStack().getType() != Material.PLAYER_HEAD || !item.getItemStack().hasItemMeta()) return false;
		final GameProfile profile = HeadUtils.getGameProfile((SkullMeta)item.getItemStack().getItemMeta());
		return profile != null && headMap.getOrDefault(damage, EMPTY_HEAD_MAP).contains(profile.getId());
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemDamage(EntityDamageEvent evt){
		if(evt.getEntity() instanceof Item && isProtectedFrom((Item)evt.getEntity(), evt.getCause())){
			getLogger().finer("Cancelled damage for: "+((Item)evt.getEntity()).getType()+", rsn: "+evt.getCause());
			evt.setCancelled(true);
		}
	}
}