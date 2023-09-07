package city.windmill.cropchance.command;

import com.mojang.realmsclient.gui.ChatFormatting;

import city.windmill.cropchance.DummyWorld;
import ic2.api.crops.Crops;
import ic2.api.item.IC2Items;
import ic2.core.crop.IC2Crops;
import ic2.core.crop.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;

public class EvalChanceCommand extends SubCommand {

    public static boolean isRunning = false;

    public int Cross_2 = 0;
    public int Cross_4 = 0;
    public int Weed_2 = 0;
    public int Weed_4 = 0;

    public EvalChanceCommand() {
        super("eval_chance");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AdvChatComponent c = new AdvChatComponent(sender);

        int ticks;

        if (args.length == 1) {
            // Stop running task
            if (args[0].equals("stop")) {
                if (!isRunning) {
                    c.text(ChatFormatting.RESET, "Chance eval not running!").commit();
                    return;
                }
                isRunning = false;

            } else {
                // Parse tick count
                try {
                    ticks = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    c.text(ChatFormatting.RESET, "/crop eval_chance <ticks | stop>").commit();
                    return;
                }

                if (ticks < 0) {
                    c.text(ChatFormatting.RESET, "ticks could not be negative!");
                    return;
                }

                isRunning = true;
                // Begin Chance eval
                Thread t = new Thread(() -> {
                    DummyWorld world = new DummyWorld();
                    TileEntityCrop crop_2 = placeCropPair(world, 0, 0, 0);
                    TileEntityCrop crop_4 = placeCropQuad(world, 5, 0, 0);

                    int tick = 0;
                    while (isRunning && tick < ticks) {
                        resetAndUpdateChance(crop_2, crop_4);
                        tick++;
                        crop_2.tick();
                        crop_4.tick();
                    }
                    isRunning = false;

                    c.beginAttr("Eval Result");
                    // Ticks
                    c.attrSameLine("Total Tick", tick);
                    float seconds = tick / 20.f;
                    if (seconds > 60)
                        c.attr("Minute", seconds / 60);
                    else
                        c.attr("Second", seconds);
                    c.endAttr();

                    // Crop 2
                    c.attrSameLine("Crop", "Reed");
                    c.attr("Parent Count", 2);
                    c.attrSameLine("Cross", Cross_2);
                    c.attr("Chance", 100f * Cross_2 / tick);
                    c.attrSameLine("Weed", Weed_2);
                    c.attr("Chance", 100f * Weed_2 / tick);
                    c.endAttr();

                    // Crop 4
                    c.attrSameLine("Crop", "Reed");
                    c.attr("Parent Count", 4);
                    c.attrSameLine("Cross", Cross_4);
                    c.attr("Chance", 100f * Cross_4 / tick);
                    c.attrSameLine("Weed", Weed_4);
                    c.attr("Chance", 100f * Weed_4 / tick);
                    c.endAttr();
                });
                // make ic2.core.Platform#isSimulating return true
                t.setName("Server thread");
                t.setDaemon(true);
                t.start();
            }

        } else
            // no argument
            c.text(ChatFormatting.RESET, "/crop eval_chance <ticks | stop>").commit();
    }

    public TileEntityCrop placeCrop(World world, int x, int y, int z) {
        ItemBlock iCrop = (ItemBlock) IC2Items.getItem("crop").getItem();
        Block bCrop = iCrop.field_150939_a;

        world.setBlock(x, y, z, Blocks.farmland);
        world.setBlock(x, y + 1, z, bCrop);
        return (TileEntityCrop) world.getTileEntity(x, y + 1, z);
    }

    public TileEntityCrop setupParent(TileEntityCrop crop) {
        crop.setCrop(IC2Crops.cropReed);
        // Make canCross() return true
        crop.size = IC2Crops.cropReed.maxSize();
        return crop;
    }

    public boolean isWeed(TileEntityCrop crop) {
        return crop.getCrop() == Crops.weed;
    }

    public boolean isCross(TileEntityCrop crop) {
        return crop.getCrop() != null && !isWeed(crop);
    }

    public void resetCross(TileEntityCrop crop) {
        crop.reset();
        crop.upgraded = true;
    }

    public void resetAndUpdateChance(TileEntityCrop crop_2, TileEntityCrop crop_4) {
        if (isCross(crop_2))
            Cross_2++;
        if (isCross(crop_4))
            Cross_4++;
        if (isWeed(crop_2))
            Weed_2++;
        if (isWeed(crop_4))
            Weed_4++;

        resetCross(crop_2);
        resetCross(crop_4);
    }

    public TileEntityCrop placeCropPair(World world, int x, int y, int z) {
        // Left
        setupParent(placeCrop(world, x, y, z + 1));
        // Right
        setupParent(placeCrop(world, x, y, z - 1));
        // Center
        return placeCrop(world, x, y, z);
    }

    public TileEntityCrop placeCropQuad(World world, int x, int y, int z) {
        // Front
        setupParent(placeCrop(world, x + 1, y, z));
        // Back
        setupParent(placeCrop(world, x - 1, y, z));
        // Left
        setupParent(placeCrop(world, x, y, z + 1));
        // Right
        setupParent(placeCrop(world, x, y, z - 1));
        // Center
        return placeCrop(world, x, y, z);
    }
}
