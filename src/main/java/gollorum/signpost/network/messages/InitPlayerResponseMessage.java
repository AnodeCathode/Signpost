package gollorum.signpost.network.messages;

import gollorum.signpost.management.ConfigHandler;
import gollorum.signpost.management.ConfigHandler.RecipeCost;
import gollorum.signpost.management.ConfigHandler.SecurityLevel;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkUtil;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.StonedHashSet;
import net.minecraft.network.PacketBuffer;

@SuppressWarnings("deprecation")
public class InitPlayerResponseMessage extends Message<InitPlayerResponseMessage> {

	public StonedHashSet allWaystones = new StonedHashSet();

	public static boolean deactivateTeleportation;
	public static boolean interdimensional;
	public static int maxDist;
	public static String paymentItem;
	public static int costMult;

	public static RecipeCost signRec;
	public static RecipeCost waysRec;

	public static SecurityLevel securityLevelWaystone;
	public static SecurityLevel securityLevelSignpost;
	
	public boolean disableVillageGeneration; 
	public int villageWaystonesWeight;
	public int villageMaxSignposts;
	public int villageSignpostsWeight;
	public boolean onlyVillageTargets;
	
	public InitPlayerResponseMessage(){
		if(!ConfigHandler.isDeactivateTeleportation()){
			allWaystones = PostHandler.getNativeWaystones();
		}
		deactivateTeleportation = ConfigHandler.isDeactivateTeleportation(); 
	    interdimensional = ConfigHandler.isInterdimensional(); 
	    maxDist = ConfigHandler.getMaxDist(); 
	    paymentItem = ConfigHandler.getPaymentItem(); 
	    costMult = ConfigHandler.getCostMult(); 
	    signRec = ConfigHandler.getSignRec(); 
	    waysRec = ConfigHandler.getWaysRec(); 
	    securityLevelWaystone = ConfigHandler.getSecurityLevelWaystone(); 
	    securityLevelSignpost = ConfigHandler.getSecurityLevelSignpost(); 
	    disableVillageGeneration = ConfigHandler.isDisableVillageGeneration();   
	    villageMaxSignposts = ConfigHandler.getVillageMaxSignposts(); 
	    villageSignpostsWeight = ConfigHandler.getVillageSignpostsWeight(); 
	    villageWaystonesWeight = ConfigHandler.getVillageWaystonesWeight();
	    onlyVillageTargets = ConfigHandler.isOnlyVillageTargets();
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBoolean(deactivateTeleportation);
		if(!ConfigHandler.isDeactivateTeleportation()){
			buffer.writeInt(allWaystones.size());
			for(BaseInfo now:allWaystones){
				now.encode(buffer);
			}
		}
		buffer.writeBoolean(interdimensional);
		buffer.writeInt(maxDist);
		buffer.writeString(paymentItem);
		buffer.writeInt(costMult);
		buffer.writeString(signRec.name());
		buffer.writeString(waysRec.name());
		buffer.writeString(securityLevelWaystone.name());
		buffer.writeString(securityLevelSignpost.name());
		buffer.writeBoolean(disableVillageGeneration);
		buffer.writeInt(villageMaxSignposts);
		buffer.writeInt(villageSignpostsWeight);
		buffer.writeInt(villageWaystonesWeight);
	    buffer.writeBoolean(onlyVillageTargets);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		deactivateTeleportation = buffer.readBoolean();
		if(!deactivateTeleportation){
			allWaystones = new StonedHashSet();
			int c = buffer.readInt();
			for(int i=0; i<c; i++){
				allWaystones.add(BaseInfo.decode(buffer));
			}
		}
		interdimensional = buffer.readBoolean();
		maxDist = buffer.readInt();
		paymentItem = buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
		costMult = buffer.readInt();
		signRec = RecipeCost.valueOf(buffer.readString(NetworkUtil.MAX_STRING_LENGTH));
		waysRec = RecipeCost.valueOf(buffer.readString(NetworkUtil.MAX_STRING_LENGTH));
		securityLevelWaystone = SecurityLevel.valueOf(buffer.readString(NetworkUtil.MAX_STRING_LENGTH));
		securityLevelSignpost = SecurityLevel.valueOf(buffer.readString(NetworkUtil.MAX_STRING_LENGTH));
		disableVillageGeneration = buffer.readBoolean();
		villageMaxSignposts = buffer.readInt();
		villageSignpostsWeight = buffer.readInt();
		villageWaystonesWeight = buffer.readInt(); 
	    onlyVillageTargets = buffer.readBoolean();
	}

}
