package gollorum.signpost.forge;

import gollorum.signpost.core.SignpostCore;
import gollorum.signpost.core.util.BlockPos;
import gollorum.signpost.core.util.Vector3;
import net.minecraft.block.BlockContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockBase extends BlockContainer {
	
	public final String registryName;

	protected BlockBase(String name, String registryName, HarvestLevel harvestLevel, int hardness, int resistance, MaterialType material) {
		super(material.GetTarget());
		this.registryName = registryName;
		this.setHarvestLevel(harvestLevel.keyword, 1);
		this.setHardness(hardness);
		this.setResistance(resistance);
		setCreativeTab(CreativeTabs.tabTransport);
		setBlockName(name);
		setBlockTextureName(SignpostCore.MODID + ":base");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return buildTile();
	}
	
	@Override
	public boolean onBlockActivated(
		World worldIn, 
		int x, int y, int z, 
		EntityPlayer playerIn, 
		int side, 
		float hitX, float hitY, float hitZ
	) {
		return rightClick(new BlockPos(x, y, z), new Vector3(hitX, hitY, hitZ));
	}
	
	protected abstract TileBase buildTile();
	protected abstract boolean rightClick(BlockPos blockPos, Vector3 hitPos);
	
	public enum HarvestLevel {
		AXE("axe"),
		PICKAXE("pickaxe"),
		SHOVEL("shovel");
		
		private String keyword;
		
		private HarvestLevel(String keyword) {
			this.keyword = keyword;
		}
	}

}
