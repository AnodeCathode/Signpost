package gollorum.signpost.core;

import gollorum.signpost.core.blocks.TestBlockTile;
import gollorum.signpost.core.util.SubSystem;
import gollorum.signpost.forge.TileHandler;

public class Tiles implements SubSystem {
	
	public static final Tiles INSTANCE = new Tiles();
	private Tiles() {}
	
	public void preInit() {
		TileHandler.INSTANCE.addTile(TestBlockTile.class, "TestBlockTile");
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

}
