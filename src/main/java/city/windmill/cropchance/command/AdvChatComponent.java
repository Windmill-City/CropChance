package city.windmill.cropchance.command;

import java.util.Collections;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import com.mojang.realmsclient.gui.ChatFormatting;

@SuppressWarnings("unused")
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
        c.setChatStyle(
            new ChatStyle().setColor(color)
                .setUnderlined(true)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmd, args))));
        return c;
    }

    public void commit() {
        Sender.addChatMessage(Text);
        Text = new ChatComponentText("");
    }

    public AdvChatComponent text(ChatFormatting f, String text) {
        Text.appendText(f + text);
        return this;
    }

    public AdvChatComponent text(String text) {
        return text(ChatFormatting.RESET, text);
    }

    public void beginPage(String title, String cmd, int cur, int max) {
        cmd = cmd + " %d";
        int lenTitle = title.length();
        int padding = TitleLength - lenTitle - 17;

        // Insert padding
        commit();
        text(ChatFormatting.GOLD, repeat("-", padding / 2) + " ");
        text(ChatFormatting.AQUA, title);
        text(ChatFormatting.RESET, String.format(" Page: %d/%d (", cur, max));
        Text.appendSibling(ComponentCommand("Prev", EnumChatFormatting.RESET, cmd, cur - 1));
        text(ChatFormatting.RESET, "/");
        Text.appendSibling(ComponentCommand("Next", EnumChatFormatting.RESET, cmd, cur + 1));
        text(ChatFormatting.RESET, ")");
        text(ChatFormatting.GOLD, " " + repeat("-", padding / 2));
        commit();
    }

    public void endPage(String cmd, int cur, int max) {
        cmd = cmd + " %d";
        int padding = TitleLength - 17;

        text(ChatFormatting.GOLD, repeat("-", padding / 2) + " ");
        text(ChatFormatting.RESET, String.format("Page: %d/%d (", cur, max));
        Text.appendSibling(ComponentCommand("Prev", EnumChatFormatting.RESET, cmd, cur - 1));
        text(ChatFormatting.RESET, "/");
        Text.appendSibling(ComponentCommand("Next", EnumChatFormatting.RESET, cmd, cur + 1));
        text(ChatFormatting.RESET, ")");
        text(ChatFormatting.GOLD, " " + repeat("-", padding / 2));
        commit();
    }

    public void beginAttr(String title) {
        int lenTitle = title.length();
        int padding = TitleLength - lenTitle;

        // Insert padding
        commit();
        text(ChatFormatting.GOLD, repeat("-", padding / 2) + " ");
        text(ChatFormatting.AQUA, title);
        text(ChatFormatting.GOLD, " " + repeat("-", padding / 2));
        commit();
    }

    public void endAttr() {
        text(ChatFormatting.GOLD, repeat("-", TitleLength) + " ");
        commit();
    }

    public void attrSameLine(String name, String value) {
        text(ChatFormatting.RESET, " ");
        text(ChatFormatting.DARK_AQUA, name + ": ");
        text(ChatFormatting.YELLOW, value);
    }

    public void attrSameLine(String name, int value) {
        attrSameLine(name, String.format("%d", value));
    }

    public void attrSameLine(String name, float value) {
        attrSameLine(name, String.format("%.2f", value));
    }

    public void attrSameLine(String name, double value) {
        attrSameLine(name, String.format("%.2f", value));
    }

    public void attr(String name, String value) {
        text(ChatFormatting.RESET, " ");
        text(ChatFormatting.DARK_AQUA, name + ": ");
        text(ChatFormatting.YELLOW, value);
        commit();
    }

    public void attr(String name, int value) {
        attr(name, String.format("%d", value));
    }

    public void attr(String name, float value) {
        attr(name, String.format("%.02f", value));
    }

    public void attr(String name, double value) {
        attr(name, String.format("%.02f", value));
    }
}
