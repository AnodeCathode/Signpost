package gollorum.signpost.core;

import gollorum.signpost.core.util.SubSystem;
import gollorum.signpost.forge.BlockHandler;
import gollorum.signpost.forge.TileHandler;

public class SignpostCore {
	
	public static final String MODID = "signpost";
	public static final String VERSION = "2.00.0";
	public static final String NAME = "SignPost";
	
	private static final SubSystem[] subSystems = { Blocks.INSTANCE, Tiles.INSTANCE, BlockHandler.INSTANCE, TileHandler.INSTANCE };

	public static void preInit() {
		for(SubSystem system : subSystems) system.preInit();
	}

	public static void init() {
		for(SubSystem system : subSystems) system.init();
	}

	public static void postInit() {
		for(SubSystem system : subSystems) system.postInit();
	}

}
