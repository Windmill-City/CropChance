package city.windmill.cropchance.command;

import java.util.List;

import net.minecraft.command.ICommandSender;

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
        c.addAttr("Scan Level", crop.getScanLevel())
            .commit();

        c.addTitle("Storage");
        c.addAttr("Water", crop.waterStorage)
            .commit();
        c.addAttr("Nutrient", crop.getNutrientStorage())
            .commit();
        c.addAttr("WeedEx", crop.getWeedExStorage())
            .commit();

        c.addTitle("Stat");
        c.addAttr("Growth", crop.getGrowth())
            .commit();
        c.addAttr("Gain", crop.getGain())
            .commit();
        c.addAttr("Resistance", crop.getResistance())
            .commit();
        c.addAttr("Size", crop.getSize())
            .commit();

        c.addTitle("Env");
        c.addAttr("Humidity", crop.getHumidity())
            .commit();
        c.addAttr("Nutrient", crop.getNutrients())
            .commit();
        c.addAttr("AirQuality", crop.getAirQuality())
            .commit();

        c.addTitle("Requirements");
        c.addAttr("Have", getHave(crop))
            .commit();
        c.addAttr("Need", getNeed(crop))
            .commit();
        c.addAttr("GrowRate", String.format("[%d, %d]", getGrowRateMin(crop), getGrowRateMax(crop)))
            .commit();
        c.addSeparator();
        c.build();
    }

    public static int getGrowRateMin(TileEntityCrop crop) {
        int base = 3 + crop.getGrowth();
        return getGrowRateInternal(crop, base);
    }

    public static int getGrowRateMax(TileEntityCrop crop) {
        int base = 3 + crop.getGrowth() + 6;
        return getGrowRateInternal(crop, base);
    }

    private static int getGrowRateInternal(TileEntityCrop crop, int base) {
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
