package city.windmill.cropchance.command;

import java.util.List;

import net.minecraft.command.ICommandSender;

import ic2.core.crop.TileEntityCrop;

public class TickCommand extends CropAction {

    public TickCommand() {
        super("tick");
    }

    @Override
    public String getHelp() {
        return getCommandPrefix() + " <tick count>";
    }

    public int getTick(List<String> args) {
        int tick;
        try {
            tick = getIntegerDefault(args, 1);
        } catch (NumberFormatException ignored) {
            throw new InvalidArgumentException(this, "tick", args.get(0), "Tick count, should be Integer");
        }
        return tick;
    }

    @Override
    public void doAction(TileEntityCrop crop, ICommandSender sender, List<String> args) {
        int tick_count = getTick(args);
        for (int i = 0; i < tick_count; i++) crop.tick();

        msg(sender, "Ticked on the target crop for %d times", tick_count);
    }

    @Override
    public boolean allowNullCrop() {
        return true;
    }
}
