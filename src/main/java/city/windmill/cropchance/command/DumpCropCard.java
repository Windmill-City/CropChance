package city.windmill.cropchance.command;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;

import java.util.stream.Collectors;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.FMLCommonHandler;
import ic2.api.crops.CropCard;
import ic2.core.crop.IC2Crops;

public class DumpCropCard extends SubCommand {

    public DumpCropCard() {
        super("dump_cropcard");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AdvChatComponent c = new AdvChatComponent(sender);

        int page = 1;

        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                c.text(ChatFormatting.RESET, "/crop dump_cropcard <page>").commit();
                return;
            }
        }

        int size = IC2Crops.instance.getCrops().size();
        page = MathHelper.clamp_int(page, 1, size);

        c.beginPage("CropCard", "/crop dump_cropcard %d", page, page - 1, page + 1, size);

        CropCard crop = IC2Crops.instance.getCrops().stream().collect(Collectors.toList()).get(page - 1);
        String name = I18n.format(crop.displayName());
        // Crop Name
        c.attr("Name", name);
        // Crop Id
        c.attrSameLine("Id", crop.name());
        c.attr("DiscoveredBy", crop.discoveredBy());

        // Mod Name
        c.attrSameLine("Owner", crop.owner())
                .attr("Name",
                        FMLCommonHandler.instance().findContainerFor(crop.owner()).getName());

        // Attributes
        String attrs = String.join(", ", crop.attributes());
        c.attr("Attr", attrs);

        c.endPage("/crop dump_cropcard %d", page, page - 1, page + 1, size);
    }

}
