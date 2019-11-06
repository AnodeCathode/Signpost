package gollorum.signpost.util;

import java.util.Objects;
import java.util.UUID;

import gollorum.signpost.Signpost;
import gollorum.signpost.network.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class BaseInfo {

	private static final String VERSION = "Version2:";
	private String name;
	public MyBlockPos blockPosition;
	/**
	 * One block below the teleport destination
	 */
	public MyBlockPos teleportPosition;
	public UUID owner;

	public BaseInfo(String name, MyBlockPos pos, UUID owner){
		this.name = ""+name;
		this.blockPosition = pos;
		if(pos==null){
			this.teleportPosition = null;
		}else{
			this.teleportPosition = new MyBlockPos(pos);
		}
		this.owner = owner;
	}
	
	public BaseInfo(String name, MyBlockPos blockPos, MyBlockPos telePos, UUID owner){
		telePos.y--;
		this.name = ""+name;
		this.blockPosition = blockPos;
		this.teleportPosition = telePos;
		this.owner = owner;
	}

	public static BaseInfo loadBaseInfo(String name, MyBlockPos blockPos, MyBlockPos telePos, UUID owner){
		telePos.y++;
		return new BaseInfo(name, blockPos, telePos, owner);
	}

	public void writeToNBT(NBTTagCompound tC){
		tC.setString("name", ""+name);	//Warum bin ich nur so unglaublich gehörnamputiert? *kotz*
		NBTTagCompound posComp = new NBTTagCompound();
		teleportPosition.writeToNBT(posComp);
		tC.setTag("pos", posComp);
		NBTTagCompound blockPosComp = new NBTTagCompound();
		teleportPosition.writeToNBT(blockPosComp);
		blockPosition.writeToNBT(blockPosComp);
		tC.setTag("blockPos", blockPosComp);
		teleportPosition.writeToNBT(tC);
		tC.setString("UUID", ""+owner);
	}

	public static BaseInfo readFromNBT(NBTTagCompound tC) {
		String name = tC.getString("name");
		UUID owner = uuidFromString(tC.getString("UUID"));
		if(tC.hasKey("blockPos")){
			MyBlockPos pos = MyBlockPos.readFromNBT(tC.getCompound("pos"));
			MyBlockPos blockPos = MyBlockPos.readFromNBT(tC.getCompound("blockPos"));
			return loadBaseInfo(name, blockPos, pos, owner);
		}else{
			MyBlockPos pos = MyBlockPos.readFromNBT(tC);
			return new BaseInfo(name, pos, owner);
		}
	}
	
	public void encode(PacketBuffer buffer) {
		buffer.writeString(""+name);
		teleportPosition.encode(buffer);
		buffer.writeString(VERSION+owner);
		blockPosition.encode(buffer);
	}

	public static BaseInfo decode(PacketBuffer buffer) {
		String name = buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
		MyBlockPos pos = MyBlockPos.decode(buffer);
		String o = buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
		if(o.startsWith(VERSION)){
			o = o.replaceFirst(VERSION, "");
			UUID owner;
			try{
				owner = uuidFromString(o);
			}catch(Exception e){
				owner = null;
			}
			MyBlockPos blockPos = MyBlockPos.decode(buffer);
			return loadBaseInfo(name, blockPos, pos, owner);//Ich bin sehr dumm.
		}else{
			UUID owner = uuidFromString(o);
			return new BaseInfo(name, pos, owner);
		}
	}

	public void setAll(BaseInfo newWS){
		this.name = ""+newWS.name;
		this.teleportPosition.update(newWS.teleportPosition);
		this.blockPosition.update(newWS.blockPosition);
		this.owner = newWS.owner;
	}
	
	public boolean update(BaseInfo newWS){
		if(equals(newWS)){
			setAll(newWS);
			return true;
		}else{
			return false;
		}
	}

	public boolean hasName(){
		return !(name==null || name.equals("null") || name.equals(""));
	}
	
	public String getName(){
		return toString();
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	private static UUID uuidFromString(String string){
		try{
			return UUID.fromString(string);
		}catch(IllegalArgumentException e){
			return null;
		}
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof BaseInfo)){
			return super.equals(other);
		}else{
			return ((BaseInfo)other).blockPosition.equals(this.blockPosition);//Wirklich sehr dumm.
		}
	}
	
	@Override
	public String toString(){
		return ""+name;
	}
	
	//TODO Check for a better solution
	@Override
	public int hashCode() {
		return blockPosition.hashCode();
	}
}
