package city.windmill.cropchance.command;

import city.windmill.cropchance.mixin.MixinIC2Crops;
import com.mojang.realmsclient.gui.ChatFormatting;
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

public class InfoCommand extends SubCommand {
    public InfoCommand() {
        super("info");

    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AdvChatComponent c = new AdvChatComponent(sender);

        int page = 1;
        if (args.length == 1) {
            if (args[0].equals("tick")) {
                infoTick(c);
                return;
            }
            if (args[0].equals("types")) {
                showBiomeTypesPage(c, page);
                return;
            }
            if (args[0].equals("biome")) {
                ChunkCoordinates coord = sender.getPlayerCoordinates();
                BiomeGenBase biome = sender.getEntityWorld().getBiomeGenForCoords(coord.posX, coord.posZ);
                c.beginAttr("Biome Info");
                showBiomeInfo(c, biome);
                c.endAttr();
                return;
            }
            if (args[0].equals("biomes")) {
                showBiomesInfo(c, page);
                return;
            }
        }

        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                printHelpTypes(c);
                return;
            }
            if (args[0].equals("biomes")) {
                showBiomesInfo(c, page);
                return;
            }
            if (args[0].equals("types")) {
                showBiomeTypesPage(c, page);
                return;
            }
        }
        printHelp(c);
    }

    public static void printHelpTypes(AdvChatComponent c) {
        c.text(ChatFormatting.RESET, "/crop info types <page>").commit();
    }

    public static void printHelpBiomes(AdvChatComponent c) {
        c.text(ChatFormatting.RESET, "/crop info biomes <page>").commit();
    }

    public static void printHelp(AdvChatComponent c) {
        c.text(ChatFormatting.RESET, "/crop info <tick | types | biome>").commit();
    }

    public static void showBiomeInfo(AdvChatComponent c, BiomeGenBase biome) {
        c.attr("Name", biome.biomeName);
        c.attr("Id", biome.biomeID);
        c.attr("Type", Arrays.stream(BiomeDictionary.getTypesForBiome(biome))
            .map(Enum::name)
            .collect(Collectors.joining(", ")));
        c.attr("Humidity", Crops.instance.getHumidityBiomeBonus(biome));
        c.attr("Nutrient", Crops.instance.getNutrientBiomeBonus(biome));
    }

    public static void showBiomesInfo(AdvChatComponent c, int page) {
        List<BiomeGenBase> biomes = Arrays.stream(BiomeGenBase.getBiomeGenArray())
            .filter(Objects::nonNull)
            .toList();
        int total = biomes.size();

        page = MathHelper.clamp_int(page, 1, total);
        c.beginPage("Biomes", "/crop info biomes %d", page, page - 1, page + 1, total);
        showBiomeInfo(c, biomes.get(page - 1));
        c.endPage("/crop info biomes %d", page, page - 1, page + 1, total);
    }

    public static List<BiomeDictionary.Type> getBiomeTypeKeys() {
        HashSet<BiomeDictionary.Type> keys = new HashSet<>();
        MixinIC2Crops crops = (MixinIC2Crops) IC2Crops.instance;
        keys.addAll(crops.getHumidityBiomeTypeBonus().keySet());
        keys.addAll(crops.getNutrientBiomeTypeBonus().keySet());
        return new ArrayList<>(keys);
    }

    public static int getHumidity(BiomeDictionary.Type type) {
        MixinIC2Crops crops = (MixinIC2Crops) IC2Crops.instance;
        return crops.getHumidityBiomeTypeBonus().getOrDefault(type, 0);
    }

    public static int getNutrient(BiomeDictionary.Type type) {
        MixinIC2Crops crops = (MixinIC2Crops) IC2Crops.instance;
        return crops.getNutrientBiomeTypeBonus().getOrDefault(type, 0);
    }

    public static void showBiomeTypesPage(AdvChatComponent c, int page) {
        List<BiomeDictionary.Type> keys = getBiomeTypeKeys();

        int itemPerPage = 2;
        int total = keys.size();
        int maxPage = (int) Math.ceil((double) total / itemPerPage);
        page = MathHelper.clamp_int(page, 1, maxPage);

        c.beginPage("Biomes Types", "/crop info types %d", page, page - 1, page + 1, maxPage);

        int iStart = (page - 1) * itemPerPage;
        int iEnd = Math.min(page * itemPerPage, total);
        for (int i = iStart; i < iEnd; i++) {
            BiomeDictionary.Type key = keys.get(i);
            c.attr("Name", key.name());
            c.attr("Humidity", getHumidity(key));
            c.attr("Nutrient", getNutrient(key));
            //Not last line
            if (i != iEnd - 1)
                c.endAttr();
        }

        c.endPage("/crop info types %d", page, page - 1, page + 1, maxPage);
    }


    public static void infoTick(AdvChatComponent c) {
        c.beginAttr("Tick Info");
        c.attrSameLine("TickRate", TileEntityCrop.tickRate);
        c.attr("in second", TileEntityCrop.tickRate / 20f);
        c.endAttr();
    }

}
