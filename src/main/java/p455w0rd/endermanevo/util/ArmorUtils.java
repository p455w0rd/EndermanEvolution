package p455w0rd.endermanevo.util;

import net.minecraft.item.ItemArmor;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;

/**
 * @author p455w0rd
 *
 */
public class ArmorUtils {

	public static ItemArmor.ArmorMaterial addArmorMaterial(String enumName, String textureName, int durability, int[] reductionAmounts, int enchantability, SoundEvent soundOnEquip, float toughness) {
		return EnumHelper.addEnum(ItemArmor.ArmorMaterial.class, enumName, new Class[] {
				String.class,
				Integer.TYPE,
				int[].class,
				Integer.TYPE,
				SoundEvent.class,
				Float.TYPE
		}, new Object[] {
				textureName,
				Integer.valueOf(durability),
				reductionAmounts,
				Integer.valueOf(enchantability),
				soundOnEquip,
				Float.valueOf(toughness)
		});
	}

}
