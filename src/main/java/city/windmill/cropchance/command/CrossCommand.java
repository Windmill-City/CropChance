package city.windmill.cropchance.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ChunkCoordinates;
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

                if (args.size() >= 4 && (args.get(3)
                    .equals("quiet")
                    || args.get(3)
                        .equals("q")))
                    trier.Quiet = true;
                if (args.size() >= 4 && args.get(3)
                    .equals("nodummy")) trier.Dummy = false;

                trier.runCross(sender);
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException(this, "try/growth/surround", String.join(", ", args));
            }
        } else msg(sender, getCommandUsage(sender));
    }

    @Override
    public String getHelp() {
        return getCommandPrefix() + " <<try> <growth> <surround> [quiet | q] [nodummy]>> | <stop>";
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
        boolean Dummy = true;
        boolean Running = false;

        CrossTry(CropCard parent, int growth, int surround, long tryCross) {
            Parent = parent;
            Growth = growth;
            Surround = surround;
            TryCross = tryCross;
        }

        public List<TileEntityCrop> buildTryEnv(World world, ChunkCoordinates at) {
            if (at.posY < 1) at = new ChunkCoordinates(at.posX, 1, at.posZ);

            // Water
            world.setBlock(at.posX - 1, at.posY - 1, at.posZ - 1, Blocks.water);
            switch (Surround) {
                case 2:
                    return placeCropPair(world, at.posX, at.posY - 1, at.posZ);
                case 3:
                    return placeCropTri(world, at.posX, at.posY - 1, at.posZ);
                case 4:
                    return placeCropQuad(world, at.posX, at.posY - 1, at.posZ);
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

                    World world = Dummy ? new DummyWorld() : sender.getEntityWorld();
                    ChunkCoordinates at = sender.getPlayerCoordinates();

                    List<TileEntityCrop> crops = buildTryEnv(world, at);
                    TileEntityCrop center = crops.get(0);
                    CropChance.LOG.info("Built up cross environment");

                    while (Tried < TryCross && Running) {
                        resetCrops(crops);
                        center.tick();
                        if (isWeed(center)) Weeded++;
                        if (isCross(center)) Crossed++;
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
            crop.reset();
            crop.setCrop(IC2Crops.cropReed);
            // Make canCross() return true
            crop.size = IC2Crops.cropReed.maxSize();
            crop.statGrowth = Growth;
        }

        public List<TileEntityCrop> placeCropPair(World world, int x, int y, int z) {
            // Clean
            world.setBlockToAir(x + 1, y + 1, z);
            world.setBlockToAir(x - 1, y + 1, z);

            List<TileEntityCrop> te = new ArrayList<>(3);
            // Center
            te.add(placeCrop(world, x, y, z));
            // Left
            te.add(placeCrop(world, x, y, z + 1));
            // Right
            te.add(placeCrop(world, x, y, z - 1));
            return te;
        }

        public List<TileEntityCrop> placeCropTri(World world, int x, int y, int z) {
            // Clean
            world.setBlockToAir(x - 1, y + 1, z);

            List<TileEntityCrop> te = new ArrayList<>(4);
            // Center
            te.add(placeCrop(world, x, y, z));
            // Front
            te.add(placeCrop(world, x + 1, y, z));
            // Left
            te.add(placeCrop(world, x, y, z + 1));
            // Right
            te.add(placeCrop(world, x, y, z - 1));
            return te;
        }

        public List<TileEntityCrop> placeCropQuad(World world, int x, int y, int z) {
            List<TileEntityCrop> te = new ArrayList<>(5);
            // Center
            te.add(placeCrop(world, x, y, z));
            // Front
            te.add(placeCrop(world, x + 1, y, z));
            // Back
            te.add(placeCrop(world, x - 1, y, z));
            // Left
            te.add(placeCrop(world, x, y, z + 1));
            // Right
            te.add(placeCrop(world, x, y, z - 1));
            return te;
        }

        public boolean isWeed(TileEntityCrop crop) {
            return crop.getCrop() == Crops.weed;
        }

        public boolean isCross(TileEntityCrop crop) {
            return crop.getCrop() != null && !isWeed(crop);
        }

        public void resetCrops(List<TileEntityCrop> crops) {
            // Reset Center
            crops.get(0)
                .reset();
            crops.get(0).upgraded = true;
            // Reset Parent
            for (int i = 1; i < crops.size(); i++) setupParent(crops.get(i));
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
