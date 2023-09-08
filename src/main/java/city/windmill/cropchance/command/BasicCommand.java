package city.windmill.cropchance.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BasicCommand extends CommandBase {
    @Nonnull
    public final String Name;
    @Nullable
    public BasicCommand Parent;

    public final List<BasicCommand> Child = new ArrayList<>();

    protected BasicCommand(@Nonnull String name) {
        Name = name;
        Parent = null;
    }

    public void addChild(BasicCommand c) {
        Child.add(c);
        c.Parent = this;
    }

    /**
     * @return Simple command usage description
     */
    public String getHelp() {
        return getCommandPrefix();
    }


    public List<String> addTabCompletionOptions(ICommandSender sender, List<String> args) {
        if (args.isEmpty())
            return Child.stream().map(it -> it.Name).collect(Collectors.toList());
        else if (args.size() == 1)
            return Child.stream().map(it -> it.Name).filter(it -> it.startsWith(args.get(0))).collect(Collectors.toList());
        else {
            Optional<BasicCommand> sub = Child.stream().filter(it -> it.Name.equals(args.get(0))).findFirst();
            if (sub.isPresent())
                return sub.get().addTabCompletionOptions(sender, args.stream().skip(1).collect(Collectors.toList()));
            else
                return Collections.emptyList();
        }
    }

    public void processCommand(ICommandSender sender, List<String> args) {
        Optional<BasicCommand> c = Child.stream().filter(it -> it.Name.equals(args.get(0))).findFirst();
        if (c.isPresent()) {
            try {
                c.get().processCommand(sender, args.stream().skip(1).collect(Collectors.toList()));
            } catch (Exception e) {
                msgEx(sender, e);
            }
        } else
            getCommandUsage(sender);
    }

    public final String getCommandPrefix() {
        LinkedList<String> parents = new LinkedList<>();
        BasicCommand c = this;
        while (c.Parent != null) {
            parents.addFirst(c.Name);
            c = c.Parent;
        }
        return String.format("/%s", String.join(" ", parents));
    }

    @Override
    public final String getCommandName() {
        return Name;
    }

    @Override
    public final String getCommandUsage(ICommandSender sender) {
        //No subcommands
        if (Child.isEmpty())
            return getHelp();
        else
            return String.format("%s <%s>", getCommandPrefix(),
                Child.stream()
                    .map(it -> it.Name)
                    .collect(Collectors.joining(" | ")));
    }

    /*
      This method should only call from the first parent
     */
    @Override
    public final void processCommand(ICommandSender sender, String[] args) {
        processCommand(sender, Arrays.asList(args));
    }

    /*
      This method should only call from the first parent
     */
    @Override
    public final List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return addTabCompletionOptions(sender, Arrays.asList(args));
    }

    public int getPage(List<String> args) {
        int page = 1;
        if (args.size() == 1) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {
                throw new InvalidArgumentException(this, "page", args.get(0), "Page number, should be Integer");
            }
        }
        return page;
    }

    public static void msg(ICommandSender sender, String msg, Object... args) {
        sender.addChatMessage(new ChatComponentText(String.format(msg, args)));
    }

    public static void msgEx(ICommandSender sender, Exception e) {
        AdvChatComponent c = new AdvChatComponent(sender);
        if (e instanceof InvalidArgumentException) {
            c.beginAttr("Invalid Argument: %s");
            c.attr("Name", ((InvalidArgumentException) e).ArgName);
            c.attr("Invalid Value", ((InvalidArgumentException) e).InvalidVal);
            if (((InvalidArgumentException) e).Desc != null)
                c.attr("Desc", ((InvalidArgumentException) e).Desc);
            c.endAttr();

            //Show help
            c.text(((InvalidArgumentException) e).Command.getHelp()).commit();
        } else {
            c.beginAttr("Error");
            c.attr("Exception", e.getClass().getTypeName());
            c.attr("Message", e.getMessage());
            c.endAttr();
        }
    }

    public static class InvalidArgumentException extends IllegalArgumentException {
        public final BasicCommand Command;
        public final String ArgName;
        public final String InvalidVal;
        public final String Desc;

        InvalidArgumentException(BasicCommand c, String argName, String invalidVal, String desc) {
            super(String.format("Argument: %s has invalid value: %s", argName, invalidVal));
            Command = c;
            ArgName = argName;
            InvalidVal = invalidVal;
            Desc = desc;
        }

        InvalidArgumentException(BasicCommand c, String argName, String invalidVal) {
            this(c, argName, invalidVal, null);
        }
    }
}
