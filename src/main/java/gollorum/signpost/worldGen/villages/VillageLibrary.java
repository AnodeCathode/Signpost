package gollorum.signpost.worldGen.villages;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import gollorum.signpost.SPEventHandler;
import gollorum.signpost.util.BoolRun;
import gollorum.signpost.util.MyBlockPos;
import gollorum.signpost.util.code.MinecraftDependent;
import gollorum.signpost.util.collections.Lurchpaerchensauna;
import gollorum.signpost.util.collections.Lurchsauna;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;

@MinecraftDependent
public class VillageLibrary {

	private static VillageLibrary INSTANCE = new VillageLibrary();
	public static VillageLibrary getInstance(){
		return INSTANCE;
	}
	public static void init(){
		INSTANCE = new VillageLibrary();
	}
	
	private Map<MyBlockPos, MyBlockPos> villageWaystones;
	private Map<MyBlockPos, Set<VillagePost>> villagePosts;
	
	private VillageLibrary(){
		villageWaystones = new Lurchpaerchensauna<MyBlockPos, MyBlockPos>();
		villagePosts = new Lurchpaerchensauna<MyBlockPos, Set<VillagePost>>();
	}
	
	public void putWaystone(final MyBlockPos villageLocation, final MyBlockPos waystoneLocation){
		villageWaystones.put(villageLocation, waystoneLocation);
		SPEventHandler.scheduleTask(new BoolRun(){
			@Override
			public boolean run() {
				if(waystoneLocation.getTile() == null){
					return false;
				}else{
					new LibraryWaystoneHelper(villageLocation, villagePosts, waystoneLocation).enscribeEmptySign();
					return true;
				}
			}
		});
	}

	public void putSignpost(final MyBlockPos villageLocation, final MyBlockPos signpostLocation, final double optimalRot){
		Set<VillagePost> villageSignposts = villagePosts.get(villageLocation);
		if(villageSignposts == null){
			villageSignposts = new Lurchsauna<VillagePost>();
			villagePosts.put(villageLocation, villageSignposts);
		}
		villageSignposts.add(new VillagePost(signpostLocation, optimalRot));
		SPEventHandler.scheduleTask(new BoolRun(){
			@Override
			public boolean run() {
				if(signpostLocation.getTile() == null){
					return false;
				}else{
					new LibrarySignpostHelper(villageLocation, signpostLocation, villageWaystones).enscribeNewSign(optimalRot);
					return true;
				}
			}
		});
	}

	public void save(NBTTagCompound compound){
		compound.setTag("Waystones", saveWaystones());
		compound.setTag("Signposts", savePosts());
	}

	private NBTTagCompound saveWaystones() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInt("WaystoneCount", villageWaystones.size());
		int i=0;
		for(Entry<MyBlockPos, MyBlockPos> now: villageWaystones.entrySet()){
			compound.setTag("Waystone"+(i++), saveWaystone(now.getKey(), now.getValue()));
		}
		return compound;
	}

	private INBTBase saveWaystone(MyBlockPos villageLocation, MyBlockPos waystoneLocation) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("VillageLocation", villageLocation.writeToNBT(new NBTTagCompound()));
		compound.setTag("WaystoneLocation", waystoneLocation.writeToNBT(new NBTTagCompound()));
		return compound;
	}
	
	private NBTTagCompound savePosts() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInt("PostCount", villagePosts.size());
		int i=0;
		for(Entry<MyBlockPos, Set<VillagePost>> now: villagePosts.entrySet()){
			compound.setTag("Posts"+(i++), savePostCollection(now.getKey(), now.getValue()));
		}
		return compound;
	}
	
	private INBTBase savePostCollection(MyBlockPos villageLocation, Set<VillagePost> posts) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("VillageLocation", villageLocation.writeToNBT(new NBTTagCompound()));
		compound.setInt("PostCount", posts.size());
		int i=0;
		for(VillagePost now: posts){
			compound.setTag("Post"+(i++), now.save());
		}
		return compound;
	}
	
	public void load(NBTTagCompound compound){
		loadWaystones(compound.getCompound("Waystones"));
		loadSignpost(compound.getCompound("Signposts"));
	}
	
	private void loadWaystones(NBTTagCompound compound) {
		villageWaystones = new Lurchpaerchensauna<MyBlockPos, MyBlockPos>();
		int count = compound.getInt("WaystoneCount");
		for(int i=0; i<count; i++){
			NBTTagCompound entry = compound.getCompound("Waystone"+i);
			MyBlockPos villageLocation = MyBlockPos.readFromNBT(entry.getCompound("VillageLocation"));
			MyBlockPos waystoneLocation = MyBlockPos.readFromNBT(entry.getCompound("WaystoneLocation"));
			villageWaystones.put(villageLocation, waystoneLocation);
		}
	}

	private void loadSignpost(NBTTagCompound compound) {
		villagePosts = new Lurchpaerchensauna<MyBlockPos, Set<VillagePost>>();
		int postCount = compound.getInt("PostCount");
		for(int i=0; i<postCount; i++){
			NBTTagCompound entry = compound.getCompound("Posts"+i);
			MyBlockPos villageLocation = MyBlockPos.readFromNBT(entry.getCompound("VillageLocation"));
			Set<VillagePost> posts = loadPostSet(entry);
			villagePosts.put(villageLocation, posts);
		}
	}
	
	private Set<VillagePost> loadPostSet(NBTTagCompound compound) {
		Set<VillagePost> ret = new Lurchsauna<VillagePost>();
		int postCount = compound.getInt("PostCount");
		for(int i=0; i<postCount; i++){
			ret.add(VillagePost.load(compound.getCompound("Post"+i)));
		}
		return ret;
	}
}
