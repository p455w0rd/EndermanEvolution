package p455w0rd.endermanevo.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import p455w0rd.endermanevo.client.particle.ParticleLove;
import p455w0rd.endermanevo.client.particle.ParticlePortal2;
import p455w0rd.endermanevo.client.particle.ParticleVanillaPortal;
import p455w0rd.endermanevo.client.render.ParticleRenderer;

/**
 * @author p455w0rd
 *
 */
public class ParticleUtil {

	public static void spawn(EnumParticles particleType, World world, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		if (world == null || FMLCommonHandler.instance().getSide().isServer() || Minecraft.getMinecraft().gameSettings.particleSetting > 0) {
			return;
		}
		Particle particle = null;
		switch (particleType) {
		case LOVE:
			particle = new ParticleLove(world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
			break;
		case PORTAL_GREEN:
			particle = new ParticlePortal2(world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
			break;
		case PORTAL:
			particle = new ParticleVanillaPortal(world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
			break;
		default:
			break;
		}
		switch (particleType.getRenderer()) {
		case "custom":
			ParticleRenderer.getInstance().addParticle(particle);
			break;
		case "vanilla":
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			break;
		}

	}

}