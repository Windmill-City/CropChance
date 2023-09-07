package city.windmill.cropchance.command;

import java.util.Collections;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class AdvChatComponent {

    public final int TitleLength = 40;
    public final ICommandSender Sender;

    private ChatComponentText Text = new ChatComponentText("");

    public AdvChatComponent(ICommandSender sender) {
        this.Sender = sender;
    }

    static private String repeat(String toRepeat, int count) {
        return String.join("", Collections.nCopies(count, toRepeat));
    }

    static private IChatComponent ComponentCommand(String name, EnumChatFormatting color, String cmd, Object... args) {
        IChatComponent c = new ChatComponentText(name);
        c.setChatStyle(new ChatStyle()
                .setColor(color)
                .setUnderlined(true)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmd, args))));
        return c;
    }

    public AdvChatComponent commit() {
        Sender.addChatMessage(Text);
        Text = new ChatComponentText("");
        return this;
    }

    public AdvChatComponent text(ChatFormatting f, String text) {
        Text.appendText(f + text);
        return this;
    }

    public AdvChatComponent beginPage(String title, String cmd, int cur, int prev, int next, int max) {
        int lenTitle = title.length();
        int padding = TitleLength - lenTitle - 17;

        // Insert padding
        commit();
        text(ChatFormatting.GOLD, repeat("-", padding / 2) + " ");
        text(ChatFormatting.AQUA, title);
        text(ChatFormatting.RESET, String.format(" Page: %d/%d (", cur, max));
        Text.appendSibling(ComponentCommand("Prev", EnumChatFormatting.RESET, cmd, prev));
        text(ChatFormatting.RESET, "/");
        Text.appendSibling(ComponentCommand("Next", EnumChatFormatting.RESET, cmd, next));
        text(ChatFormatting.RESET, String.format(")", max));
        text(ChatFormatting.GOLD, " " + repeat("-", padding / 2));
        return commit();
    }

    public AdvChatComponent endPage(String cmd, int cur, int prev, int next, int max) {
        int padding = TitleLength - 17;

        text(ChatFormatting.GOLD, repeat("-", padding / 2) + " ");
        text(ChatFormatting.RESET, String.format("Page: %d/%d (", cur, max));
        Text.appendSibling(ComponentCommand("Prev", EnumChatFormatting.RESET, cmd, prev));
        text(ChatFormatting.RESET, "/");
        Text.appendSibling(ComponentCommand("Next", EnumChatFormatting.RESET, cmd, next));
        text(ChatFormatting.RESET, String.format(")", max));
        text(ChatFormatting.GOLD, " " + repeat("-", padding / 2));
        return commit();
    }

    public AdvChatComponent beginAttr(String title) {
        int lenTitle = title.length();
        int padding = TitleLength - lenTitle;

        // Insert padding
        commit();
        text(ChatFormatting.GOLD, repeat("-", padding / 2) + " ");
        text(ChatFormatting.AQUA, title);
        text(ChatFormatting.GOLD, " " + repeat("-", padding / 2));
        return commit();
    }

    public AdvChatComponent endAttr() {
        text(ChatFormatting.GOLD, repeat("-", TitleLength));
        return commit();
    }

    public AdvChatComponent attrSameLine(String name, String value) {
        text(ChatFormatting.RESET, " ");
        text(ChatFormatting.DARK_AQUA, name + ": ");
        text(ChatFormatting.YELLOW, value);
        return this;
    }

    public AdvChatComponent attrSameLine(String name, int value) {
        attrSameLine(name, String.format("%d", value));
        return this;
    }

    public AdvChatComponent attrSameLine(String name, float value) {
        attrSameLine(name, String.format("%.2f", value));
        return this;
    }

    public AdvChatComponent attrSameLine(String name, double value) {
        attrSameLine(name, String.format("%.2f", value));
        return this;
    }

    public AdvChatComponent attr(String name, String value) {
        text(ChatFormatting.RESET, " ");
        text(ChatFormatting.DARK_AQUA, name + ": ");
        text(ChatFormatting.YELLOW, value);
        return commit();
    }

    public AdvChatComponent attr(String name, int value) {
        return attr(name, String.format("%d", value));
    }

    public AdvChatComponent attr(String name, float value) {
        return attr(name, String.format("%.02f", value));
    }

    public AdvChatComponent attr(String name, double value) {
        return attr(name, String.format("%.02f", value));
    }
}
