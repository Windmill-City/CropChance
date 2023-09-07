package city.windmill.cropchance.command;

import net.minecraft.command.ICommandSender;

public abstract class SubCommand {
    public final String Name;

    SubCommand(String name) {
        this.Name = name;
    }

    public abstract void processCommand(ICommandSender sender, String[] args);
}