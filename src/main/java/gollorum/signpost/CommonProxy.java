package gollorum.signpost;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

import gollorum.signpost.blocks.tiles.BasePostTile;
import gollorum.signpost.blocks.tiles.BigPostPostTile;
import gollorum.signpost.blocks.tiles.PostPostTile;
import gollorum.signpost.management.PlayerStorage;
import gollorum.signpost.management.PlayerStore;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.worldGen.villages.VillageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CommonProxy {

	public BlockHandler blockHandler;
	protected ItemHandler itemHandler;
	
	public CommonProxy(){
		blockHandler = BlockHandler.INSTANCE;
		itemHandler = ItemHandler.INSTANCE;
	}
	
	void preInit(){
		MinecraftForge.EVENT_BUS.register(blockHandler);
		MinecraftForge.EVENT_BUS.register(itemHandler);
	}
	
	void init(){
		registerBlocksAndItems();
		registerCapabilities();
		registerHandlers();
		registerVillageCreation();
	} 
	   
	private void registerVillageCreation() {
		VillageHandler.getInstance().register();
	}

	private void registerHandlers() {
		NetworkHandler.register();
		MinecraftForge.EVENT_BUS.register(SPEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(TileEntityHandler.INSTANCE);
	}

	private void registerBlocksAndItems() {
		blockHandler.init();
		blockHandler.register();

		itemHandler.init();
		itemHandler.register();
	}

	protected void registerCapabilities() {
		CapabilityManager.INSTANCE.register(PlayerStore.class, new PlayerStorage(), PlayerStore::new);
	}

	public InputStream getResourceInputStream(String location){
		return getClass().getResourceAsStream(location);
	}
}
