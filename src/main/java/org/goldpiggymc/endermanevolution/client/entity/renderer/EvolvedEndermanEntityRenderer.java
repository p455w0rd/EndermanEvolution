package org.goldpiggymc.endermanevolution.client.entity.renderer;

import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import org.goldpiggymc.endermanevolution.Vars;

public class EvolvedEndermanEntityRenderer extends EndermanEntityRenderer {
    public EvolvedEndermanEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(EndermanEntity endermanEntity) {
        return new Identifier(Vars.MOD_ID, "textures/entity/evolved_enderman.png");
    }
}
