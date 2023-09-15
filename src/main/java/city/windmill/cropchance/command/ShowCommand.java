package city.windmill.cropchance.command;

import java.util.List;

import net.minecraft.command.ICommandSender;

import com.mojang.realmsclient.gui.ChatFormatting;

import ic2.core.crop.TileEntityCrop;

public class ShowCommand extends CropAction {

    public ShowCommand() {
        super("show");
    }

    @Override
    public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
        ChatBuilder c = new ChatBuilder(sender);

        c.addTitle("cropchance.cmd.show.title");
        CropCardCommand.formatCropCard(c, crop.getCrop());
        c.addAttr("cropchance.ui.attr.scan", crop.getScanLevel());
        c.addAttr(
                "cropchance.ui.attr.can-cross",
                crop.getCrop()
                    .canCross(crop))
            .commit();

        c.addTitle("cropchance.cmd.show.storage");
        c.addAttr("cropchance.ui.attr.water", crop.waterStorage);
        c.addAttr("cropchance.ui.attr.nutrient", crop.getNutrientStorage());
        c.addAttr("cropchance.ui.attr.ex", crop.getWeedExStorage())
            .commit();

        c.addTitle("cropchance.cmd.show.stat");
        c.addAttr("cropchance.ui.attr.growth", crop.getGrowth());
        c.addAttr("cropchance.ui.attr.gain", crop.getGain());
        c.addAttr("cropchance.ui.attr.resistance", crop.getResistance())
            .commit();
        c.addAttr("cropchance.ui.attr.growth-points", crop.growthPoints);
        c.addAttr("cropchance.ui.attr.size", crop.getSize())
            .text(
                ChatFormatting.WHITE,
                " (%d)",
                crop.getCrop()
                    .maxSize())
            .commit();

        c.addTitle("cropchance.cmd.show.env");
        c.addAttr("cropchance.ui.attr.humidity", crop.getHumidity());
        c.addAttr("cropchance.ui.attr.nutrient", crop.getNutrients());
        c.addAttr("cropchance.ui.attr.air", crop.getAirQuality())
            .commit();

        c.addTitle("cropchance.cmd.show.requirements");
        c.addAttr("cropchance.ui.attr.have", getHave(crop));
        c.addAttr("cropchance.ui.attr.need", getNeed(crop));
        int growthRateMin = getGrowthRateMin(crop);
        int growthRateMax = getGrowthRateMax(crop);
        c.addAttrRange("cropchance.ui.attr.growth-rate", growthRateMin, growthRateMax)
            .commit();

        int duration = crop.getCrop()
            .growthDuration(crop);
        int minTick = duration / growthRateMax;
        int maxTick = duration / growthRateMin;
        float minMs = minTick * TileEntityCrop.tickRate / 20f / 60f;
        float maxMs = maxTick * TileEntityCrop.tickRate / 20f / 60f;

        c.addAttr("cropchance.ui.attr.duration-stage", duration)
            .commit();
        c.addAttrRange("cropchance.ui.attr.in-tick", minTick, maxTick);
        c.addAttrRange("cropchance.ui.attr.in-minute", minMs, maxMs)
            .commit();

        int stage = crop.getCrop()
            .maxSize() - 1;
        int durationFull = duration * stage;
        int minTickFull = minTick * stage;
        int maxTickFull = maxTick * stage;
        float minMsFull = minMs * stage;
        float maxMsFull = maxMs * stage;
        c.addAttr("cropchance.ui.attr.duration-full", durationFull)
            .commit();
        c.addAttrRange("cropchance.ui.attr.in-tick", minTickFull, maxTickFull);
        c.addAttrRange("cropchance.ui.attr.in-minute", minMsFull, maxMsFull)
            .commit();

        c.addSeparator();
        c.build();
    }

    public static int getGrowthRateMin(TileEntityCrop crop) {
        int base = 3 + crop.getGrowth();
        return getGrowthRateInternal(crop, base);
    }

    public static int getGrowthRateMax(TileEntityCrop crop) {
        int base = 3 + crop.getGrowth() + 6;
        return getGrowthRateInternal(crop, base);
    }

    private static int getGrowthRateInternal(TileEntityCrop crop, int base) {
        if (crop.getCrop() == null) return 0;

        int need = getNeed(crop);
        int have = getHave(crop);

        int delta = have - need;

        // Crop will disappear after some time
        if (delta < -25) return -1;

        if (have >= need) base = base * (100 + delta) / 100;
        else base = base * (100 + delta * 4) / 100;

        return Math.max(0, base);
    }

    public static int getNeed(TileEntityCrop crop) {
        int need = (crop.getCrop()
            .tier() - 1) * 4 + crop.getGrowth() + crop.getGain() + crop.getResistance();
        return Math.max(0, need);
    }

    public static int getHave(TileEntityCrop crop) {
        return crop.getCrop()
            .weightInfluences(crop, crop.getHumidity(), crop.getNutrients(), crop.getAirQuality()) * 5;
    }
}
