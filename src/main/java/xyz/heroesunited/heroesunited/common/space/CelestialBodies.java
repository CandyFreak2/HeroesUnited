package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CelestialBodies {

    private static final HashMap<String, CelestialBody> CELESTIAL_BODIES = new HashMap<>();

    public static CelestialBody SUN = register("sun", new Star(new Vector3d(0,0,0),12.5F));

    public static CelestialBody ASTEROIDS_BELT = register("asteroids_belt", new CelestialBody(new Vector3d(0,0,0)));

    public static CelestialBody EARTH = register("earth", new Planet(World.OVERWORLD, new Vector3d(50,0,0),1.1F,0.0001F, (Star) SUN));
    public static CelestialBody MOON = register("moon", new Satellite(new Vector3d(2,0,0),0.05F, 0.001F, (Planet) EARTH));

    public static CelestialBody MARS = register("mars", new Planet(HeroesUnited.MARS, new Vector3d(75,0,0),2,0.0002F, (Star) SUN));


    private static CelestialBody register(String name, CelestialBody celestialBody) {
        CELESTIAL_BODIES.put( name, celestialBody);
        return celestialBody;
    }


    @SubscribeEvent
    public static void registerAbilityTypes(RegistryEvent.Register<CelestialBody> e) {
        CELESTIAL_BODIES.forEach((name, celestialBody)->{
            celestialBody.setRegistryName(HeroesUnited.MODID, name);
            e.getRegistry().register(celestialBody);
        });
    }
}