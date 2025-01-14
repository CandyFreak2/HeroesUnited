package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.AbilityCreator;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;

import java.util.List;
import java.util.Map;

public class HUPackPowers extends JsonReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static HUPackPowers INSTANCE;
    private final Map<ResourceLocation, List<AbilityCreator>> registeredPowers = Maps.newHashMap();

    public HUPackPowers() {
        super(GSON, "hupowers");
        INSTANCE = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager iResourceManager, IProfiler iProfiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation location = entry.getKey();
            try {
                this.registeredPowers.put(location, AbilityHelper.parsePowers((JsonObject) entry.getValue(), null, location));
            } catch (Exception e) {
                HeroesUnited.LOGGER.error("Parsing error loading power {}", location, e);
            }
        }
        HeroesUnited.LOGGER.info("Loaded {} powers", this.registeredPowers.size());
    }

    public static HUPackPowers getInstance() {
        return INSTANCE;
    }

    public static List<AbilityCreator> getPower(ResourceLocation location) {
        return getInstance().registeredPowers.get(location);
    }
}
