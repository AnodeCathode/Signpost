package gollorum.signpost.core;

import gollorum.signpost.core.blocks.TestBlock;
import gollorum.signpost.core.util.SubSystem;
import gollorum.signpost.forge.BlockHandler;

public class Blocks implements SubSystem {
	
	public static final Blocks INSTANCE = new Blocks();
	private Blocks() {}
	
	public void preInit() {
		BlockHandler.INSTANCE.addBlock(new TestBlock());
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

}
