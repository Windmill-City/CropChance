package city.windmill.cropchance.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;

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
        ChatBuilder c = new ChatBuilder(sender);
        c.addPage(
            "cropchance.cmd.cropcard.title",
            getCommandPrefix(),
            getPage(args),
            Arrays.asList(
                Crops.instance.getCrops()
                    .toArray()),
            crop -> formatCropCard(c, (CropCard) crop));
        c.build();
    }

    public static void formatCropCard(ChatBuilder c, CropCard crop) {
        String name = I18n.format(crop.displayName());
        // Crop Name
        c.addAttr("cropchance.ui.attr.name", name);
        c.addAttr("cropchance.ui.attr.tier", crop.tier())
            .commit();
        // Crop Id
        c.addAttr("cropchance.ui.attr.id", crop.name());
        c.addAttr("cropchance.ui.attr.discover-by", crop.discoveredBy())
            .commit();

        // Mod Name
        ModContainer container = FMLCommonHandler.instance()
            .findContainerFor(crop.owner());
        c.addAttr("cropchance.ui.attr.owner", crop.owner());
        if (container != null) c.addAttr("cropchance.ui.attr.name", container.getName());
        c.commit();

        // Attributes
        String attrs = String.join(", ", crop.attributes());
        c.addAttr("cropchance.ui.attr.attr", attrs)
            .commit();
    }

}
