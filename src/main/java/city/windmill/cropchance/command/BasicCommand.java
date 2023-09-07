package city.windmill.cropchance.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BasicCommand extends CommandBase {

    private List<SubCommand> Commands = new ArrayList<>();

    @Override
    public String getCommandName() {
        return "crop";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        String subs = String.join(" | ", Commands.stream().map(c -> c.Name).collect(Collectors.toList()));
        return String.format("/crop <%s>", subs);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 0)
            return Commands.stream().map(s -> s.Name).collect(Collectors.toList());
        else
            return Commands.stream().map(s -> s.Name).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        String subName = args[0];
        for (SubCommand s : Commands) {
            if (s.Name.equals(subName)) {
                if (args.length > 1)
                    s.processCommand(sender, (String[]) Arrays.copyOfRange(args, 1, args.length));
                else
                    s.processCommand(sender, new String[0]);
                return;
            }
        }
    }

    public void register(SubCommand command) {
        Commands.add(command);
    }

}
