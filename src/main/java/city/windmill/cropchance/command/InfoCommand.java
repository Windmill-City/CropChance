package city.windmill.cropchance.command;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import city.windmill.cropchance.mixin.MixinIC2Crops;
import ic2.api.crops.Crops;
import ic2.core.crop.IC2Crops;
import ic2.core.crop.TileEntityCrop;

public class InfoCommand extends BasicCommand {

    public InfoCommand() {
        super("info");
        addChild(new BiomeCommand());
        addChild(new BiomesCommand());
        addChild(new BiomeTypesCommand());
        addChild(new TickRateCommand());
    }

    public static class BiomeCommand extends BasicCommand {

        public BiomeCommand() {
            super("biome");
        }

        @Override
        public void processCommand(ICommandSender sender, List<String> args) {
            ChatBuilder c = new ChatBuilder(sender);
            ChunkCoordinates coord = sender.getPlayerCoordinates();
            BiomeGenBase biome = sender.getEntityWorld()
                .getBiomeGenForCoords(coord.posX, coord.posZ);

            c.addTitle("Biome Info");
            showBiomeInfo(c, biome);
            c.addSeparator();
            c.build();
        }

        public static void showBiomeInfo(ChatBuilder c, BiomeGenBase biome) {
            c.addAttr("Name", biome.biomeName)
                .commit();
            c.addAttr("Id", biome.biomeID)
                .commit();
            c.addAttr(
                "Type",
                Arrays.stream(BiomeDictionary.getTypesForBiome(biome))
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")))
                .commit();
            int humidity = Crops.instance.getHumidityBiomeBonus(biome);
            int nutrient = Crops.instance.getNutrientBiomeBonus(biome);
            c.addAttr("Humidity", humidity);
            c.addAttr("Nutrient", nutrient);
            c.addAttr("Sum", humidity + nutrient)
                .commit();
        }

    }

    public static class BiomesCommand extends BasicCommand {

        public BiomesCommand() {
            super("biomes");
        }

        @Override
        public String getHelp() {
            return getCommandPrefix() + " [page]";
        }

        @Override
        public void processCommand(ICommandSender sender, List<String> args) {
            List<BiomeGenBase> biomes = Arrays.stream(BiomeGenBase.getBiomeGenArray())
                .filter(Objects::nonNull)
                .sorted((l, r) -> {
                    int humidity_l = Crops.instance.getHumidityBiomeBonus(l);
                    int nutrient_l = Crops.instance.getNutrientBiomeBonus(l);
                    int sum_l = humidity_l + nutrient_l;
                    int humidity_r = Crops.instance.getHumidityBiomeBonus(r);
                    int nutrient_r = Crops.instance.getNutrientBiomeBonus(r);
                    int sum_r = humidity_r + nutrient_r;
                    return sum_r - sum_l;
                })
                .collect(Collectors.toList());

            ChatBuilder c = new ChatBuilder(sender);
            c.addPage(
                "Biomes Info",
                getCommandPrefix(),
                getPage(args),
                biomes,
                biome -> BiomeCommand.showBiomeInfo(c, biome));
            c.build();
        }
    }

    public static class BiomeTypesCommand extends BasicCommand {

        public BiomeTypesCommand() {
            super("types");
        }

        @Override
        public String getHelp() {
            return getCommandPrefix() + " [page]";
        }

        @Override
        public void processCommand(ICommandSender sender, List<String> args) {
            ChatBuilder c = new ChatBuilder(sender);
            c.addPage(
                "Biome Types",
                getCommandPrefix(),
                getPage(args),
                getBiomeTypeKeys().stream()
                    .sorted((l, r) -> {
                        int humidity_l = getHumidity(l);
                        int nutrient_l = getNutrient(l);
                        int sum_l = humidity_l + nutrient_l;
                        int humidity_r = getHumidity(r);
                        int nutrient_r = getNutrient(r);
                        int sum_r = humidity_r + nutrient_r;
                        return sum_r - sum_l;
                    })
                    .collect(Collectors.toList()),
                key -> {
                    c.addAttr("Name", key.name())
                        .commit();
                    int humidity = getHumidity(key);
                    int nutrient = getNutrient(key);
                    c.addAttr("Humidity", humidity);
                    c.addAttr("Nutrient", nutrient);
                    c.addAttr("Sum", humidity + nutrient)
                        .commit();
                });
            c.build();
        }

        public static List<BiomeDictionary.Type> getBiomeTypeKeys() {
            HashSet<BiomeDictionary.Type> keys = new HashSet<>();
            MixinIC2Crops crops = (MixinIC2Crops) IC2Crops.instance;
            keys.addAll(
                crops.getHumidityBiomeTypeBonus()
                    .keySet());
            keys.addAll(
                crops.getNutrientBiomeTypeBonus()
                    .keySet());
            return new ArrayList<>(keys);
        }

        public static int getHumidity(BiomeDictionary.Type type) {
            MixinIC2Crops crops = (MixinIC2Crops) IC2Crops.instance;
            return crops.getHumidityBiomeTypeBonus()
                .getOrDefault(type, 0);
        }

        public static int getNutrient(BiomeDictionary.Type type) {
            MixinIC2Crops crops = (MixinIC2Crops) IC2Crops.instance;
            return crops.getNutrientBiomeTypeBonus()
                .getOrDefault(type, 0);
        }
    }

    public static class TickRateCommand extends BasicCommand {

        public TickRateCommand() {
            super("tickrate");
        }

        @Override
        public void processCommand(ICommandSender sender, List<String> args) {
            ChatBuilder c = new ChatBuilder(sender);
            c.addTitle("Crop TickRate Info");
            c.addAttr("TickRate", TileEntityCrop.tickRate);
            c.addAttr("in second", TileEntityCrop.tickRate / 20f)
                .commit();
            c.addSeparator();
            c.build();
        }

    }
}
