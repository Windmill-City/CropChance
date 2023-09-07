package city.windmill.cropchance.command;

import ic2.core.crop.TileEntityCrop;
import net.minecraft.command.ICommandSender;

public class InfoCommand extends SubCommand {

    public InfoCommand() {
        super("info");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AdvChatComponent c = new AdvChatComponent(sender);
        c.beginAttr("Crop Info");
        c.attrSameLine("TickRate", TileEntityCrop.tickRate);
        c.attr("in second", TileEntityCrop.tickRate / 20f);
        c.endAttr();
    }

}
