package city.windmill.cropchance.command;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;

import city.windmill.cropchance.CropChance;
import city.windmill.cropchance.DummyWorld;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.item.IC2Items;
import ic2.core.crop.IC2Crops;
import ic2.core.crop.TileEntityCrop;

public class CrossCommand extends BasicCommand {

    public CrossCommand() {
        super("cross");
    }

    @Override
    public void processCommand(ICommandSender sender, List<String> args) {
        if (args.size() == 3) {
            try {
                int tryCross = (int) Float.parseFloat(args.get(0));
                int growth = Integer.parseInt(args.get(1));
                int surround = Integer.parseInt(args.get(2));

                new CrossTry(IC2Crops.cropReed, growth, surround, tryCross).runCross(sender);
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException(this, "try/growth/surround", String.join(", ", args));
            }
        } else msg(sender, getCommandUsage(sender));
    }

    @Override
    public String getHelp() {
        return getCommandPrefix() + " <try> <growth> <surround>";
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
                } catch (IllegalArgumentException e) {
                    msg(sender, e.getMessage());
                } catch (Exception e) {
                    CropChance.LOG.error("Exception throw during crossing", e);
                    msgEx(sender, e);
                }
            });
            // Make IC2's isSimulating return true
            t.setName("Server thread");
            t.setDaemon(true);
            t.start();
        }

        public void formatResult(ICommandSender sender) {
            ChatBuilder c = new ChatBuilder(sender);

            c.addTitle("Result");
            c.addAttr("Growth", Growth)
                .commit();
            c.addAttr("Surround", Surround)
                .commit();
            c.addSeparator();

            c.addAttr("Try", TryCross)
                .commit();

            c.addAttr("Weed", Weeded);
            c.addAttr("Percent", 100f * Weeded / TryCross)
                .text("%")
                .commit();

            c.addAttr("Cross", Crossed);
            c.addAttr("Percent", 100f * Crossed / TryCross)
                .text("%")
                .commit();
            c.addSeparator();
            c.build();
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
