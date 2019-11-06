package gollorum.signpost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import gollorum.signpost.blocks.BaseModelPost;
import gollorum.signpost.blocks.BasePost;
import gollorum.signpost.blocks.SuperPostPost;
import gollorum.signpost.blocks.WaystoneContainer;
import gollorum.signpost.blocks.tiles.BasePostTile;
import gollorum.signpost.blocks.tiles.SuperPostPostTile;
import gollorum.signpost.items.CalibratedPostWrench;
import gollorum.signpost.items.PostWrench;
import gollorum.signpost.management.ClientConfigStorage;
import gollorum.signpost.management.PlayerProvider;
import gollorum.signpost.management.PlayerStore;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.management.PostHandler.PlayerRestrictions;
import gollorum.signpost.management.WorldSigns;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.InitPlayerResponseMessage;
import gollorum.signpost.network.messages.SendAllBigPostBasesMessage;
import gollorum.signpost.network.messages.SendAllPostBasesMessage;
import gollorum.signpost.util.BoolRun;
import gollorum.signpost.util.CollectionUtil;
import gollorum.signpost.util.MyBlockPos;
import gollorum.signpost.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class SPEventHandler {

	private static Map<Runnable, Integer> serverTasks = new HashMap<Runnable, Integer>();
	private static Collection<BoolRun> serverPredicatedTasks = new ArrayList<BoolRun>();
	
	private static Map<Runnable, Integer> clientTasks = new HashMap<Runnable, Integer>();
	private static Collection<BoolRun> clientPredicatedTasks = new ArrayList<BoolRun>();

	public static final SPEventHandler INSTANCE = new SPEventHandler();
	private SPEventHandler(){}

	/**
	 * Schedules a task
	 * 
	 * @param task
	 *            The task to execute
	 * @param delay
	 *            The delay in ticks (1s/20)
	 */
	public static void scheduleTask(Runnable task, int delay) {
		switch(EffectiveSide.get()){
		case SERVER:
			serverTasks.put(task, delay);
			return;
		case CLIENT:
			clientTasks.put(task, delay);
			return;
		}
	}

	public static void scheduleTask(BoolRun task){
		switch(EffectiveSide.get()){
		case SERVER:
			serverPredicatedTasks.add(task);
			return;
		case CLIENT:
			clientPredicatedTasks.add(task);
			return;
		}
	}

	public static boolean cancelTask(BoolRun task){
		switch(EffectiveSide.get()){
		case SERVER:
			return serverPredicatedTasks.remove(task);
		case CLIENT:
			return clientPredicatedTasks.remove(task);
		}
		throw new RuntimeException("Unhandled side: " + EffectiveSide.get());
	}

	@SubscribeEvent
	public void onServerTick(TickEvent event) {
		if (!(event instanceof TickEvent.ServerTickEvent)) {
			return;
		}
		
		serverTasks = CollectionUtil.mutateOr(
			serverTasks, 
			(task, delay) -> delay > 1, // condition
			(task, delay) -> delay - 1, // mutation
			(task, delay) -> task.run() // elseAction
		);
		serverPredicatedTasks = CollectionUtil.where(serverPredicatedTasks, task -> task.run());
	}

	@SubscribeEvent
	public void onClientTick(TickEvent event) {
		if (!(event instanceof TickEvent.ClientTickEvent)) {
			return;
		}

		clientTasks = CollectionUtil.mutateOr(
				clientTasks, 
			(task, delay) -> delay > 1, // condition
			(task, delay) -> delay - 1, // mutation
			(task, delay) -> task.run() // elseAction
		);
		clientPredicatedTasks = CollectionUtil.where(clientPredicatedTasks, task -> task.run());
	}
	
	// ServerSide
	@SubscribeEvent
	public void loggedIn(PlayerLoggedInEvent event) {
		if (event.getPlayer() instanceof EntityPlayerMP) {
			EntityPlayerMP player =  (EntityPlayerMP) event.getPlayer();
			NetworkHandler.sendTo(player, new InitPlayerResponseMessage());
			NetworkHandler.sendTo(player, new SendAllPostBasesMessage());
			NetworkHandler.sendTo(player, new SendAllBigPostBasesMessage());
			LazyOptional<PlayerStore> optionalStore = player.getCapability(PlayerProvider.STORE_CAP, null);
			optionalStore.ifPresent(store -> store.init(player));
		}
	}

	public static final ResourceLocation PLAYER_CAP = new ResourceLocation(Signpost.MODID, "playerstore");
	 
	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayerMP) {
			PlayerProvider provider = new PlayerProvider((EntityPlayerMP) event.getObject());
			event.addCapability(PLAYER_CAP, provider);
		}
	}
	
	@SubscribeEvent
	public void onLoad(WorldEvent.Load event) {
		if(!event.getWorld().isRemote()) {
			WorldSigns.worldSigns(event.getWorld());
		}
	}

	@SubscribeEvent
	public void onSave(WorldEvent.Save event) {
		if(!event.getWorld().isRemote()) {
			WorldSigns.worldSigns(event.getWorld());
		}
	}
	
	@SubscribeEvent
	public void oBlockPlace(EntityPlaceEvent event){
		EntityPlayer player = Utils.castOrNull(event.getEntity(), EntityPlayer.class);
		DimensionType dimension = event.getWorld().getDimension().getType();
		MyBlockPos blockPos = new MyBlockPos(event.getPos(), dimension);
		if(!(player instanceof EntityPlayerMP)){
			if(event.getState().getBlock() instanceof BasePost){
				BasePost.placeClient(event.getWorld(), blockPos, player);
			}else if(event.getState().getBlock() instanceof BaseModelPost){
				BaseModelPost.placeClient(event.getWorld(), blockPos, player);
			}else if(event.getState().getBlock() instanceof SuperPostPost){
				SuperPostPost.placeClient(event.getWorld(), blockPos, player);
			}
			return;
		}
		EntityPlayerMP playerMP = (EntityPlayerMP) player;
		if(event.getState().getBlock() instanceof BasePost){
			BasePostTile tile = BasePost.getWaystoneRootTile(event.getWorld(), event.getPos());
			if(!(ClientConfigStorage.INSTANCE.getSecurityLevelWaystone().canPlace(playerMP) && checkWaystoneCount(playerMP))){
				tile.onBlockDestroy(blockPos);
				event.setCanceled(true);
			}else{
				BasePost.placeServer(event.getWorld(), blockPos, playerMP);
			}
		}else if(event.getState().getBlock() instanceof BaseModelPost){
			BasePostTile tile = BaseModelPost.getWaystoneRootTile(event.getWorld(), event.getPos());
			if(!(ClientConfigStorage.INSTANCE.getSecurityLevelWaystone().canPlace(playerMP) && checkWaystoneCount(playerMP))){
				tile.onBlockDestroy(blockPos);
				event.setCanceled(true);
			}else{
				BaseModelPost.placeServer(event.getWorld(), blockPos, playerMP);
			}
		}else if(event.getState().getBlock() instanceof SuperPostPost){
			SuperPostPostTile tile = SuperPostPost.getSuperTile(event.getWorld(), event.getPos());
			if(!(ClientConfigStorage.INSTANCE.getSecurityLevelSignpost().canPlace(playerMP) && checkSignpostCount(playerMP))){
				tile.onBlockDestroy(blockPos);
				event.setCanceled(true);
			}else{
				SuperPostPost.placeServer(event.getWorld(), blockPos, playerMP);
			}
		}
	}

	public boolean checkWaystoneCount(EntityPlayerMP player){
		PlayerRestrictions restrictions = PostHandler.playerKnownWaystonePositions.get(player.getUniqueID());
		int remaining = restrictions.remainingWaystones;
		if(remaining == 0){
			player.sendMessage(new TextComponentString("You are not allowed to place more waystones"));
			return false;
		}
		if(remaining > 0) restrictions.remainingWaystones--;
		return true;
	}
	
	public void updateWaystoneCount(WaystoneContainer tile){
		if(tile == null || tile.getBaseInfo() == null){
			return;
		}
		UUID owner = tile.getBaseInfo().owner;
		if(owner == null){
			return;
		}
		PlayerRestrictions restrictions = PostHandler.playerKnownWaystonePositions.get(owner);
		if(restrictions.remainingWaystones >= 0){
			restrictions.remainingWaystones++;
		}
	}

	private boolean checkSignpostCount(EntityPlayerMP player){
		PlayerRestrictions restrictions = PostHandler.playerKnownWaystonePositions.get(player.getUniqueID());
		int remaining = restrictions.remainingSignposts;
		if(remaining == 0){
			player.sendMessage(new TextComponentString("You are not allowed to place more signposts"));
			return false;
		}
		if(remaining > 0) restrictions.remainingSignposts--;
		return true;
	}
	
	private void updateSignpostCount(SuperPostPostTile tile){
		if(tile == null || tile.owner == null){
			return;
		}
		PlayerRestrictions restrictions = PostHandler.playerKnownWaystonePositions.get(tile.owner);
		if(restrictions.remainingSignposts >= 0){
			restrictions.remainingSignposts++;
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event){
		try{
			TileEntity tile = event.getWorld().getTileEntity(event.getPos());
			if(tile instanceof SuperPostPostTile 
					&& !PostHandler.isHandEmpty(event.getPlayer()) 
					&& (event.getPlayer().getHeldItemMainhand().getItem() instanceof PostWrench 
							|| event.getPlayer().getHeldItemMainhand().getItem() instanceof CalibratedPostWrench
							|| event.getPlayer().getHeldItemMainhand().getItem().equals(Items.WHEAT_SEEDS)
							|| event.getPlayer().getHeldItemMainhand().getItem().equals(Items.SNOWBALL)
							|| event.getPlayer().getHeldItemMainhand().getItem().equals(Blocks.VINE.asItem()))){
				event.setCanceled(true);
				((SuperPostPost)tile.getBlockState().getBlock()).onBlockClicked(event.getWorld(), event.getPos(), event.getPlayer());
				return;
			}
			if(!(event.getPlayer() instanceof EntityPlayerMP)){
				return;
			}
			EntityPlayerMP player = (EntityPlayerMP)event.getPlayer();
			if(event.getState().getBlock() instanceof BasePost){
				BasePostTile t = BasePost.getWaystoneRootTile(event.getWorld(), event.getPos());
				if(!ClientConfigStorage.INSTANCE.getSecurityLevelWaystone().canUse(player, ""+t.getBaseInfo().owner)){
					event.setCanceled(true);
				}else{
					updateWaystoneCount(t);
					t.onBlockDestroy(new MyBlockPos(event.getPos(), player.dimension));
				}
			}else if(event.getState().getBlock() instanceof BaseModelPost){
				BasePostTile t = BaseModelPost.getWaystoneRootTile(event.getWorld(), event.getPos());
				if(!ClientConfigStorage.INSTANCE.getSecurityLevelWaystone().canUse(player, ""+t.getBaseInfo().owner)){
					event.setCanceled(true);
				}else{
					updateWaystoneCount(t);
					t.onBlockDestroy(new MyBlockPos(event.getPos(), player.dimension));
				}
			}else if(event.getState().getBlock() instanceof SuperPostPost){
				SuperPostPostTile t = SuperPostPost.getSuperTile(event.getWorld(), event.getPos());
				if(!ClientConfigStorage.INSTANCE.getSecurityLevelSignpost().canUse(player, ""+t.owner)){
					event.setCanceled(true);
				}else{
					updateSignpostCount(t);
					t.onBlockDestroy(new MyBlockPos(event.getPos(), player.dimension));
				}
			}
		}catch(Exception e){}
	}
}
