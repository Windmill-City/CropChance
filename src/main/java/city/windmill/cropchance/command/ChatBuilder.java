package city.windmill.cropchance.command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;

import com.mojang.realmsclient.gui.ChatFormatting;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class ChatBuilder {

    public final int LineMax = 20;
    public final int Padding_X = 40;
    public final ICommandSender Sender;

    public final LinkedList<IChatComponent> Lines = new LinkedList<>();
    public final LinkedList<IChatComponent> Line = new LinkedList<>();

    /**
     * Char count of active line
     */
    public int CharNum = 0;

    public ChatBuilder(ICommandSender sender) {
        this.Sender = sender;
    }

    /**
     * Push lines to Sender
     */
    public void build() {
        // Y Paddings
        IChatComponent c = new ChatComponentText("");
        for (int i = Lines.size(); i < LineMax; i++) {
            Sender.addChatMessage(c);
        }

        for (IChatComponent l : Lines) Sender.addChatMessage(l);
        Lines.clear();
    }

    /**
     * Push new line to Lines
     */
    public void commit() {
        commit(false);
    }

    /**
     * Push new line to Lines
     */
    public void commit(boolean atFront) {
        IChatComponent c = new ChatComponentText("");
        for (IChatComponent ic : Line) {
            c.appendSibling(ic);
        }
        if (atFront) Lines.addFirst(c);
        else Lines.addLast(c);
        Line.clear();
        CharNum = 0;
    }

    public ChatBuilder text(String text, Object... args) {
        return text(ChatFormatting.WHITE, text, args);
    }

    public ChatBuilder text(ChatFormatting f, String text, Object... args) {
        text = String.format(text, args);
        Line.addLast(new ChatComponentText(f + text));
        CharNum += text.length();
        return this;
    }

    public ChatBuilder textFront(String text, Object args) {
        return textFront(ChatFormatting.WHITE, text, args);
    }

    public ChatBuilder textFront(ChatFormatting f, String text, Object... args) {
        text = String.format(text, args);
        Line.addFirst(new ChatComponentText(f + String.format(text, args)));
        CharNum += text.length();
        return this;
    }

    public <T> void addPage(String title, String cmd, int page, List<T> contents, Consumer<T> formatter) {
        Lines.clear();
        int maxPage = 0;

        if (!contents.isEmpty()) {
            formatter.accept(contents.get(0));

            int linesPerItem = Lines.size() + 1;// Separator
            int itemsPerPage = (LineMax - 1) / linesPerItem;// Extract PageHead
            Lines.clear();

            maxPage = (int) Math.ceil((float) contents.size() / itemsPerPage);
            page = MathHelper.clamp_int(page, 1, maxPage);

            int iStart = (page - 1) * itemsPerPage;
            int iEnd = Math.min(page * itemsPerPage, contents.size());
            for (int i = iStart; i < iEnd; i++) {
                formatter.accept(contents.get(i));
                // Not last line
                if (i != iEnd - 1) addSeparator();
            }
        }

        addPageHead(title, cmd, page, maxPage);
        addPageTail(cmd, page, maxPage);
    }

    public void addPageHead(String title, String cmd, int curPage, int maxPage) {
        text(ChatFormatting.AQUA, title + " ");
        addPageNav(cmd, curPage, maxPage);
        addPadding();
        commit(true);
    }

    public void addPageTail(String cmd, int curPage, int maxPage) {
        addPageNav(cmd, curPage, maxPage);
        addPadding();
        commit();
    }

    public void addPageNav(String cmd, int cur, int max) {
        cmd = cmd + " %d";

        text(ChatFormatting.WHITE, "Page: %d/%d (", cur, max);
        addCommand("Prev", EnumChatFormatting.GREEN, cmd, cur - 1);
        text(ChatFormatting.WHITE, "/");
        addCommand("Next", EnumChatFormatting.GREEN, cmd, cur + 1);
        text(ChatFormatting.WHITE, ")");
    }

    public void addTitle(String title) {
        int lenTitle = title.length();
        int padding = Padding_X - lenTitle;

        text(ChatFormatting.AQUA, title);
        addPadding();
        commit();
    }

    public void addSeparator() {
        textFront(ChatFormatting.GOLD, repeat("-", Padding_X) + " ");
        commit();
    }

    public void addPadding() {
        int padding = Padding_X - CharNum;
        textFront(ChatFormatting.GOLD, repeat("-", padding / 2) + " ");
        padding = Padding_X - CharNum;
        text(ChatFormatting.GOLD, " " + repeat("-", padding));
    }

    public ChatBuilder addAttr(String name, String value, Object... args) {
        text(ChatFormatting.RESET, " ");
        text(ChatFormatting.DARK_AQUA, name + ": ");
        text(ChatFormatting.YELLOW, value, args);
        return this;
    }

    public ChatBuilder addAttr(String name, long value) {
        return addAttr(name, "%d", value);
    }

    public ChatBuilder addAttr(String name, double value) {
        return addAttr(name, "%.02f", value);
    }

    public ChatBuilder addAttr(String name, boolean val) {
        return addAttr(name, val ? "true" : "false");
    }

    public ChatBuilder addAttrRange(String name, long min, long max) {
        return addAttr(name, "[%d, %d] (%d)", min, max, (min + max) / 2);
    }

    public ChatBuilder addAttrRange(String name, double min, double max) {
        return addAttr(name, "[%.2f, %.2f] (%.2f)", min, max, (min + max) / 2);
    }

    public void addCommand(String name, EnumChatFormatting color, String cmd, Object... args) {
        IChatComponent c = new ChatComponentText(name);
        c.setChatStyle(
            new ChatStyle().setColor(color)
                .setUnderlined(true)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmd, args))));
        Line.addLast(c);
        CharNum += name.length();
    }

    public static String repeat(String toRepeat, int count) {
        return String.join("", Collections.nCopies(count, toRepeat));
    }
}
