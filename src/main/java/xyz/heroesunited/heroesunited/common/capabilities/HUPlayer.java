package xyz.heroesunited.heroesunited.common.capabilities;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import xyz.heroesunited.heroesunited.common.events.HURegisterPlayerControllers;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.HUTypes;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSetAnimation;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUPlayer;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUType;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesInventory;

import javax.annotation.Nullable;
import java.util.Map;

public class HUPlayer implements IHUPlayer {
    public final AccessoriesInventory inventory;
    protected final PlayerEntity player;
    protected final HUPlayerFactory factory = new HUPlayerFactory(this);
    protected Map<ResourceLocation, Level> superpowerLevels;
    private int theme;
    private float flightAmount, flightAmountO;
    private float slowMo = 20F;
    private boolean flying, intangible;
    protected ResourceLocation animationFile;
    private final PlayerGeoModel modelProvider = new PlayerGeoModel();

    public HUPlayer(PlayerEntity player) {
        this.player = player;
        this.superpowerLevels = Maps.newHashMap();
        this.inventory = new AccessoriesInventory(player);
    }

    @Nullable
    public static IHUPlayer getCap(Entity entity) {
        return entity.getCapability(HUPlayerProvider.CAPABILITY).orElse(null);
    }

    @Override
    public void updateFlyAmount() {
        this.flightAmountO = this.flightAmount;
        if (player.isSprinting()) {
            this.flightAmount = Math.min(1.0F, this.flightAmount + 0.07F);
        } else {
            this.flightAmount = Math.max(0.0F, this.flightAmount - 0.07F);
        }
    }

    @Override
    public float getFlightAmount(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.flightAmountO, this.flightAmount);
    }

    @Override
    public Map<ResourceLocation, Level> getSuperpowerLevels() {
        return superpowerLevels;
    }

    @Override
    public void setAnimation(String name, String controllerName, ResourceLocation animationFile, boolean loop) {
        this.animationFile = animationFile;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            getController(controllerName).markNeedsReload();
            getController(controllerName).setAnimation(new AnimationBuilder().addAnimation(name, loop));
        });
        if (!player.level.isClientSide) {
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSetAnimation(player.getId(), name, controllerName, this.modelProvider.getAnimationFileLocation(this), loop));
        }
        syncToAll();
    }

    @Override
    public float getSlowMoSpeed() {
        return slowMo;
    }

    @Override
    public void setSlowMoSpeed(float slowMo) {
        this.slowMo = slowMo;
    }

    @Override
    public boolean isFlying() {
        return flying;
    }

    @Override
    public void setFlying(boolean flying) {
        this.flying = flying;
        if (!player.level.isClientSide)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.FLYING, flying));
    }

    @Override
    public boolean isIntangible() {
        return intangible;
    }

    @Override
    public void setIntangible(boolean intangible) {
        this.intangible = intangible;
        if (!player.level.isClientSide)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.INTAGIBLE, intangible));
    }

    @Override
    public int getTheme() {
        return theme;
    }

    @Override
    public void setTheme(int theme) {
        this.theme = theme;
    }

    @Override
    public IHUPlayer copy(IHUPlayer cap) {
        this.deserializeNBT(cap.serializeNBT());
        this.theme = cap.getTheme();
        this.inventory.copy(cap.getInventory());
        this.flying = false;
        this.slowMo = 20F;
        this.sync();
        return this;
    }

    @Override
    public IHUPlayer sync() {
        if (player instanceof ServerPlayerEntity) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(player.getId(), this.serializeNBT()), ((ServerPlayerEntity) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
        return this;
    }

    @Override
    public IHUPlayer syncToAll() {
        this.sync();
        for (PlayerEntity player : this.player.level.players()) {
            if (player instanceof ServerPlayerEntity) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(this.player.getId(), this.serializeNBT()), ((ServerPlayerEntity) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return this;
    }

    @Override
    public AccessoriesInventory getInventory() {
        return inventory;
    }

    @Override
    public PlayerGeoModel getAnimatedModel() {
        return modelProvider;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 1, this::predicate));
        MinecraftForge.EVENT_BUS.post(new HURegisterPlayerControllers(this, player, data));
    }

    private <P extends IHUPlayer> PlayState predicate(AnimationEvent<P> event) {
        if (event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public AnimationController getController(String controllerName) {
        return getFactory().getOrCreateAnimationData(player.getUUID().hashCode()).getAnimationControllers().get(controllerName);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        CompoundNBT levels = new CompoundNBT();
        superpowerLevels.forEach((resourceLocation, level) -> levels.put(resourceLocation.toString(), level.writeNBT()));
        nbt.put("levels", levels);
        nbt.putBoolean("Flying", this.flying);
        nbt.putFloat("SlowMo", this.slowMo);
        nbt.putBoolean("Intangible", this.intangible);
        nbt.putInt("Theme", this.theme);
        if (this.animationFile != null) {
            nbt.putString("AnimationFile", this.animationFile.toString());
        }
        ItemStackHelper.saveAllItems(nbt, this.inventory.getItems());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT levels = nbt.getCompound("levels");
        superpowerLevels.clear();
        for (String key : levels.getAllKeys()) {
            superpowerLevels.put(new ResourceLocation(key), Level.readFromNBT(levels.getCompound(key)));
        }
        if (nbt.contains("Flying")) {
            this.flying = nbt.getBoolean("Flying");
        }
        if (nbt.contains("Intangible")) {
            this.intangible = nbt.getBoolean("Intangible");
        }
        if (nbt.contains("SlowMo")) {
            this.slowMo = nbt.getFloat("SlowMo");
        }
        if (nbt.contains("Theme")) {
            this.theme = nbt.getInt("Theme");
        }
        if (nbt.contains("AnimationFile")) {
            this.animationFile = new ResourceLocation(nbt.getString("AnimationFile"));
        }
        inventory.getItems().clear();
        ItemStackHelper.loadAllItems(nbt, inventory.getItems());

    }

    @Override
    public int tickTimer() {
        return this.player.tickCount;
    }
}
