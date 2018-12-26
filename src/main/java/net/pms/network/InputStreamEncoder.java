package net.pms.network;

import java.io.InputStream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * youtube-dl downloads youtube video as separate video and audio streams which are ultimately merged.
 * Hence InputStream in this case represents a complete file. We don't need to send this as chunked message.
 * In case chunked message is used, LG TV sends out second request seeking bytes from EOF onwards.
 * 
 * @author Anand Tamariya
 *
 */
public class InputStreamEncoder extends MessageToByteEncoder<InputStream>{

	@Override
	protected void encode(ChannelHandlerContext ctx, InputStream inputstream, ByteBuf out) throws Exception {
		int data = inputstream.read();
		int offset = 0, available = 0;
		while (data != -1) {
			out.writeByte(data);
			available = inputstream.available() - offset + 1;
			out.writeBytes(inputstream, available);
			offset = available;
			data = inputstream.read();
		}
	}

}
