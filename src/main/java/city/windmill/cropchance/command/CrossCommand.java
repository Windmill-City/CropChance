package city.windmill.cropchance.command;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;

import com.mojang.realmsclient.gui.ChatFormatting;

import city.windmill.cropchance.DummyWorld;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.item.IC2Items;
import ic2.core.crop.IC2Crops;
import ic2.core.crop.TileEntityCrop;

public class CrossCommand extends SubCommand {

    public CrossCommand() {
        super("cross");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AdvChatComponent c = new AdvChatComponent(sender);

        if (args.length == 3) {
            try {
                int tryCross = (int) Float.parseFloat(args[0]);
                int growth = Integer.parseInt(args[1]);
                int surround = Integer.parseInt(args[2]);

                new CrossTry(IC2Crops.cropReed, growth, surround, tryCross).runCross(sender);
            } catch (Exception e) {
                c.text(
                    ChatFormatting.RESET,
                    e.getClass()
                        .getTypeName() + e.getMessage())
                    .commit();
            }
        } else printHelp(c);
    }

    public static void printHelp(AdvChatComponent c) {
        c.text(ChatFormatting.RESET, "/crop cross <try> <growth> <surround>")
            .commit();
    }

    public static class CrossTry {

        CropCard Parent;
        int Growth;
        int Surround;
        int TryCross;

        int Crossed = 0;
        int Weeded = 0;

        CrossTry(CropCard parent, int growth, int surround, int tryCross) {
            Parent = parent;
            Growth = growth;
            Surround = surround;
            TryCross = tryCross;
        }

        public TileEntityCrop buildTryEnv() {
            DummyWorld world = new DummyWorld();

            switch (Surround) {
                case 2:
                    return placeCropPair(world, 0, 0, 0);
                case 3:
                    return placeCropTri(world, 0, 0, 0);
                case 4:
                    return placeCropQuad(world, 0, 0, 0);
                default:
                    throw new IllegalArgumentException("Surround valid values are: 2, 3, 4.");
            }
        }

        public void runCross(ICommandSender sender) {
            Thread t = new Thread(() -> {
                try {
                    TileEntityCrop c = buildTryEnv();

                    int tried = 0;
                    while (tried < TryCross) {
                        resetCrop(c);
                        c.tick();
                        if (isWeed(c)) Weeded++;
                        if (isCross(c)) Crossed++;
                        tried++;
                    }

                    formatResult(sender);
                } catch (Exception e) {
                    new AdvChatComponent(sender).text(ChatFormatting.RESET, e.getMessage())
                        .commit();
                }
            });
            // Make IC2's isSimulating return true
            t.setName("Server thread");
            t.setDaemon(true);
            t.start();
            try {
                t.join();
            } catch (Exception ignored) {}
        }

        public void formatResult(ICommandSender sender) {
            AdvChatComponent c = new AdvChatComponent(sender);

            c.beginAttr("Result");
            c.attr("Growth", Growth);
            c.attr("Surround", Surround);
            c.endAttr();

            c.attr("Try", TryCross);
            c.attrSameLine("Weed", Weeded);
            c.attr("Percent %", 100f * Weeded / TryCross);
            c.attrSameLine("Cross", Crossed);
            c.attr("Percent %", 100f * Crossed / TryCross);
            c.endAttr();
        }

        public TileEntityCrop placeCrop(World world, int x, int y, int z) {
            ItemBlock iCrop = (ItemBlock) IC2Items.getItem("crop")
                .getItem();
            assert iCrop != null;
            Block bCrop = iCrop.field_150939_a;

            world.setBlock(x, y, z, Blocks.farmland);
            world.setBlock(x, y + 1, z, bCrop);
            return (TileEntityCrop) world.getTileEntity(x, y + 1, z);
        }

        public void setupParent(TileEntityCrop crop) {
            crop.setCrop(IC2Crops.cropReed);
            // Make canCross() return true
            crop.size = IC2Crops.cropReed.maxSize();
            crop.statGrowth = Growth;
        }

        public TileEntityCrop placeCropPair(World world, int x, int y, int z) {
            // Left
            setupParent(placeCrop(world, x, y, z + 1));
            // Right
            setupParent(placeCrop(world, x, y, z - 1));
            // Center
            return placeCrop(world, x, y, z);
        }

        public TileEntityCrop placeCropTri(World world, int x, int y, int z) {
            // Front
            setupParent(placeCrop(world, x + 1, y, z));
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

        public boolean isWeed(TileEntityCrop crop) {
            return crop.getCrop() == Crops.weed;
        }

        public boolean isCross(TileEntityCrop crop) {
            return crop.getCrop() != null && !isWeed(crop);
        }

        public void resetCrop(TileEntityCrop crop) {
            crop.reset();
            crop.upgraded = true;
        }
    }
}
