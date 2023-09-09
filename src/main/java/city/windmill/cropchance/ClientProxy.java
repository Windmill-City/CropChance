package city.windmill.cropchance;

import net.minecraftforge.client.ClientCommandHandler;

import city.windmill.cropchance.command.CropCommand;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CropCommand());
    }
}
