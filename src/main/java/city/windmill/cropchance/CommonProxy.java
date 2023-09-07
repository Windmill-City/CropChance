package city.windmill.cropchance;

import city.windmill.cropchance.command.BasicCommand;
import city.windmill.cropchance.command.DumpCropCard;
import city.windmill.cropchance.command.EvalChanceCommand;
import city.windmill.cropchance.command.InfoCommand;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        CropChance.LOG.info(Config.greeting);
        CropChance.LOG.info("I am " + Tags.MODNAME + " at version " + Tags.VERSION);
    }

    // load "Do your mod setup. Build whatever data structures you care about.
    // Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
    }

    // postInit "Handle interaction with other mods, complete your setup based on
    // this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        BasicCommand basic = new BasicCommand();

        basic.register(new InfoCommand());
        basic.register(new DumpCropCard());
        basic.register(new EvalChanceCommand());

        event.registerServerCommand(basic);
    }
}
