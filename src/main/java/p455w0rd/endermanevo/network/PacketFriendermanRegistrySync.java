/*
 * This file is part of Wireless Crafting Terminal. Copyright (c) 2017, p455w0rd
 * (aka TheRealp455w0rd), All rights reserved unless otherwise stated.
 *
 * Wireless Crafting Terminal is free software: you can redistribute it and/or
 * modify it under the terms of the MIT License.
 *
 * Wireless Crafting Terminal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the MIT License for
 * more details.
 *
 * You should have received a copy of the MIT License along with Wireless
 * Crafting Terminal. If not, see <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.endermanevo.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.collect.Maps;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.endermanevo.init.ModRegistries;

/**
 * @author p455w0rd
 *
 */
public class PacketFriendermanRegistrySync implements IMessage {

	Map<Integer, UUID> registry = Maps.newHashMap();

	public PacketFriendermanRegistrySync() {
	}

	public PacketFriendermanRegistrySync(Map<Integer, UUID> registry) {
		this.registry = registry;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromBytes(ByteBuf buf) {
		short len = buf.readShort();
		byte[] compressedBody = new byte[len];

		for (short i = 0; i < len; i++) {
			compressedBody[i] = buf.readByte();
		}

		try {
			ObjectInputStream obj = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(compressedBody)));
			registry = (Map<Integer, UUID>) obj.readObject();
			obj.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteArrayOutputStream obj = new ByteArrayOutputStream();

		try {
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			ObjectOutputStream objStream = new ObjectOutputStream(gzip);
			objStream.writeObject(registry);
			objStream.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		buf.writeShort(obj.size());
		buf.writeBytes(obj.toByteArray());
	}

	public static class Handler implements IMessageHandler<PacketFriendermanRegistrySync, IMessage> {
		@Override
		public IMessage onMessage(PacketFriendermanRegistrySync message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				if (ctx.getClientHandler() != null) {
					ModRegistries.setTamedFriendermanRegistry(message.registry);
				}
			});
			return null;
		}
	}

}
