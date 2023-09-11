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

    public CrossTry trier = null;

    public CrossCommand() {
        super("cross");
    }

    @Override
    public void processCommand(ICommandSender sender, List<String> args) {
        if (args.size() == 1 && args.get(0)
            .equals("stop") && trier != null) {
            trier.stop();
        }

        if (args.size() >= 3) {
            try {
                long tryCross = (long) Float.parseFloat(args.get(0));
                int growth = Integer.parseInt(args.get(1));
                int surround = Integer.parseInt(args.get(2));

                trier = new CrossTry(IC2Crops.cropReed, growth, surround, tryCross);

                if (args.size() == 4 && (args.get(3)
                    .equals("quiet")
                    || args.get(3)
                        .equals("q")))
                    trier.Quiet = true;

                trier.runCross(sender);
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException(this, "try/growth/surround", String.join(", ", args));
            }
        } else msg(sender, getCommandUsage(sender));
    }

    @Override
    public String getHelp() {
        return getCommandPrefix() + " <<try> <growth> <surround> [quiet | q]>> | <stop>";
    }

    public static class CrossTry {

        CropCard Parent;
        long TryCross;
        int Growth;
        int Surround;

        long Tried = 0;
        int Crossed = 0;
        int Weeded = 0;

        boolean Quiet = false;
        boolean Running = false;

        CrossTry(CropCard parent, int growth, int surround, long tryCross) {
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
                    Running = true;
                    CropChance.LOG.info("Cross simulate start with param: " + this);
                    msg(sender, "Cross simulate start...");

                    TileEntityCrop c = buildTryEnv();
                    CropChance.LOG.info("Built up cross environment");

                    while (Tried < TryCross && Running) {
                        resetCrop(c);
                        c.tick();
                        if (isWeed(c)) Weeded++;
                        if (isCross(c)) Crossed++;
                        Tried++;

                        // Report progress if Try is large
                        if (!Quiet && TryCross > 1e6 && (Tried % 1E6) == 0) {
                            msg(sender, "Progress: %d %%", 100L * Tried / TryCross);
                        }
                    }

                    Running = false;
                    CropChance.LOG.info("Cross simulate end!");

                    formatResult(sender);
                } catch (Exception e) {
                    CropChance.LOG.error("Exception thrown during crossing", e);
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

            c.addAttr("Try", Tried)
                .commit();

            c.addAttr("Weed", Weeded);
            c.addAttr("Percent", 100f * Weeded / TryCross)
                .text("%%")
                .commit();

            c.addAttr("Cross", Crossed);
            c.addAttr("Percent", 100f * Crossed / TryCross)
                .text("%%")
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

        public void stop() {
            if (Running) {
                Running = false;
                CropChance.LOG.info("Running simulation has stopped!");
            } else CropChance.LOG.info("No running simulation!");
        }

        @Override
        public String toString() {
            return "CrossTry{" + "Parent="
                + Parent.getClass()
                + ", TryCross="
                + TryCross
                + ", Growth="
                + Growth
                + ", Surround="
                + Surround
                + '}';
        }
    }
}
