package p455w0rd.endermanevo.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.client.model.ModelSkullBase;

/**
 * @author p455w0rd
 *
 */
@SideOnly(Side.CLIENT)
public class TESRBlockSkull extends TileEntitySpecialRenderer<TileBlockSkull> {

	public static TESRBlockSkull INSTANCE;

	@Override
	public void render(TileBlockSkull te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		EnumFacing enumfacing = EnumFacing.getFront(te.getBlockMetadata() & 7);
		float f = te.getAnimationProgress(partialTicks);
		ModelSkullBase model = te.getModel();

		renderSkull((float) x, (float) y, (float) z, enumfacing, te.getSkullRotation() * 360 / 16.0F, model, destroyStage, f);

	}

	@Override
	public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
		super.setRendererDispatcher(rendererDispatcherIn);
		INSTANCE = this;
	}

	public void renderSkull(float x, float y, float z, EnumFacing facing, float rot, ModelSkullBase modelIn, int destroyStage, float animateTicks) {
		ModelSkullBase model = modelIn;
		if (destroyStage >= 0) {
			bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		}
		else {
			bindTexture(model.getTexture());
		}
		GlStateManager.pushMatrix();

		if (facing == EnumFacing.UP) {
			GlStateManager.translate(x + 0.5F, y, z + 0.5F);
		}
		else {
			switch (facing) {
			case NORTH:
				GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.74F);
				break;
			case SOUTH:
				GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.26F);
				rot = 180.0F;
				break;
			case WEST:
				GlStateManager.translate(x + 0.74F, y + 0.25F, z + 0.5F);
				rot = 270.0F;
				break;
			case EAST:
			default:
				GlStateManager.translate(x + 0.26F, y + 0.25F, z + 0.5F);
				rot = 90.0F;
			}
		}
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlpha();
		model.render((Entity) null, animateTicks, 0.0F, 0.0F, rot, 0.0F, 0.0625F);
		if (model.getLightMap() != null) {
			bindTexture(model.getLightMap());
			model.renderLightMap(rot);
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();

		//===============================
		/*
		if (model instanceof Enderman2) {
			GlStateManager.pushMatrix();
			float oldTexX = OpenGlHelper.lastBrightnessX;
			float oldTexY = OpenGlHelper.lastBrightnessY;
			GlStateManager.depthMask(true);
			bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/entity/charge_nocolor.png"));
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f2 = Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
			GlStateManager.translate(f2 * 0.01F, f2 * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float r = 0;
			float g = 0.75F;
			float b = 0;
			GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.color(r, g, b, 1.0F);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			GlStateManager.scale(1.1F, 1.1F, 1.1F);
			model.render(rot);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldTexX, oldTexY);
			GlStateManager.popMatrix();
		}
		*/
		//=========================

		GlStateManager.popMatrix();
		if (destroyStage >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}

	}

}
