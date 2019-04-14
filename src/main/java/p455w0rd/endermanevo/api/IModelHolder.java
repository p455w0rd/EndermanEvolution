package p455w0rd.endermanevo.api;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import p455w0rd.endermanevo.client.render.ItemLayerWrapper;

/**
 * @author p455w0rd
 *
 */
public interface IModelHolder {

	// Where you would normally call ModelLoader#setCustomModelResourceLocation
	default void initModel(final IModelHolder item) {
		ModelLoader.setCustomModelResourceLocation((Item) item, 0, item.getModelResource((Item) item));
	}

	//used for TEISR rendering
	default ItemLayerWrapper getWrappedModel() {
		return null;
	}

	//used for TEISR rendering
	default void setWrappedModel(final ItemLayerWrapper wrappedModel) {
	}

	//used for TEISR rendering
	default boolean shouldUseInternalTEISR() {
		return false;
	}

	//used for TEISR rendering (in situations where the item model uses a different registry name than the item)
	default ModelResourceLocation getModelResource(final Item item) {
		return new ModelResourceLocation(item.getRegistryName(), "inventory");
	}

}