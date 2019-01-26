package p455w0rd.endermanevo.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author p455w0rd
 *
 */
public class ParticleEvolvedEndermanAggroPortal extends Particle {
	private final float portalParticleScale;
	private final double portalPosX;
	private final double portalPosY;
	private final double portalPosZ;

	public ParticleEvolvedEndermanAggroPortal(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		motionX = xSpeedIn;
		motionY = ySpeedIn;
		motionZ = zSpeedIn;
		posX = xCoordIn;
		posY = yCoordIn;
		posZ = zCoordIn;
		portalPosX = posX;
		portalPosY = posY;
		portalPosZ = posZ;
		float f = rand.nextFloat() * 0.6F + 0.4F;
		particleScale = rand.nextFloat() * 0.2F + 0.5F;
		portalParticleScale = particleScale;
		particleRed = f * 1.0F;
		particleGreen = f * 0.1F;
		particleBlue = f * 0.2F;
		particleMaxAge = (int) (Math.random() * 10.0D) + 40;
		setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	@Override
	public void move(double x, double y, double z) {
		setBoundingBox(getBoundingBox().offset(x, y, z));
		resetPositionToBB();
	}

	/**
	 * Renders the particle
	 */
	@Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float f = (particleAge + partialTicks) / particleMaxAge;
		f = 1.0F - f;
		f = f * f;
		f = 1.0F - f;
		particleScale = portalParticleScale * f;
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 260f, 260f);
		super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
		GlStateManager.popMatrix();
	}

	@Override
	public int getBrightnessForRender(float brightness) {
		int i = super.getBrightnessForRender(brightness);

		float f = (float) particleAge / (float) particleMaxAge;
		f = f * f;
		f = f * f;
		int j = i & 255;
		int k = i >> 16 & 255;
		k = k + (int) (f * 15.0F * 16.0F);

		if (k > 240) {
			k = 240;
		}

		return j | k << 16 / 200;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		float f = (float) particleAge / (float) particleMaxAge;
		float f1 = -f + f * f * 2.0F;
		float f2 = 1.0F - f1;
		posX = portalPosX + motionX * f2;
		posY = portalPosY + motionY * f2 + (1.0F - f);
		posZ = portalPosZ + motionZ * f2;

		if (particleAge++ >= particleMaxAge) {
			setExpired();
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Factory implements IParticleFactory {
		@Override
		public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
			return new ParticleEvolvedEndermanPortal(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		}
	}
}