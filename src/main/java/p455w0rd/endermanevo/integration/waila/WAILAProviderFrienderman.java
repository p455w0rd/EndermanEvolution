/*
 * This file is part of p455w0rd's Things.
 * Copyright (c) 2016, p455w0rd (aka TheRealp455w0rd), All rights reserved
 * unless
 * otherwise stated.
 *
 * p455w0rd's Things is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * p455w0rd's Things is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * MIT License for more details.
 *
 * You should have received a copy of the MIT License
 * along with p455w0rd's Things. If not, see
 * <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.endermanevo.integration.waila;

import java.util.List;
import java.util.UUID;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import p455w0rd.endermanevo.entity.EntityFrienderman;
import p455w0rd.endermanevo.integration.WAILA;
import p455w0rdslib.util.MCUtils;
import p455w0rdslib.util.PlayerUUIDUtils;

/**
 * @author p455w0rd
 *
 */
public class WAILAProviderFrienderman implements IWailaEntityProvider {

	@Override
	public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		if (!(entity instanceof EntityFrienderman)) {
			return currenttip;
		}
		EntityFrienderman friend = (EntityFrienderman) entity;
		NBTTagCompound nbt = friend.getEntityData();
		String owner = "";
		boolean isTame = friend.isTamed();
		currenttip.add(WAILA.toolTipEnclose);
		if (isTame) {
			UUID ownerUUID = friend.getOwnerId();
			if (MCUtils.isSMP(FMLCommonHandler.instance().getMinecraftServerInstance())) {
				owner = PlayerUUIDUtils.getPlayerName(ownerUUID);
				if (owner == "") {
					owner = "404 Error: Name not found!";
				}
			}
			else {
				owner = accessor.getPlayer().getName();
			}

			currenttip.add("Owner: " + owner);
			currenttip.add("Mode: " + (friend.isSitting() ? "Idle" : "Following/Defending"));
			if (friend.isHoldingChest() && accessor == friend.getOwner()) {
				currenttip.add("Sneak+Right-Click to take chest");
			}
		}
		else {
			currenttip.add("Right-click with Frienderpearl to tame");
			currenttip.add(nbt.getString("OwnerUUID"));
		}
		currenttip.add(WAILA.toolTipEnclose);
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
		return ent.getEntityData();
	}

}
