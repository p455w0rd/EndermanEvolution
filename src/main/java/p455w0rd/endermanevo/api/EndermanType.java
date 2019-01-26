package p455w0rd.endermanevo.api;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import p455w0rd.endermanevo.client.model.ModelEvolvedEnderman;
import p455w0rd.endermanevo.client.model.ModelFrienderman;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public enum EndermanType {

		EVOLED(new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman_evolved.png"), new ResourceLocation("textures/entity/enderman/enderman_eyes.png"), new ModelEvolvedEnderman(), 0.5f),
		FRIENDERMAN(new ResourceLocation(ModGlobals.MODID, "textures/entity/frienderman.png"), new ResourceLocation("textures/entity/enderman/enderman_eyes.png"), new ModelFrienderman(), 0.25f);

	ResourceLocation texture;
	ResourceLocation eyesTexture;
	ModelBase model;
	float shadowSize;

	EndermanType(ResourceLocation texture, ResourceLocation eyesTexture, ModelBase model, float shadowSize) {
		this.texture = texture;
		this.eyesTexture = eyesTexture;
		this.model = model;
		this.shadowSize = shadowSize;
	}

	public ResourceLocation getEntityTexture() {
		return texture;
	}

	public ResourceLocation getEyesTexture() {
		return eyesTexture;
	}

	public ModelBase getModel() {
		return model;
	}

	public float getShadowSize() {
		return shadowSize;
	}

}
