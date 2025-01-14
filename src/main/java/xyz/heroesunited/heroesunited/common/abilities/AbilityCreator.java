package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;

public class AbilityCreator {

    protected final String key;
    protected final AbilityType abilityType;
    protected final JsonObject abilityJson, jsonObject;

    public AbilityCreator(String key, AbilityType abilityType, JsonObject abilityJson, JsonObject jsonObject) {
        this.key = key;
        this.abilityType = abilityType;
        this.abilityJson = abilityJson;
        this.jsonObject = jsonObject;
    }

    public String getKey() {
        return key;
    }

    public Ability create(PlayerEntity player) {
        Ability ability = this.abilityType.create(this.key);

        if (this.jsonObject != null) {
            ability.getConditionManager().registerConditions("conditions_for_abilities", this.jsonObject);
        }

        if (this.abilityJson != null) {
            ability.setJsonObject(player, this.abilityJson);
        }
        return ability;
    }
}
