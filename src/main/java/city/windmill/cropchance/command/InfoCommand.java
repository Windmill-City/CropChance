package city.windmill.cropchance.command;

import city.windmill.cropchance.mixin.MixinIC2Crops;
import ic2.api.crops.Crops;
import ic2.core.crop.IC2Crops;
import ic2.core.crop.TileEntityCrop;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import java.util.*;
import java.util.stream.Collectors;


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
            AdvChatComponent c = new AdvChatComponent(sender);
            ChunkCoordinates coord = sender.getPlayerCoordinates();
            BiomeGenBase biome = sender.getEntityWorld()
                .getBiomeGenForCoords(coord.posX, coord.posZ);
            c.beginAttr("Biome Info");
            showBiomeInfo(c, biome);
            c.endAttr();
        }

        public static void showBiomeInfo(AdvChatComponent c, BiomeGenBase biome) {
            c.attr("Name", biome.biomeName);
            c.attr("Id", biome.biomeID);
            c.attr(
                "Type",
                Arrays.stream(BiomeDictionary.getTypesForBiome(biome))
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
            c.attr("Humidity", Crops.instance.getHumidityBiomeBonus(biome));
            c.attr("Nutrient", Crops.instance.getNutrientBiomeBonus(biome));
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
            int page = getPage(args);
            showBiomesInfo(new AdvChatComponent(sender), page);
        }


        public void showBiomesInfo(AdvChatComponent c, int page) {
            List<BiomeGenBase> biomes = Arrays.stream(BiomeGenBase.getBiomeGenArray())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            int total = biomes.size();

            page = MathHelper.clamp_int(page, 1, total);
            c.beginPage("Biomes", getCommandPrefix(), page, total);
            BiomeCommand.showBiomeInfo(c, biomes.get(page - 1));
            c.endPage(getCommandPrefix(), page, total);
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
            int page = getPage(args);
            showBiomeTypesPage(new AdvChatComponent(sender), page);
        }

        public void showBiomeTypesPage(AdvChatComponent c, int page) {
            List<BiomeDictionary.Type> keys = getBiomeTypeKeys();

            int itemPerPage = 2;
            int total = keys.size();
            int maxPage = (int) Math.ceil((double) total / itemPerPage);
            page = MathHelper.clamp_int(page, 1, maxPage);

            c.beginPage("Biomes Types", getCommandPrefix(), page, maxPage);

            int iStart = (page - 1) * itemPerPage;
            int iEnd = Math.min(page * itemPerPage, total);
            for (int i = iStart; i < iEnd; i++) {
                BiomeDictionary.Type key = keys.get(i);
                c.attr("Name", key.name());
                c.attr("Humidity", getHumidity(key));
                c.attr("Nutrient", getNutrient(key));
                // Not last line
                if (i != iEnd - 1) c.endAttr();
            }

            c.endPage(getCommandPrefix(), page, maxPage);
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
            showTickRate(new AdvChatComponent(sender));
        }

        public static void showTickRate(AdvChatComponent c) {
            c.beginAttr("Crop TickRate Info");
            c.attrSameLine("TickRate", TileEntityCrop.tickRate);
            c.attr("in second", TileEntityCrop.tickRate / 20f);
            c.endAttr();
        }
    }
}
