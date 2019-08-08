package gollorum.signpost.forge;

import java.util.HashSet;

import cpw.mods.fml.common.registry.GameRegistry;
import gollorum.signpost.core.util.SubSystem;

public class BlockHandler implements SubSystem {
	
	public static final BlockHandler INSTANCE = new BlockHandler();

	private final HashSet<BlockBase> blocks = new HashSet<BlockBase>();
	
	private BlockHandler() {}
	
	public void addBlock(BlockBase block) {
		blocks.add(block);
	}
	
	@Override
	public void preInit() {
		for(BlockBase block : blocks) {
			GameRegistry.registerBlock(block, block.registryName);
		}
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}
	
}
