package city.windmill.cropchance;

import city.windmill.cropchance.command.CropCommand;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CropCommand());
    }
}
