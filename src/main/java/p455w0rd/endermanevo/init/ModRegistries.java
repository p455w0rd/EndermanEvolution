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
package p455w0rd.endermanevo.init;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayer;
import p455w0rd.endermanevo.entity.EntityFrienderman;

/**
 * @author p455w0rd
 *
 */
public class ModRegistries {

	private static Map<Integer, UUID> TAMED_FRIENDERMAN_REGISTRY = Maps.newHashMap();

	public static Map<Integer, UUID> getTamedFriendermanRegistry() {
		return TAMED_FRIENDERMAN_REGISTRY;
	}

	public static void registerTamedFrienderman(EntityPlayer player, EntityFrienderman frienderman) {
		if (!TAMED_FRIENDERMAN_REGISTRY.containsKey(frienderman)) {
			TAMED_FRIENDERMAN_REGISTRY.put(frienderman.getEntityId(), player.getGameProfile().getId());
		}
	}

	public static void unregisterTamedFrienderman(EntityFrienderman frienderman) {
		if (TAMED_FRIENDERMAN_REGISTRY.containsKey(frienderman)) {
			TAMED_FRIENDERMAN_REGISTRY.remove(frienderman);
		}
	}

	public static void setTamedFriendermanRegistry(Map<Integer, UUID> registry) {
		TAMED_FRIENDERMAN_REGISTRY = registry;
	}

}
