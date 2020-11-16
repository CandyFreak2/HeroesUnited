package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.ModelHoras;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

@OnlyIn(Dist.CLIENT)
public class RendererHoras extends BipedRenderer<Horas, ModelHoras<Horas>> {

    public RendererHoras(EntityRendererManager manager) {
        super(manager, new ModelHoras<>(), 0.0F);
    }

    @Override
    public ResourceLocation getEntityTexture(Horas entity) {
        return new ResourceLocation(HeroesUnited.MODID, "textures/entity/horas.png");
    }
}