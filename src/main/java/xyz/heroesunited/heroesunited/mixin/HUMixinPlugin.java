package xyz.heroesunited.heroesunited.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class HUMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    private boolean isClassLoaded(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!isClassLoaded("com.mrcrayfish.obfuscate.Obfuscate")) {
            return isClassLoaded("optifine.Installer") ? !mixinClassName.equals("xyz.heroesunited.heroesunited.mixin.client.MixinLivingRenderer") : !mixinClassName.equals("xyz.heroesunited.heroesunited.mixin.client.MixinOptifineLivingRenderer");
        } else {
            return !mixinClassName.equals("xyz.heroesunited.heroesunited.mixin.client.MixinLivingRenderer") && !mixinClassName.equals("xyz.heroesunited.heroesunited.mixin.client.MixinOptifineLivingRenderer");
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}