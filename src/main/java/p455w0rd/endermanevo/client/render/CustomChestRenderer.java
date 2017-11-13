package p455w0rd.endermanevo.client.render;

import java.util.Calendar;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelShulker;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderShulker;
import net.minecraft.util.ResourceLocation;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class CustomChestRenderer {

	public static final ModelChest CHEST_MODEL = new ModelChest();
	public static final ModelShulker SHULKER_BOX_MODEL = new ModelShulker();

	//private ChestType type;
	/*
	public CustomChestRenderer(ChestType typeIn) {
		type=typeIn;
		Calendar calendar = Calendar.getInstance();
	    if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26 && type==ChestType.NORMAL) {
	        type=ChestType.CHRISTMAS;
	    }
	}
	*/
	public static void renderChest(ChestType typeIn, float lidAngle) {
		ChestType type = typeIn;
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26 && type == ChestType.NORMAL) {
			type = ChestType.CHRISTMAS;
		}
		GlStateManager.color(1, 1, 1, 1);
		RenderUtils.bindTexture(type.getTexture());
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 1.0, 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(2 * 90, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		CHEST_MODEL.chestLid.rotateAngleX = lidAngle;
		CHEST_MODEL.renderAll();
		GlStateManager.popMatrix();
	}

	public static void renderShulkerBox(int color, float lidProgress) {
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		RenderUtils.bindTexture(RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[color]);
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(0 + 0.5F, 0 + 1.5F, 0 + 0.5F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.0F, 1.0F, 0.0F);
		GlStateManager.scale(0.9995F, 0.9995F, 0.9995F);
		GlStateManager.translate(0.0F, -1.0F, 0.0F);
		SHULKER_BOX_MODEL.base.render(0.0625F);
		GlStateManager.translate(0.0F, -lidProgress * 0.5F, 0.0F);
		GlStateManager.rotate(270.0F * lidProgress, 0.0F, 1.0F, 0.0F);
		SHULKER_BOX_MODEL.lid.render(0.0625F);
		GlStateManager.enableCull();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static enum ChestType {
			NORMAL(new ResourceLocation("textures/entity/chest/normal.png")),
			TRAPPED(new ResourceLocation("textures/entity/chest/trapped.png")),
			CHRISTMAS(new ResourceLocation("textures/entity/chest/christmas.png")),
			ENDER(new ResourceLocation("textures/entity/chest/ender.png"));

		ResourceLocation texture;

		ChestType(ResourceLocation textureIn) {
			texture = textureIn;
		}

		public ResourceLocation getTexture() {
			return texture;
		}
	}

}
