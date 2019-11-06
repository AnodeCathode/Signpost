package gollorum.signpost;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import gollorum.signpost.blocks.tiles.BasePostTile;
import gollorum.signpost.blocks.tiles.BigPostPostTile;
import gollorum.signpost.blocks.tiles.PostPostTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TileEntityHandler {

	private static final Map<String, Class<? extends TileEntity>> TILE_CLASSES;
	
	static {
		Map<String, Class<? extends TileEntity>> mutableSet = new HashMap<String, Class<? extends TileEntity>>();
		mutableSet.put("SignpostBaseTile", BasePostTile.class);
		mutableSet.put("SignpostPostTile", PostPostTile.class);
		mutableSet.put("SignpostBigPostTile", BigPostPostTile.class);
		TILE_CLASSES = Collections.unmodifiableMap(mutableSet);
	}
	
	public static final TileEntityHandler INSTANCE = new TileEntityHandler();
	
	private TileEntityHandler() {}

	@SubscribeEvent
	public void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> evt) {
		for(Entry<String, Class<? extends TileEntity>> entry : TILE_CLASSES.entrySet()) {
			evt.getRegistry().register(TileEntityType.Builder.create(() -> {
				try {
					return entry.getValue().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException("TileEntity type "+entry.getValue()+" must provide a parameterless constructor");
				}
			}).build(null).setRegistryName(Signpost.MODID, entry.getKey()));
		}
	}
	
}
