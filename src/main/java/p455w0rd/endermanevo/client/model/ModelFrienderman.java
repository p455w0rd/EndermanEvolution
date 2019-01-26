package p455w0rd.endermanevo.client.model;

import net.minecraft.entity.EntityLivingBase;
import p455w0rd.endermanevo.entity.EntityFrienderman;

/**
 * @author p455w0rd
 *
 */
public class ModelFrienderman extends ModelEndermanBase {

	public ModelFrienderman() {
		super(0.05F);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
		if (entity instanceof EntityFrienderman) {
			EntityFrienderman frienderman = (EntityFrienderman) entity;
			isCarrying = frienderman.getHeldBlockState() != null || !frienderman.getHeldItemStack().isEmpty();
			isPartying = frienderman.isPartying();
		}
	}

}
