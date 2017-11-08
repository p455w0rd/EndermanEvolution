package p455w0rd.endermanevo.init;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import p455w0rd.endermanevo.util.ArmorUtils;

/**
 * @author p455w0rd
 *
 */
public class ModMaterials {

	public static final ItemArmor.ArmorMaterial SKULL_MATERIAL = ArmorUtils.addArmorMaterial(ModGlobals.MODID + ":skull", ModGlobals.MODID + ":skull", -1, new int[] {
			0,
			0,
			0,
			0
	}, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);

}
