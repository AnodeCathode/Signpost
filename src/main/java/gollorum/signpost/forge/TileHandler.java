package gollorum.signpost.forge;

import java.util.HashMap;
import java.util.Map.Entry;

import cpw.mods.fml.common.registry.GameRegistry;
import gollorum.signpost.core.util.SubSystem;

public class TileHandler implements SubSystem {

	public static final TileHandler INSTANCE = new TileHandler();

	private final HashMap<Class<? extends TileBase>, String>  tiles = new HashMap<Class<? extends TileBase>, String>();

	private TileHandler() {}
	
	public void addTile(Class<? extends TileBase> tile, String registryName) {
		tiles.put(tile, registryName);
	}
	
	@Override
	public void preInit() {
		for(Entry<Class<? extends TileBase>, String> now : tiles.entrySet()) {
			GameRegistry.registerTileEntity(now.getKey(), now.getValue());
		}
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}
	
}
