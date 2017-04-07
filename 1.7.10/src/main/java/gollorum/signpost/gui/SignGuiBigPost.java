package gollorum.signpost.gui;

import java.awt.Color;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import gollorum.signpost.blocks.BigPostPostTile;
import gollorum.signpost.management.ConfigHandler;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.SendBigPostBasesMessage;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.BigBaseInfo;
import gollorum.signpost.util.BlockPos.Connection;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class SignGuiBigPost extends GuiScreen {

	private GuiTextField baseInputBox;
	
	private String std = "";
	private int col = 0;
	private boolean go;
	
	private BigPostPostTile tile;

	private boolean resetMouse;
	
	public SignGuiBigPost(BigPostPostTile tile) {
		this.tile = tile;
		baseInputBox = new GuiTextField(this.fontRendererObj, this.width / 2 - 68, this.height / 2 + 40, 137, 20);
	}

	@Override
	public void initGui() {
		BigBaseInfo tilebases = tile.getBases();
		baseInputBox = new GuiTextField(this.fontRendererObj, this.width / 2 - 68, this.height / 2 - 46, 137, 20);
		baseInputBox.setMaxStringLength(23);
		baseInputBox.setText(tilebases.base==null?"":tilebases.base.toString());
		go = true;
		baseInputBox.setFocused(true);
		
		resetMouse = true;
	}
	
	@Override
    protected void mouseClicked(int x, int y, int bla){
		super.mouseClicked(x, y, bla);
		baseInputBox.mouseClicked(x, y, bla);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(mc==null){
			mc = FMLClientHandler.instance().getClient();
		}
		drawDefaultBackground();
		baseInputBox.drawTextBox();
		this.drawCenteredString(fontRendererObj, std, this.width/2, baseInputBox.yPosition+25, col);
		if(resetMouse){
			resetMouse = false;
			org.lwjgl.input.Mouse.setGrabbed(false);
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
		if(par1==13){
			if(baseInputBox.isFocused()){
				this.mc.displayGuiScreen(null);
			}else{
				baseInputBox.setFocused(true);
			}
			return;
		}else if(par1==9){
			if(!baseInputBox.isFocused()){
				baseInputBox.setFocused(true);
			}
			return;
		}
		baseType(par1, par2);
	}
	
	private void baseType(char par1, int par2){
		GuiTextField tf = baseInputBox;
		String before = tf.getText();
		if(tf.textboxKeyTyped(par1, par2)&&!tf.getText().equals(before)){
			if(ConfigHandler.deactivateTeleportation){
				return;
			}
			BaseInfo inf = PostHandler.getWSbyName(tf.getText());
			Connection connect = tile.toPos().canConnectTo(inf);
			if(inf==null||!connect.equals(Connection.VALID)){
				tf.setTextColor(Color.red.getRGB());
				if(connect.equals(Connection.DIST)){
					
					String out = LanguageRegistry.instance().getStringLocalization("signpost.guiTooFar");
					if(out.equals("")){
						out = LanguageRegistry.instance().getStringLocalization("signpost.guiTooFar", "en_US");
					}
					out = out.replaceAll("<distance>", ""+(int)tile.toPos().distance(inf.pos)+1);
					out = out.replaceAll("<maxDist>", ""+ConfigHandler.maxDist);
					std = out;
					col = Color.red.getRGB();
					go = false;
					
				}else if(connect.equals(Connection.WORLD)){

					String out = LanguageRegistry.instance().getStringLocalization("signpost.guiWorldDim");
					if(out.equals("")){
						out = LanguageRegistry.instance().getStringLocalization("signpost.guiWorldDim", "en_US");
					}
					std = out;
					col = Color.red.getRGB();
					go = false;
					
				}else{
					std = "";
					col = Color.red.getRGB();
					go = false;
				}
			}else{
				tf.setTextColor(Color.white.getRGB());
				col = Color.white.getRGB();
				go = true;

				if(!(ConfigHandler.deactivateTeleportation||ConfigHandler.cost==null)){
					String out = LanguageRegistry.instance().getStringLocalization("signpost.guiPrev");
					if(out.equals("")){
						out = LanguageRegistry.instance().getStringLocalization("signpost.guiPrev", "en_US");
					}
					int distance = (int) tile.toPos().distance(inf.pos)+1;
					out = out.replaceAll("<distance>", ""+distance);
					out = out.replaceAll("<amount>", Integer.toString((int) (tile.toPos().distance(inf.pos)/ConfigHandler.costMult+1)));
					out = out.replaceAll("<itemName>", ConfigHandler.costName());
					col = Color.white.getRGB();
					std = out;
				}
			}
		}
	}

	@Override
	public void onGuiClosed() {
		BigBaseInfo tilebases = tile.getBases();
		if(ConfigHandler.deactivateTeleportation||go){
			tilebases.base = PostHandler.getWSbyName(baseInputBox.getText());
		}else{
			tilebases.base = null;
		}
		NetworkHandler.netWrap.sendToServer(new SendBigPostBasesMessage(tile, tilebases));
	}
}
