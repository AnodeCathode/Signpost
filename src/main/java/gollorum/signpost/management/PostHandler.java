package gollorum.signpost.management;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import gollorum.signpost.SPEventHandler;
import gollorum.signpost.Signpost;
import gollorum.signpost.blocks.tiles.BigPostPostTile;
import gollorum.signpost.blocks.tiles.PostPostTile;
import gollorum.signpost.modIntegration.SignpostAdapter;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.handlers.SendAllWaystoneNamesHandler;
import gollorum.signpost.network.messages.ChatMessage;
import gollorum.signpost.network.messages.TeleportRequestMessage;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.BigBaseInfo;
import gollorum.signpost.util.BoolRun;
import gollorum.signpost.util.DoubleBaseInfo;
import gollorum.signpost.util.MyBlockPos;
import gollorum.signpost.util.MyBlockPosSet;
import gollorum.signpost.util.Paintable;
import gollorum.signpost.util.Sign;
import gollorum.signpost.util.StonedHashSet;
import gollorum.signpost.util.StringSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class PostHandler {

	private static StonedHashSet allWaystones = new StonedHashSet();	
	private static HashMap<MyBlockPos, DoubleBaseInfo> posts = new HashMap<MyBlockPos, DoubleBaseInfo>();
	private static HashMap<MyBlockPos, BigBaseInfo> bigPosts = new HashMap<MyBlockPos, BigBaseInfo>();
	//ServerSide
	public static HashMap<UUID, TeleportInformation> awaiting =  new HashMap<UUID, TeleportInformation>(); 

	/**
	 * UUID = the player;
	 * StringSet = the discovered waystones;
	 */
	public static HashMap<UUID, StringSet> playerKnownWaystones = new HashMap<UUID, StringSet>(){
		@Override
		public StringSet get(Object obj){
			StringSet set = super.get(obj);
			if(set == null){
				return put((UUID) obj, new StringSet());
			}else{
				return set;
			}
		}
	};
	
	public static class PlayerRestrictions {
		public final MyBlockPosSet discoveredWastones;
		public int remainingWaystones;
		public int remainingSignposts;
		public PlayerRestrictions(MyBlockPosSet discoveredWastones, int remainingWaystones, int remainingSignposts) {
			this.discoveredWastones = discoveredWastones;
			this.remainingWaystones = remainingWaystones;
			this.remainingSignposts = remainingSignposts;
		}
		
		public PlayerRestrictions() {
			this(
				new MyBlockPosSet(),
				ClientConfigStorage.INSTANCE.getMaxWaystones(),
				ClientConfigStorage.INSTANCE.getMaxSignposts()
			);
		}
	}

	public static HashMap<UUID, PlayerRestrictions> playerKnownWaystonePositions = new HashMap<UUID, PlayerRestrictions>(){
		@Override
		public PlayerRestrictions get(Object obj){
			PlayerRestrictions restrictions = super.get(obj);
			if(restrictions != null) return restrictions;
			else return put((UUID) obj, new PlayerRestrictions());
		}
	};
	
	public static boolean doesPlayerKnowWaystone(EntityPlayerMP player, BaseInfo waystone){
		if(ClientConfigStorage.INSTANCE.isDisableDiscovery()){
			return true;
		}else{
			return doesPlayerKnowNativeWaystone(player, waystone) || getPlayerKnownWaystones(player).contains(waystone);
		}
	}

	public static boolean doesPlayerKnowNativeWaystone(EntityPlayerMP player, BaseInfo waystone){
		if(ClientConfigStorage.INSTANCE.isDisableDiscovery()){
			return true;
		}else if(playerKnownWaystonePositions.get(player.getUniqueID()).discoveredWastones.contains(waystone.blockPosition)){
			if(playerKnownWaystones.containsKey(player.getUniqueID())){
				playerKnownWaystones.get(player.getUniqueID()).remove(waystone.getName());
			}
			return true;
		}else{
			return playerKnownWaystones.get(player.getUniqueID()).contains(waystone.getName());
		}
	}
	
	public static void init(){
		allWaystones = new StonedHashSet();
		playerKnownWaystones = new HashMap<UUID, StringSet>(){
			@Override
			public StringSet get(Object obj){
				StringSet pair = super.get(obj);
				if(pair == null){
					return put((UUID) obj, new StringSet());
				} else {
					return pair;
				}
			}
		};
		playerKnownWaystonePositions = new HashMap<UUID, PlayerRestrictions>(){
			@Override
			public PlayerRestrictions get(Object obj){
				PlayerRestrictions restrictions = super.get(obj);
				if(restrictions != null) return restrictions;
				else return put((UUID) obj, new PlayerRestrictions());
			}
		};
		posts = new HashMap<MyBlockPos, DoubleBaseInfo>();
		bigPosts = new HashMap<MyBlockPos, BigBaseInfo>();
		awaiting = new HashMap<UUID, TeleportInformation>();
	}

	public static HashMap<MyBlockPos, DoubleBaseInfo> getPosts() {
		return posts;
	}

	public static void setPosts(HashMap<MyBlockPos, DoubleBaseInfo> posts) {
		PostHandler.posts = posts;
		refreshDoublePosts();
	}

	public static HashMap<MyBlockPos, BigBaseInfo> getBigPosts() {
		return bigPosts;
	}

	public static void setBigPosts(HashMap<MyBlockPos, BigBaseInfo> bigPosts) {
		PostHandler.bigPosts = bigPosts;
		refreshBigPosts();
	}

	public static List<Sign> getSigns(MyBlockPos pos) {
		List<Sign> ret = new LinkedList();

		DoubleBaseInfo doubleBase = getPosts().get(pos);
		if (doubleBase != null) {
			ret.add(doubleBase.sign1);
			ret.add(doubleBase.sign2);
		} else {
			BigBaseInfo bigBase = getBigPosts().get(pos);
			if (bigBase != null) {
				ret.add(bigBase.sign);
			}
		}

		return ret;
	}

	public static Paintable getPost(MyBlockPos pos) {
		Paintable ret = getPosts().get(pos);
		if (ret == null) {
			ret = getBigPosts().get(pos);
		}
		if (ret == null) {
			pos.getTile();
			ret = getPosts().get(pos);
			if (ret == null) {
				ret = getBigPosts().get(pos);
			}
		}
		return ret;
	}
	
	public static void refreshDoublePosts(){
		for(Entry<MyBlockPos, DoubleBaseInfo> now: posts.entrySet()){
			PostPostTile tile = (PostPostTile) now.getKey().getTile();
			if(tile!=null){
				tile.isWaystone();
				tile.getBases();
			}
		}
	}
	
	public static void refreshBigPosts(){
		for(Entry<MyBlockPos, BigBaseInfo> now: bigPosts.entrySet()){
			BigPostPostTile tile = (BigPostPostTile) now.getKey().getTile();
			if(tile!=null){
				tile.isWaystone();
				tile.getBases();
			}
		}
	}
	
	public static BaseInfo getWSbyName(String name){
		if(ClientConfigStorage.INSTANCE.deactivateTeleportation()){
			return new BaseInfo(name, null, null);
		}else{
			for(BaseInfo now:getAllWaystones()){
				if(name.equals(now.getName())){
					return now;
				}
			}
			return null;
		}
	}

	public static BaseInfo getForceWSbyName(String name){
		if(name==null || name.equals("null")){
			return null;
		}
		for(BaseInfo now:getAllWaystones()){
			if(name.equals(now.getName())){
				return now;
			}
		}
		return new BaseInfo(name, null, null);
	}
	
	public static class TeleportInformation{
		public final BaseInfo destination;
		public final int stackSize;
		public final WorldServer world;
		public final BoolRun boolRun;
		public TeleportInformation(BaseInfo destination, int stackSize, World world, BoolRun boolRun) {
			this.destination = destination;
			this.stackSize = stackSize;
			this.world = (WorldServer) world;
			this.boolRun = boolRun;
		}
	}

	/**
	 * @return whether the player could pay
	 */
	public static boolean pay(EntityPlayer player, BlockPos origin, BlockPos destination){
		if(canPay(player, origin, destination)){
			doPay(player, origin, destination);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean canPay(EntityPlayer player, BlockPos origin, BlockPos destination){
		if(ClientConfigStorage.INSTANCE.getCost() == null || ConfigHandler.isCreative(player)){
			return true;
		} else {
			int playerItemCount = 0;
			for(ItemStack now: player.inventory.mainInventory){
				if(now != null && now.getItem() !=null && now.getItem().getClass() == ClientConfigStorage.INSTANCE.getCost().getClass()){
					playerItemCount += now.getCount();
				}
			}
			return playerItemCount >= getStackSize(origin, destination);
		}
	}

	private static void doPay(EntityPlayer player, BlockPos origin, BlockPos destination){
		if(ClientConfigStorage.INSTANCE.getCost() == null || ConfigHandler.isCreative(player)){
			return;
		} else {
			int stackSize = getStackSize(origin, destination);
			player.inventory.clearMatchingItems(itemStack -> itemStack.getItem() == ClientConfigStorage.INSTANCE.getCost(), stackSize);
		}
	}
	
	public static int getStackSize(BlockPos origin, BlockPos destination){
		if(ClientConfigStorage.INSTANCE.getCostMult() == 0){
			return 1;
		} else {
			return (int) origin.getDistance(destination) / ClientConfigStorage.INSTANCE.getCostMult() + 1;
		}
	}
	
	public static void confirm(final EntityPlayerMP player){
		final TeleportInformation info = awaiting.get(player.getUniqueID());
		SPEventHandler.scheduleTask(new Runnable(){
			@Override
			public void run() {
				if(info == null){
					NetworkHandler.sendTo(player, new ChatMessage("signpost.noConfirm"));
					return;
				}else{
					doPay(player, player.getPosition(), info.destination.teleportPosition.toBlockPos());
					SPEventHandler.cancelTask(info.boolRun);
					if(player.dimension != info.destination.teleportPosition.dim){
						player.changeDimension(info.destination.teleportPosition.dim, null);
					}
					player.setPositionAndUpdate(info.destination.teleportPosition.x+0.5, info.destination.teleportPosition.y+1, info.destination.teleportPosition.z+0.5);
				}
			}
		}, 1);
	}

	public static void teleportMe(BaseInfo destination, final EntityPlayerMP player, int stackSize){
		if(ClientConfigStorage.INSTANCE.deactivateTeleportation()){
			return;
		}
		if(canTeleport(player, destination)){
			World world = destination.teleportPosition.getWorld();
			if(world == null){
				NetworkHandler.sendTo(player, new ChatMessage("signpost.errorWorld", "<world>", destination.teleportPosition.dim.getRegistryName().getPath()));
			}else{
				SPEventHandler.scheduleTask(awaiting.put(player.getUniqueID(), new TeleportInformation(destination, stackSize, world, new BoolRun(){
					private short ticksLeft = 2400;
					@Override
					public boolean run() {
						if(ticksLeft--<=0){
							awaiting.remove(player.getUniqueID());
							return true;
						}
						return false;
					}
				})).boolRun);
				NetworkHandler.sendTo(player, new TeleportRequestMessage(stackSize, destination.getName()));
			}
		}
	}
	
	public static boolean addAllDiscoveredByName(UUID player, StringSet ws){
		MyBlockPosSet set = new MyBlockPosSet();
		StringSet newStrs = new StringSet();
		newStrs.addAll(ws);
		for(String now: ws){
			for(BaseInfo base: getAllWaystones()){
				if(base.getName().equals(now)){
					set.add(base.blockPosition);
					newStrs.remove(now);
				}
			}
		}
		ws = newStrs;
		boolean ret = false;
		if(!ws.isEmpty()) if(playerKnownWaystones.containsKey(player)){
			ret = playerKnownWaystones.get(player).addAll(ws);
		} else {
			StringSet strSet = new StringSet();
			ret = strSet.addAll(ws);
			playerKnownWaystones.put(player, strSet);
		}
		if(playerKnownWaystonePositions.containsKey(player)){
			return ret | playerKnownWaystonePositions.get(player).discoveredWastones.addAll(set);
		} else {
			MyBlockPosSet newSet = new MyBlockPosSet();
			ret |= newSet.addAll(set);
			playerKnownWaystonePositions.put(player, new PlayerRestrictions());
			return ret;
		}
	}
	
	public static boolean addAllDiscoveredByPos(UUID player, MyBlockPosSet ws){
		if(playerKnownWaystonePositions.containsKey(player)){
			return playerKnownWaystonePositions.get(player).discoveredWastones.addAll(ws);
		}else{
			MyBlockPosSet newSet = new MyBlockPosSet();
			boolean ret = newSet.addAll(ws);
			playerKnownWaystonePositions.put(player, new PlayerRestrictions());
			return ret;
		}
	}
	
	public static boolean addDiscovered(UUID player, BaseInfo ws){
		if(ws==null){
			return false;
		}
		if(playerKnownWaystonePositions.containsKey(player)){
			boolean ret = playerKnownWaystonePositions.get(player).discoveredWastones.add(ws.blockPosition);
			ret = ret |! (playerKnownWaystonePositions.containsKey(player) && playerKnownWaystones.get(player).remove(ws.getName()));
			return ret;
		}else{
			MyBlockPosSet newSet = new MyBlockPosSet();
			newSet.add(ws.blockPosition);
			playerKnownWaystonePositions.put(player, new PlayerRestrictions());
			return !(playerKnownWaystonePositions.containsKey(player) && playerKnownWaystones.get(player).remove(ws.getName()));
		}
	}
	
	public static void refreshDiscovered(){
		HashSet<UUID> toDelete = new HashSet<UUID>();
		HashMap<UUID, MyBlockPosSet> toAdd = new HashMap<UUID, MyBlockPosSet>();
		for(Entry<UUID, StringSet> now: playerKnownWaystones.entrySet()){
			StringSet newSet = new StringSet();
			MyBlockPosSet newPosSet = new MyBlockPosSet();
			for(String str: now.getValue()){
				for(BaseInfo base: allWaystones){
					if(base.hasName() && base.getName().equals(str)){
						newPosSet.add(base.blockPosition);
						newSet.add(str);
					}
				}
			}
			toAdd.put(now.getKey(), newPosSet);
			now.getValue().removeAll(newSet);
			if(now.getValue().isEmpty()){
				toDelete.add(now.getKey());
			}
		}
		
		for(UUID now: toDelete){
			playerKnownWaystones.remove(now);
		}
		
		for(Entry<UUID, MyBlockPosSet> now: toAdd.entrySet()){
			addAllDiscoveredByPos(now.getKey(), now.getValue());
		}
	}
	
	public static boolean canTeleport(EntityPlayerMP player, BaseInfo target){
		if(doesPlayerKnowWaystone(player, target)){
			if(new MyBlockPos(player).checkInterdimensional(target.blockPosition)){
				return true;
			} else {
				NetworkHandler.sendTo(player, (new ChatMessage("signpost.guiWorldDim")));
			}
		} else {
			NetworkHandler.sendTo(player, new ChatMessage("signpost.notDiscovered", "<Waystone>", target.getName()));
		}
		return false;
	}
	
	public static EntityPlayer getPlayerByName(String name){
		return Signpost.getServerInstance().getPlayerList().getPlayerByUsername(name);
	}
	
	public static boolean isHandEmpty(EntityPlayer player){
		return player.getHeldItemMainhand() == null || player.getHeldItemMainhand().getItem() == null || player.getHeldItemMainhand().getItem().equals(Blocks.AIR.asItem());
	}

	public static StonedHashSet getAllWaystones() {
		StonedHashSet ret = SignpostAdapter.INSTANCE.getExternalBaseInfos();
		ret.addAll(allWaystones);
		return ret;
	}
	
	public static Collection<String> getAllWaystoneNames(){
		Collection<String> ret = getAllWaystones().select(b -> b.getName());
		if(EffectiveSide.get().equals(LogicalSide.CLIENT)) {
			ret.addAll(SendAllWaystoneNamesHandler.cachedWaystoneNames);
		}
		return ret;
	}

	public static StonedHashSet getNativeWaystones(){
		return allWaystones;
	}

	public static void setNativeWaystones(StonedHashSet set){
		allWaystones = set;
	}

	public static StonedHashSet getPlayerKnownWaystones(EntityPlayerMP player){
		StonedHashSet ret = SignpostAdapter.INSTANCE.getExternalPlayerBaseInfos(player);
		for(BaseInfo now: allWaystones){
			if(doesPlayerKnowNativeWaystone(player, now)){
				ret.add(now);
			}
		}
		return ret;
	}

	public static boolean addWaystone(BaseInfo baseInfo){
		return allWaystones.add(baseInfo);
	}

}
