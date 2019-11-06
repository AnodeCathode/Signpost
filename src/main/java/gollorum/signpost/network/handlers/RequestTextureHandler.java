package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.network.messages.RequestTextureMessage;
import gollorum.signpost.util.TextureHelper;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class RequestTextureHandler extends Handler<RequestTextureHandler, RequestTextureMessage>{

	@Override
	public void handle(RequestTextureMessage message, Supplier<Context> contextSupplier) {
		TextureHelper.instance().setTexture(message.toBlockPos(), message.hand, message.facing, message.hitX, message.hitY, message.hitZ);
	}

}
