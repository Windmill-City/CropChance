package city.windmill.cropchance.command;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;

import ic2.core.crop.TileEntityCrop;

public class SetCommand extends BasicCommand {

    public SetCommand() {
        super("set");
        addChild(new SetGrowth());
        addChild(new SetGain());
        addChild(new SetResistance());
        addChild(new SetNutrient());
        addChild(new SetWater());
        addChild(new SetWeedEx());
        addChild(new SetSize());
        addChild(new SetScanLevel());
    }

    public static class SetGrowth extends CropAction {

        public SetGrowth() {
            super("growth");
        }

        @Override
        public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
            crop.setGrowth((byte) getIntegerDefault(args, 0));
            crop.updateState();
            msg(
                sender,
                "Crop: %s Growth set to: %d",
                I18n.format(
                    crop.getCrop()
                        .displayName()),
                crop.getGrowth());
        }
    }

    public static class SetGain extends CropAction {

        public SetGain() {
            super("gain");
        }

        @Override
        public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
            crop.setGain((byte) getIntegerDefault(args, 0));
            crop.updateState();
            msg(
                sender,
                "Crop: %s Gain set to: %d",
                I18n.format(
                    crop.getCrop()
                        .displayName()),
                crop.getGain());
        }
    }

    public static class SetResistance extends CropAction {

        public SetResistance() {
            super("resistance");
        }

        @Override
        public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
            crop.setResistance((byte) getIntegerDefault(args, 0));
            crop.updateState();
            msg(
                sender,
                "Crop: %s Resistance set to: %d",
                I18n.format(
                    crop.getCrop()
                        .displayName()),
                crop.getResistance());
        }
    }

    public static class SetNutrient extends CropAction {

        public SetNutrient() {
            super("nutrient");
        }

        @Override
        public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
            crop.setNutrientStorage(getIntegerDefault(args, 0));
            crop.updateState();
            msg(
                sender,
                "Crop: %s Nutrient set to: %d",
                I18n.format(
                    crop.getCrop()
                        .displayName()),
                crop.getNutrientStorage());
        }
    }

    public static class SetWater extends CropAction {

        public SetWater() {
            super("water");
        }

        @Override
        public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
            crop.waterStorage = getIntegerDefault(args, 0);
            crop.updateState();
            msg(
                sender,
                "Crop: %s Water set to: %d",
                I18n.format(
                    crop.getCrop()
                        .displayName()),
                crop.waterStorage);
        }
    }

    public static class SetWeedEx extends CropAction {

        public SetWeedEx() {
            super("weedex");
        }

        @Override
        public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
            crop.setWeedExStorage(getIntegerDefault(args, 0));
            crop.updateState();
            msg(
                sender,
                "Crop: %s WeedEx set to: %d",
                I18n.format(
                    crop.getCrop()
                        .displayName()),
                crop.getWeedExStorage());
        }
    }

    public static class SetSize extends CropAction {

        public SetSize() {
            super("size");
        }

        @Override
        public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
            crop.setSize(
                (byte) MathHelper.clamp_int(
                    getIntegerDefault(args, 1),
                    1,
                    crop.getCrop()
                        .maxSize()));
            crop.updateState();
            msg(
                sender,
                "Crop: %s Size set to: %d(%d)",
                I18n.format(
                    crop.getCrop()
                        .displayName()),
                crop.getSize(),
                crop.getCrop()
                    .maxSize());
        }
    }

    public static class SetScanLevel extends CropAction {

        public SetScanLevel() {
            super("scan");
        }

        @Override
        public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
            crop.setScanLevel((byte) Math.max(getIntegerDefault(args, 1), 1));
            crop.updateState();
            msg(
                sender,
                "Crop: %s ScanLevel set to: %d(%d)",
                I18n.format(
                    crop.getCrop()
                        .displayName()),
                crop.getScanLevel());
        }
    }
}
