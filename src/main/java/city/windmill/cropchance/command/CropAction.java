package city.windmill.cropchance.command;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;

import ic2.core.crop.TileEntityCrop;

public abstract class CropAction extends BasicCommand {

    protected CropAction(@Nonnull String name) {
        super(name);
    }

    @Override
    public void processCommand(ICommandSender sender, List<String> args) {
        MovingObjectPosition objHit = Minecraft.getMinecraft().objectMouseOver;
        if (objHit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            TileEntity e = sender.getEntityWorld()
                .getTileEntity(objHit.blockX, objHit.blockY, objHit.blockZ);
            if (e instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) e;
                if (checkCrop(sender, crop)) doAction(crop, sender, args);
                return;
            }
        }
        msg(sender, "You need to point to a CropBlock!");
    }

    public abstract void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args);

    public boolean checkCrop(ICommandSender sender, TileEntityCrop crop) {
        if (crop.getCrop() == null) {
            msg(sender, "No crop on it!");
            return false;
        }
        return true;
    }
}
