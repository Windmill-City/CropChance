package city.windmill.cropchance.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import ic2.api.crops.CropCard;
import ic2.core.crop.IC2Crops;

public class CropCardCommand extends BasicCommand {

    public CropCardCommand() {
        super("cropcard");
    }

    @Override
    public String getHelp() {
        return getCommandPrefix() + " [page]";
    }

    @Override
    public void processCommand(ICommandSender sender, List<String> args) {
        AdvChatComponent c = new AdvChatComponent(sender);

        int page = getPage(args);
        int size = IC2Crops.instance.getCrops()
            .size();
        page = MathHelper.clamp_int(page, 1, size);

        c.beginPage("CropCard", getCommandPrefix(), page, size);

        CropCard crop = new ArrayList<>(IC2Crops.instance.getCrops()).get(page - 1);
        formatCropCard(crop, c);

        c.endPage(getCommandPrefix(), page, size);
    }

    public static void formatCropCard(CropCard crop, AdvChatComponent c) {
        String name = I18n.format(crop.displayName());
        // Crop Name
        c.attr("Name", name);
        // Crop Id
        c.attrSameLine("Id", crop.name());
        c.attr("DiscoveredBy", crop.discoveredBy());

        // Mod Name
        ModContainer container = FMLCommonHandler.instance()
            .findContainerFor(crop.owner());
        c.attrSameLine("Owner", crop.owner());
        if (container != null) c.attr("Name", container.getName());
        else c.commit();

        // Attributes
        String attrs = String.join(", ", crop.attributes());
        c.attr("Attr", attrs);
    }
}
