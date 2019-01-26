package p455w0rd.endermanevo.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.client.model.ModelSkullBase;
import p455w0rd.endermanevo.entity.EntityEvolvedEnderman;
import p455w0rd.endermanevo.entity.EntityFrienderman;
import p455w0rd.endermanevo.init.ModItems;
import p455w0rd.endermanevo.items.ItemSkullBase;
import p455w0rdslib.util.MathUtils;

/**
 * @author p455w0rd
 *
 */
public class EntityUtils {

	public static void knockBackEntity(EntityPlayer player, EntityLivingBase target) {
		double d1 = player.posX - target.posX;
		double d0;

		for (d0 = player.posZ - target.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
			d1 = (Math.random() - Math.random()) * 0.01D;
		}

		target.attackedAtYaw = (float) (MathUtils.atan2(d0, d1) * (180D / Math.PI) - target.rotationYaw);
		target.knockBack(player, 1.0F, d1, d0);
	}

	public static boolean isWearingCustomSkull(EntityLivingBase entity) {
		return !entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() && entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemSkullBase;
	}

	public static ItemSkullBase getSkullItem(EntityLivingBase entity) {
		if (!isWearingCustomSkull(entity)) {
			return null;
		}
		return (ItemSkullBase) entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
	}

	public static ModelSkullBase getSkullModel(EntityLivingBase entity) {
		if (!isWearingCustomSkull(entity)) {
			return null;
		}
		ItemSkullBase item = getSkullItem(entity);
		return TileBlockSkull.getModel(item.getRegistryName().getResourcePath());
	}

	public static ItemStack getSkullDrop(EntityLivingBase entity) {
		if (entity instanceof EntitySkeleton) {
			return new ItemStack(Items.SKULL, 1, 0);
		}
		if (entity instanceof EntityWitherSkeleton) {
			return new ItemStack(Items.SKULL, 1, 1);
		}
		if (entity instanceof EntityZombie) {
			return new ItemStack(Items.SKULL, 1, 2);
		}
		if (entity instanceof EntityCreeper) {
			return new ItemStack(Items.SKULL, 1, 4);
		}
		if (entity instanceof EntityPlayer) {
			ItemStack head = new ItemStack(Items.SKULL, 1, 3);
			NBTTagCompound nametag = new NBTTagCompound();
			nametag.setString("SkullOwner", entity.getDisplayName().getFormattedText());
			head.setTagCompound(nametag);
			return head;
		}
		if (entity instanceof EntityDragon) {
			return new ItemStack(Items.SKULL, 1, 5);
		}
		if (entity instanceof EntityEnderman) {
			if (entity instanceof EntityEvolvedEnderman) {
				return new ItemStack(ModItems.SKULL_EVOLVED_ENDERMAN);
			}
			return new ItemStack(ModItems.SKULL_ENDERMAN);
		}
		if (entity instanceof EntityFrienderman) {
			return new ItemStack(ModItems.SKULL_FRIENDERMAN);
		}
		return null;
	}

}
