package p455w0rd.endermanevo.init;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.IModelHolder;
import p455w0rd.endermanevo.client.render.ItemLayerWrapper;
import p455w0rd.endermanevo.client.render.TESRBlockSkull.TEISRBlockSkull;
import p455w0rd.endermanevo.items.*;

public class ModItems {

	public static final ItemFrienderPearl FRIENDER_PEARL = new ItemFrienderPearl();
	public static final ItemSkullBase.Enderman SKULL_ENDERMAN = new ItemSkullBase.Enderman();
	public static final ItemSkullBase.Frienderman SKULL_FRIENDERMAN = new ItemSkullBase.Frienderman();
	public static final ItemSkullBase.EvolvedEnderman SKULL_EVOLVED_ENDERMAN = new ItemSkullBase.EvolvedEnderman();
	public static final ItemEnderFlower ENDER_FLOWER = new ItemEnderFlower();
	public static final ItemEnderFragment ENDER_FRAGMENT = new ItemEnderFragment();
	private static final Item[] ITEM_ARRAY = new Item[] {
			FRIENDER_PEARL, SKULL_ENDERMAN, SKULL_FRIENDERMAN, SKULL_EVOLVED_ENDERMAN, ENDER_FLOWER, ENDER_FRAGMENT
	};

	@SideOnly(Side.CLIENT)
	public static final void initModels(final ModelBakeEvent event) {
		for (final Item item : ITEM_ARRAY) {
			if (item instanceof IModelHolder) {
				final IModelHolder holder = (IModelHolder) item;
				holder.initModel(holder);
				if (holder.shouldUseInternalTEISR()) {
					final IBakedModel currentModel = event.getModelRegistry().getObject(new ModelResourceLocation(item.getRegistryName(), "inventory"));
					holder.setWrappedModel(new ItemLayerWrapper(currentModel));
					event.getModelRegistry().putObject(new ModelResourceLocation(item.getRegistryName(), "inventory"), holder.getWrappedModel());
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static final void registerTEISRs(final ModelRegistryEvent event) {
		for (final Item item : getList()) {
			if (item instanceof IModelHolder) {
				final IModelHolder holder = (IModelHolder) item;
				if (holder.shouldUseInternalTEISR()) {
					item.setTileEntityItemStackRenderer(new TEISRBlockSkull().setModel(holder.getWrappedModel()));
				}
			}
		}
	}

	public static List<Item> getList() {
		return Lists.newArrayList(ITEM_ARRAY);
	}

}
