package xyz.heroesunited.heroesunited.client.render.renderer.space;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.space.SunModel;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

public class SunRenderer extends StarRenderer {
    public SunRenderer() {
        super(new SunModel());
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(HeroesUnited.MODID, "textures/planets/sun.png");
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers, int packedLight, float partialTicks) {
        matrixStack.scale(12.5F, 12.5F, 12.5F);
        matrixStack.translate(0, -1.5, 0);
        IVertexBuilder buffer = SunModel.SUN_TEXTURE_MATERIAL.buffer(buffers, HUClientUtil.HURenderTypes::sunRenderer);
        starModel.prepareModel(partialTicks);
        starModel.renderToBuffer(matrixStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    @Override
    protected RenderType getRenderType() {
        return RenderType.entityTranslucent(getTextureLocation());
    }
}
