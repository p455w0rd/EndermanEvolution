package p455w0rd.endermanevo.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class ModelSkullBase extends ModelBiped {

	private static ModelEnderman ENDERMAN_MODEL_INSTANCE = new ModelEnderman(0.0F);

	public ModelRenderer head;
	protected ModelRenderer overlay;
	private boolean renderOverlay;
	private ResourceLocation TEXTURE = null;
	private ResourceLocation LIGHTMAP = null;

	public ModelSkullBase() {
		this(32);
	}

	protected ModelSkullBase(final int height) {
		textureWidth = 64;
		textureHeight = height;
		renderOverlay = true;
		head = new ModelRenderer(this, 0, 0);
		overlay = new ModelRenderer(this, 32, 0);
		head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		overlay.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
		overlay.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	public static ModelEnderman getEndermanModelInstace() {
		return ENDERMAN_MODEL_INSTANCE;
	}

	public ResourceLocation getTexture() {
		return TEXTURE;
	}

	public ResourceLocation getLightMap() {
		return LIGHTMAP;
	}

	protected void setLightMap(final ResourceLocation texture) {
		LIGHTMAP = texture;
	}

	protected void setTexture(final ResourceLocation texture) {
		TEXTURE = texture;
	}

	public float playerRenderOffset() {
		return 0;
	}

	protected ModelSkullBase hideOverlay() {
		renderOverlay = false;
		return this;
	}

	public void render(final float rotationX) {
		render(rotationX, 0.0F, null);
	}

	public void render(final float rotationX, final float rotationY, final EntityLivingBase entity) {
		if (entity != null && entity.isSneaking()) {
			GlStateManager.translate(0.0F, 0.175F, 0.0F);
		}
		else {
			GlStateManager.translate(0.0F, 0.02F, 0.0F);
		}
		head.rotateAngleY = rotationX / (180F / (float) Math.PI);
		head.rotateAngleX = rotationY / (180F / (float) Math.PI);
		if (entity == null || entity != null && !entity.isInvisible()) {
			head.render(0.0625F);
		}
		if (renderOverlay) {
			if (entity == null || entity != null && !entity.isInvisible()) {
				renderOverlay(rotationX, rotationY);
			}
		}
		if (entity != null && entity.isSneaking()) {
			GlStateManager.translate(0.0F, -0.175F, 0.0F);
		}
		else {
			GlStateManager.translate(0.0F, -0.02F, 0.0F);
		}
		if (entity != null && entity.isSneaking()) {
			GlStateManager.translate(0, 0.2f, 0);
		}
		if (this instanceof Enderman2) {

			GlStateManager.pushMatrix();
			final float oldTexX = OpenGlHelper.lastBrightnessX;
			final float oldTexY = OpenGlHelper.lastBrightnessY;
			GlStateManager.depthMask(false);
			bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/entity/charge_nocolor.png"));
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			final float f = Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
			GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			final float r = 0;
			final float g = 0.75F;
			final float b = 0;
			GlStateManager.color(r, g, b, 0.5F);
			RenderHelper.enableStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 260.0F, 260.0F);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			GlStateManager.scale(1.1F, 1.1F, 1.1F);
			head.render(0.0625F);
			GlStateManager.translate(-(f * 0.01F), -(f * 0.01F), 0.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldTexX, oldTexY);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
			GlStateManager.popMatrix();
		}
		if (entity != null && entity.isSneaking()) {
			GlStateManager.translate(0, -0.2f, 0);
		}
	}

	public void bindTexture(final ResourceLocation texture) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	}

	@Override
	public void render(final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
		if (entityIn != null && entityIn instanceof EntityLivingBase) {
			render(netHeadYaw, headPitch, (EntityLivingBase) entityIn);
		}
		else {
			render(netHeadYaw, headPitch, null);
		}

	}

	public void renderOverlay(final float rotationX) {
		renderOverlay(rotationX, 0.0F);
	}

	public void renderOverlay(final float rotationX, final float rotationY) {
		overlay.rotateAngleY = rotationX / (180F / (float) Math.PI);
		overlay.rotateAngleX = rotationY / (180F / (float) Math.PI);
		overlay.render(0.0625F);
	}

	protected void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void renderLightMap(final float skullRotation) {
		renderLightMap(skullRotation, 0.0F, null);
	}

	public void renderLightMap(final float skullRotation, final float skullPitch, final EntityLivingBase entity) {
		GlStateManager.pushMatrix();
		final float brightnessX = OpenGlHelper.lastBrightnessX;
		final float brightnessY = OpenGlHelper.lastBrightnessY;
		RenderHelper.enableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		if (entity != null && entity instanceof EntityLivingBase) {
			render(skullRotation, skullPitch, entity);
		}
		else {
			render(skullRotation, skullPitch, null);
		}
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.popMatrix();
		GlStateManager.enableBlend();
	}

	public void renderLightMapOnPlayerHead(final World world, final EntityLivingBase wearer) {
		GlStateManager.pushMatrix();
		final float brightnessX = OpenGlHelper.lastBrightnessX;
		final float brightnessY = OpenGlHelper.lastBrightnessY;
		RenderHelper.enableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		render(wearer.getRotationYawHead(), wearer.rotationPitch, wearer);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.popMatrix();
	}

	public static class Enderman extends ModelSkullBase {

		protected static final ResourceLocation TEXTURE_ENDERMAN = new ResourceLocation("textures/entity/enderman/enderman.png");
		protected static final ResourceLocation LIGHTMAP_ENDERMAN = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
		private static Enderman INSTANCE = new Enderman();

		public Enderman() {
			setTexture(TEXTURE_ENDERMAN);
			setLightMap(LIGHTMAP_ENDERMAN);
			final ModelEnderman model = getEndermanModelInstace();
			head = model.bipedHead;
			overlay = model.bipedHeadwear;
			model.isAttacking = true;
			head.setRotationPoint(0F, 0F, 0F);
			overlay.setRotationPoint(0F, 0F, 0F);
		}

		public static Enderman getInstance() {
			return INSTANCE;
		}

	}

	public static class Frienderman extends ModelSkullBase {

		protected static final ResourceLocation TEXTURE_ENDERMAN = new ResourceLocation(ModGlobals.MODID, "textures/entity/frienderman.png");
		protected static final ResourceLocation LIGHTMAP_ENDERMAN = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
		private static Frienderman INSTANCE = new Frienderman();

		public Frienderman() {
			setTexture(TEXTURE_ENDERMAN);
			setLightMap(LIGHTMAP_ENDERMAN);
			final ModelEnderman model = getEndermanModelInstace();
			head = model.bipedHead;
			overlay = model.bipedHeadwear;
			model.isAttacking = true;
			head.setRotationPoint(0F, 0F, 0F);
			overlay.setRotationPoint(0F, 0F, 0F);
		}

		public static Frienderman getInstance() {
			return INSTANCE;
		}

	}

	public static class Enderman2 extends ModelSkullBase {

		protected static final ResourceLocation TEXTURE_ENDERMAN = new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman_evolved.png");
		protected static final ResourceLocation LIGHTMAP_ENDERMAN = new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman_evolved_eyes.png");
		private static Enderman2 INSTANCE = new Enderman2();

		public Enderman2() {
			setTexture(TEXTURE_ENDERMAN);
			setLightMap(LIGHTMAP_ENDERMAN);
			final ModelEnderman model = getEndermanModelInstace();
			head = model.bipedHead;
			overlay = model.bipedHeadwear;
			model.isAttacking = true;
			model.bipedHeadwear.showModel = true;
			model.bipedHeadwear.isHidden = false;
			head.setRotationPoint(0F, 0F, 0F);
			overlay.setRotationPoint(0F, 0F, 0F);
		}

		public static Enderman2 getInstance() {
			return INSTANCE;
		}

	}

}