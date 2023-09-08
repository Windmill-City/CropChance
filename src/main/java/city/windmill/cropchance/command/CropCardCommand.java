package city.windmill.cropchance.command;

import java.util.ArrayList;
import java.util.stream.Collectors;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.FMLCommonHandler;
import ic2.api.crops.CropCard;
import ic2.core.crop.IC2Crops;

public class CropCardCommand extends SubCommand {

    public CropCardCommand() {
        super("cropcard");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AdvChatComponent c = new AdvChatComponent(sender);

        int page = 1;

        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                c.text(ChatFormatting.RESET, "/crop cropcard <page>")
                    .commit();
                return;
            }
        }

        int size = IC2Crops.instance.getCrops()
            .size();
        page = MathHelper.clamp_int(page, 1, size);

        c.beginPage("CropCard", "/crop cropcard %d", page, page - 1, page + 1, size);

        CropCard crop = new ArrayList<>(IC2Crops.instance.getCrops())
            .get(page - 1);
        formatCropCard(crop, c);

        c.endPage("/crop cropcard %d", page, page - 1, page + 1, size);
    }

    public static void formatCropCard(CropCard crop, AdvChatComponent c) {
        String name = I18n.format(crop.displayName());
        // Crop Name
        c.attr("Name", name);
        // Crop Id
        c.attrSameLine("Id", crop.name());
        c.attr("DiscoveredBy", crop.discoveredBy());

        // Mod Name
        c.attrSameLine("Owner", crop.owner());
        c.attr(
            "Name",
            FMLCommonHandler.instance()
                .findContainerFor(crop.owner())
                .getName());

        // Attributes
        String attrs = String.join(", ", crop.attributes());
        c.attr("Attr", attrs);
    }

}
