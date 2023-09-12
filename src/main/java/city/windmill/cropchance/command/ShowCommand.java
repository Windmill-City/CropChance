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

        c.addTitle("Crop Info");
        CropCardCommand.formatCropCard(c, crop.getCrop());
        c.addAttr("Scan Level", crop.getScanLevel());
        c.addAttr(
            "CanCross",
            crop.getCrop()
                .canCross(crop))
            .commit();

        c.addTitle("Storage");
        c.addAttr("Water", crop.waterStorage);
        c.addAttr("Nutrient", crop.getNutrientStorage());
        c.addAttr("WeedEx", crop.getWeedExStorage())
            .commit();

        c.addTitle("Stat");
        c.addAttr("Growth", crop.getGrowth());
        c.addAttr("Gain", crop.getGain());
        c.addAttr("Resistance", crop.getResistance())
            .commit();
        c.addAttr("Growth Points", crop.growthPoints);
        c.addAttr("Size", crop.getSize())
            .text(
                ChatFormatting.YELLOW,
                " (%d)",
                crop.getCrop()
                    .maxSize())
            .commit();

        c.addTitle("Env");
        c.addAttr("Humidity", crop.getHumidity());
        c.addAttr("Nutrient", crop.getNutrients());
        c.addAttr("AirQuality", crop.getAirQuality())
            .commit();

        c.addTitle("Requirements");
        c.addAttr("Have", getHave(crop));
        c.addAttr("Need", getNeed(crop));
        int growthRateMin = getGrowthRateMin(crop);
        int growthRateMax = getGrowthRateMax(crop);
        int growthRateAvg = (growthRateMin + growthRateMax) / 2;
        c.addAttr("GrowthRate", "[%d, %d] (%d)", growthRateMin, growthRateMax, growthRateAvg)
            .commit();

        int duration = crop.getCrop()
            .growthDuration(crop);
        int minTick = duration / growthRateMax;
        int maxTick = duration / growthRateMin;
        float minMs = minTick * TileEntityCrop.tickRate / 20f / 60f;
        float maxMs = maxTick * TileEntityCrop.tickRate / 20f / 60f;

        c.addAttr("Duration (Stage)", duration)
            .commit();
        c.addAttrRange("Ticks", minTick, maxTick);
        c.addAttrRange("Minutes", minMs, maxMs)
            .commit();

        int stage = crop.getCrop()
            .maxSize() - 1;
        int durationFull = duration * stage;
        int minTickFull = minTick * stage;
        int maxTickFull = maxTick * stage;
        float minMsFull = minMs * stage;
        float maxMsFull = maxMs * stage;
        c.addAttr("Duration (Full)", durationFull)
            .commit();
        c.addAttrRange("Ticks", minTickFull, maxTickFull);
        c.addAttrRange("Minutes", minMsFull, maxMsFull)
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
