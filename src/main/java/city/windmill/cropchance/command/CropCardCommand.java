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
            "CropCard",
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
        c.addAttr("Name", name)
            .commit();
        // Crop Id
        c.addAttr("Id", crop.name());
        c.addAttr("DiscoveredBy", crop.discoveredBy())
            .commit();

        // Mod Name
        ModContainer container = FMLCommonHandler.instance()
            .findContainerFor(crop.owner());
        c.addAttr("Owner", crop.owner());
        if (container != null) c.addAttr("Name", container.getName());
        c.commit();

        // Attributes
        String attrs = String.join(", ", crop.attributes());
        c.addAttr("Attr", attrs)
            .commit();

        // MaxSize
        c.addAttr("MaxSize", crop.maxSize())
            .commit();
    }

}
