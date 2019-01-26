package p455w0rd.endermanevo.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import p455w0rd.endermanevo.api.EndermanType;
import p455w0rd.endermanevo.client.model.ModelEndermanBase;
import p455w0rd.endermanevo.client.model.layers.LayerFriendermanEyes;
import p455w0rd.endermanevo.client.model.layers.LayerHeldBlock3;
import p455w0rd.endermanevo.entity.EntityFrienderman;

/**
 * @author p455w0rd
 *
 */
public class RenderFrienderman extends RenderEndermanBase<EntityFrienderman> {

	public RenderFrienderman() {
		super(EndermanType.FRIENDERMAN);
		addLayer(new LayerFriendermanEyes(this));
		addLayer(new LayerHeldBlock3(this));
	}

	@Override
	public void doRender(EntityFrienderman entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (endermanModel instanceof ModelEndermanBase) {
			ModelEndermanBase entityModel = (ModelEndermanBase) endermanModel;
			IBlockState iblockstate = entity.getHeldBlockState();
			ItemStack stack = entity.getHeldItemStack();
			entityModel.isCarrying = iblockstate != null || !stack.isEmpty();
			if (entityModel.isCarrying && iblockstate != null) {
				entityModel.carriedBlock = iblockstate.getBlock();
			}
			entityModel.isAttacking = entity.isScreaming();
			entityModel.isPartying = entity.isPartying();
			if (entity.isScreaming()) {
				x += getRandom().nextGaussian() * 0.02D;
				z += getRandom().nextGaussian() * 0.02D;
			}
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

}
