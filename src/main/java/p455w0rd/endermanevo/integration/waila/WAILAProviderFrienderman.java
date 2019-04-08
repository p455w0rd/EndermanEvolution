/*
 * This file is part of Enderman Evolution.
 * Copyright (c) 2016, p455w0rd (aka TheRealp455w0rd), All rights reserved
 * unless
 * otherwise stated.
 *
 * Enderman Evolution is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * Enderman Evolution is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * MIT License for more details.
 *
 * You should have received a copy of the MIT License
 * along with Enderman Evolution. If not, see
 * <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.endermanevo.integration.waila;

import java.util.List;
import java.util.UUID;

import mcp.mobius.waila.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
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
@SuppressWarnings("deprecation")
public class WAILAProviderFrienderman implements IWailaEntityProvider {

	@Override
	public Entity getWailaOverride(final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(final Entity entity, final List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaBody(final Entity entity, final List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
		if (!(entity instanceof EntityFrienderman)) {
			return currenttip;
		}
		final EntityFrienderman friend = (EntityFrienderman) entity;
		final NBTTagCompound nbt = friend.getEntityData();
		String owner = "";
		final boolean isTame = friend.isTamed();
		currenttip.add(WAILA.toolTipEnclose);
		if (isTame) {
			final UUID ownerUUID = friend.getOwnerId();
			if (FMLCommonHandler.instance().getMinecraftServerInstance() != null && MCUtils.isSMP(FMLCommonHandler.instance().getMinecraftServerInstance())) {
				owner = PlayerUUIDUtils.getPlayerName(ownerUUID);
				if (owner == "") {
					owner = I18n.translateToLocal("waila.404");
				}
			}
			else {
				owner = accessor.getPlayer().getName();
			}

			currenttip.add(I18n.translateToLocal("waila.owner") + ": " + owner);
			currenttip.add(I18n.translateToLocal("waila.mode") + ": " + (friend.isSitting() ? I18n.translateToLocal("waila.idle") : I18n.translateToLocal("waila.followingdefending")));
			if (friend.isHoldingChest() && accessor == friend.getOwner()) {
				currenttip.add(I18n.translateToLocal("waila.sneakrclicktotake"));
			}
		}
		else {
			currenttip.add(I18n.translateToLocal("waila.rightclickpearltotame"));
			currenttip.add(nbt.getString("OwnerUUID"));
		}
		currenttip.add(WAILA.toolTipEnclose);
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(final Entity entity, final List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP player, final Entity ent, final NBTTagCompound tag, final World world) {
		return ent.getEntityData();
	}

}
