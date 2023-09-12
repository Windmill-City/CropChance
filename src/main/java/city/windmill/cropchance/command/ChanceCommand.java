package city.windmill.cropchance.command;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import city.windmill.cropchance.CropChance;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.crop.TileEntityCrop;

public class ChanceCommand extends CropAction {

    public final Map<CropCard, Integer> Result = new HashMap<>();
    public int Cross = 0;
    public int Weed = 0;

    public ChanceCommand() {
        super("chance");
    }

    @Override
    public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
        try {
            int tryCross = getIntegerDefault(args, 1);

            msg(sender, "Chance test begin...");

            World world = crop.getWorld();
            TileEntityCrop left = getTile(world, crop.xCoord + 1, crop.yCoord, crop.zCoord);
            TileEntityCrop right = getTile(world, crop.xCoord - 1, crop.yCoord, crop.zCoord);
            TileEntityCrop front = getTile(world, crop.xCoord, crop.yCoord, crop.zCoord + 1);
            TileEntityCrop back = getTile(world, crop.xCoord, crop.yCoord, crop.zCoord - 1);

            replaceParent(left);
            replaceParent(right);
            replaceParent(front);
            replaceParent(back);

            for (int i = 0; i < tryCross; i++) {
                resetForCross(crop);
                applyParent(left);
                applyParent(right);
                applyParent(front);
                applyParent(back);

                crop.tick();

                if (isWeed(crop)) Weed++;
                if (isCross(crop)) {
                    int count = Result.getOrDefault(crop.getCrop(), 0);
                    Result.put(crop.getCrop(), ++count);
                    Cross++;
                }
            }

            resetForCross(crop);
            restoreParent(left);
            restoreParent(right);
            restoreParent(front);
            restoreParent(back);

            msg(sender, "Chance test end!");

            ChatBuilder c = new ChatBuilder(sender);
            c.addTitle("Chances");

            for (Map.Entry<CropCard, Integer> entry : Result.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .collect(Collectors.toList())) {
                // Crop
                CropCard card = entry.getKey();
                c.addAttr("Name", I18n.format(card.displayName()));
                c.addAttr("Id", card.name());
                c.addAttr("Tier", card.tier())
                    .commit();

                // Chance
                Integer appear = entry.getValue();
                c.addAttr("Chance", appear)
                    .text(" (%.2f %%)", 100f * appear / Cross)
                    .commit();
                c.addSeparator();
            }

            // General
            c.addAttr("Tried", tryCross)
                .commit();
            c.addAttr("Weed", Weed)
                .text(" (%.2f %%)", 100f * Weed / tryCross)
                .commit();
            c.addAttr("Cross", Cross)
                .text(" (%.2f %%)", 100f * Cross / tryCross)
                .commit();
            c.addSeparator();
            c.build();
        } catch (IllegalAccessException e) {
            CropChance.LOG.error("Exception thrown during cross chance:", e);
            msgEx(sender, e);
        }
    }

    @Override
    public boolean checkCrop(ICommandSender sender, TileEntityCrop crop) {
        if (!crop.upgraded) {
            msg(sender, "Not a hybrid frame!");
            return false;
        }
        return true;
    }

    public void replaceParent(TileEntityCrop crop) throws IllegalAccessException {
        if (crop != null) {
            World world = crop.getWorld();
            world.setTileEntity(crop.xCoord, crop.yCoord, crop.zCoord, new TileEntityCrop());
        }
    }

    public void restoreParent(TileEntityCrop crop) {
        if (crop != null) {
            World world = crop.getWorld();
            world.setTileEntity(crop.xCoord, crop.yCoord, crop.zCoord, crop);
        }
    }

    public void applyParent(TileEntityCrop parent) throws IllegalAccessException {
        if (parent != null) {
            World world = parent.getWorld();
            TileEntityCrop to = (TileEntityCrop) world.getTileEntity(parent.xCoord, parent.yCoord, parent.zCoord);

            for (Field field : parent.getClass()
                .getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) continue;
                field.setAccessible(true);
                field.set(to, field.get(parent));
            }
        }
    }

    public void resetForCross(TileEntityCrop crop) {
        crop.reset();
        crop.upgraded = true;
    }

    public boolean isWeed(TileEntityCrop crop) {
        return crop.getCrop() == Crops.weed;
    }

    public boolean isCross(TileEntityCrop crop) {
        return crop.getCrop() != null && !isWeed(crop);
    }

    public TileEntityCrop getTile(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCrop) return (TileEntityCrop) te;
        else return null;
    }
}
