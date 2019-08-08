package gollorum.signpost.core.blocks;

import gollorum.signpost.core.util.BlockPos;
import gollorum.signpost.core.util.Vector3;
import gollorum.signpost.forge.BlockBase;
import gollorum.signpost.forge.MaterialType;
import gollorum.signpost.forge.TileBase;

public class TestBlock extends BlockBase{
	
	private static final String NAME = "TestBlock";
	private static final HarvestLevel HARVEST_LEVEL = HarvestLevel.PICKAXE;
	private static final int HARDNESS = 1;
	private static final int resistance = 10;
	private static final MaterialType MATERIAL = MaterialType.ROCK;

	public TestBlock() {
		super(NAME, NAME, HARVEST_LEVEL, HARDNESS, resistance, MATERIAL);
	}

	@Override
	protected TileBase buildTile() {
		return new TestBlockTile();
	}

	@Override
	protected boolean rightClick(BlockPos blockPos, Vector3 hitPos) {
		System.out.println("HELP! I got clicked D:");
		return false;
	}

}
