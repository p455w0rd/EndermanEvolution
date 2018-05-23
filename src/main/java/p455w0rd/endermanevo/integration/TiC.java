package p455w0rd.endermanevo.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import p455w0rd.endermanevo.init.ModIntegration.Mods;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * @author p455w0rd
 *
 */
public class TiC {

	private static final String MOD_BEHEADING = "beheading";
	private static final String MOD_BEHEADING_CLEAVER = MOD_BEHEADING + "_cleaver";

	public static boolean hasModifier(ItemStack stack, String identifier) {
		return TinkerUtil.hasModifier(getTag(stack), identifier);
	}

	public static NBTTagCompound getTag(ItemStack stack) {
		return TagUtil.getTagSafe(stack);
	}

	public static boolean hasBeheading(ItemStack stack) {
		return hasModifier(stack, MOD_BEHEADING) || hasModifier(stack, MOD_BEHEADING_CLEAVER);
	}

	public static int getBeheadingLevel(ItemStack stack) {
		if (isTinkersItem(stack) && hasBeheading(stack)) {
			return getModifierLevel(stack, getBeheadingTypeString(stack));
		}
		return 0;
	}

	public static boolean isTinkersItem(ItemStack stack) {
		return stack.getItem().getRegistryName().getResourceDomain().equals(Mods.TINKERS.getId());
	}

	public static int getModifierLevel(ItemStack stack, String modifier) {
		int level = 0;
		if (hasModifier(stack, modifier)) {
			ModifierNBT data = ModifierNBT.readTag(TinkerUtil.getModifierTag(stack, modifier));
			if (data.level > 0) {
				return data.level;
			}
		}
		return level;
	}

	public static String getBeheadingTypeString(ItemStack stack) {
		if (hasBeheading(stack)) {
			if (hasModifier(stack, MOD_BEHEADING)) {
				return MOD_BEHEADING;
			}
			if (hasModifier(stack, MOD_BEHEADING_CLEAVER)) {
				return MOD_BEHEADING_CLEAVER;
			}
		}
		return "";
	}

}
