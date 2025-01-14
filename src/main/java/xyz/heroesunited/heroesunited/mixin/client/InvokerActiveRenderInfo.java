package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ActiveRenderInfo.class)
public interface InvokerActiveRenderInfo {

    @Invoker("move")
    void invokeMove(double x, double y, double z);

    @Invoker("setPosition")
    void invokeSetPosition(Vector3d vector3d);

    @Invoker("setRotation")
    void invokeSetRotation(float yRot, float xRot);
}
