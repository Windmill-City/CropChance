package city.windmill.cropchance.mixin;

import ic2.core.crop.IC2Crops;
import net.minecraftforge.common.BiomeDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(IC2Crops.class)
public interface MixinIC2Crops {
    @Accessor(remap = false)
    Map<BiomeDictionary.Type, Integer> getHumidityBiomeTypeBonus();

    @Accessor(remap = false)
    Map<BiomeDictionary.Type, Integer> getNutrientBiomeTypeBonus();
}
