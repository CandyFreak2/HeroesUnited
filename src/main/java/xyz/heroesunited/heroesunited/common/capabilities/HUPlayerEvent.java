package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilities;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncSuperpower;

public class HUPlayerEvent {

    @SubscribeEvent
    public void attachCap(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(HeroesUnited.MODID, "huplayer"), new HUPlayerProvider((PlayerEntity) event.getObject()));
        }
    }

    @SubscribeEvent
    public void clonePlayer(PlayerEvent.Clone event) {
        IHUPlayer newCap = HUPlayer.getCap(event.getPlayer());
        IHUPlayer oldCap = HUPlayer.getCap(event.getOriginal());
        newCap.deserializeNBT(oldCap.serializeNBT());
        newCap.copy(oldCap);
        newCap.sync();
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking e) {
        e.getTarget().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            if (e.getPlayer() instanceof ServerPlayerEntity) {
                syncServerMessages(e.getEntity(), a);
            }
        });
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent e) {
        e.getEntity().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            if (e.getEntity() instanceof ServerPlayerEntity) {
                syncServerMessages(e.getEntity(), a);
            }
        });
    }

    public void syncServerMessages(Entity entity, IHUPlayer a) {
        a.sync();
        if (a.getSuperpower() != null) HUNetworking.INSTANCE.sendTo(new ClientSyncSuperpower(entity.getEntityId(), a.getSuperpower().getRegistryName()), ((ServerPlayerEntity) entity).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(entity.getEntityId(), AbilityHelper.getAbilities((PlayerEntity) entity)), ((ServerPlayerEntity) entity).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
}