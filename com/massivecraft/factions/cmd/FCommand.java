/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;

public abstract class FCommand {
    public final FactionsPlugin plugin = FactionsPlugin.getInstance();
    public final List<String> aliases;
    public final List<String> requiredArgs;
    public final LinkedHashMap<String, String> optionalArgs;
    public CommandRequirements requirements = new CommandRequirements.Builder(null).build();
    public final List<FCommand> subCommands = new ArrayList<FCommand>();
    public final List<String> helpLong;
    public final CommandVisibility visibility;
    private String helpShort = null;

    public FCommand() {
        this.aliases = new ArrayList<String>();
        this.requiredArgs = new ArrayList<String>();
        this.optionalArgs = new LinkedHashMap();
        this.helpLong = new ArrayList<String>();
        this.visibility = CommandVisibility.VISIBLE;
    }

    public abstract void perform(CommandContext var1);

    public void execute(CommandContext commandContext) {
        if (!commandContext.args.isEmpty()) {
            for (FCommand fCommand : this.subCommands) {
                if (!fCommand.aliases.contains(((String)commandContext.args.getFirst()).toLowerCase())) continue;
                commandContext.args.removeFirst();
                commandContext.commandChain.add(this);
                fCommand.execute(commandContext);
                return;
            }
        }
        if (!this.validCall(commandContext)) {
            return;
        }
        if (!this.isEnabled(commandContext)) {
            return;
        }
        this.perform(commandContext);
    }

    public boolean validCall(CommandContext commandContext) {
        return this.requirements.computeRequirements(commandContext, true) && this.validArgs(commandContext);
    }

    public boolean isEnabled(CommandContext commandContext) {
        if (FactionsPlugin.getInstance().getLocked() && this.requirements.isDisableOnLock()) {
            commandContext.msg("<b>Factions was locked by an admin. Please try again later.", new Object[0]);
            return false;
        }
        return true;
    }

    public boolean validArgs(CommandContext commandContext) {
        if (commandContext.args.size() < this.requiredArgs.size()) {
            if (commandContext.sender != null) {
                commandContext.msg(TL.GENERIC_ARGS_TOOFEW, new Object[0]);
                commandContext.sender.sendMessage(this.getUsageTemplate(commandContext));
            }
            return false;
        }
        if (commandContext.args.size() > this.requiredArgs.size() + this.optionalArgs.size() && this.requirements.isErrorOnManyArgs()) {
            if (commandContext.sender != null) {
                List<String> list = commandContext.args.subList(this.requiredArgs.size() + this.optionalArgs.size(), commandContext.args.size());
                commandContext.msg(TL.GENERIC_ARGS_TOOMANY, TextUtil.implode(list, " "));
                commandContext.sender.sendMessage(this.getUsageTemplate(commandContext));
            }
            return false;
        }
        return true;
    }

    public void addSubCommand(FCommand fCommand) {
        this.subCommands.add(fCommand);
    }

    public void setHelpShort(String string) {
        this.helpShort = string;
    }

    public String getHelpShort() {
        if (this.helpShort == null) {
            TL tL = this.getUsageTranslation();
            return tL == null ? "" : tL.toString();
        }
        return this.helpShort;
    }

    public TL getUsageTranslation() {
        return null;
    }

    public List<String> getToolTips(FPlayer fPlayer) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : FactionsPlugin.getInstance().conf().commands().toolTips().getPlayer()) {
            arrayList.add(ChatColor.translateAlternateColorCodes((char)'&', (String)Tag.parsePlain(fPlayer, string)));
        }
        return arrayList;
    }

    public List<String> getToolTips(Faction faction) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : FactionsPlugin.getInstance().conf().commands().toolTips().getFaction()) {
            arrayList.add(ChatColor.translateAlternateColorCodes((char)'&', (String)Tag.parsePlain(faction, string)));
        }
        return arrayList;
    }

    public String getUsageTemplate(CommandContext commandContext, boolean bl) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FactionsPlugin.getInstance().txt().parseTags("<c>"));
        stringBuilder.append('/');
        for (FCommand iterator : commandContext.commandChain) {
            stringBuilder.append(TextUtil.implode(iterator.aliases, ","));
            stringBuilder.append(' ');
        }
        stringBuilder.append(TextUtil.implode(this.aliases, ","));
        ArrayList arrayList = new ArrayList();
        for (String string : this.requiredArgs) {
            arrayList.add("<" + string + ">");
        }
        for (Map.Entry<String, String> entry : this.optionalArgs.entrySet()) {
            Object object = entry.getValue();
            object = object == null ? "" : "=" + (String)object;
            arrayList.add("[" + entry.getKey() + (String)object + "]");
        }
        if (!arrayList.isEmpty()) {
            stringBuilder.append(FactionsPlugin.getInstance().txt().parseTags("<p> "));
            stringBuilder.append(TextUtil.implode(arrayList, " "));
        }
        if (bl) {
            stringBuilder.append(FactionsPlugin.getInstance().txt().parseTags(" <i>"));
            stringBuilder.append(this.getHelpShort());
        }
        return stringBuilder.toString();
    }

    public String getUsageTemplate(CommandContext commandContext) {
        return this.getUsageTemplate(commandContext, false);
    }

    public static enum CommandVisibility {
        VISIBLE,
        SECRET,
        INVISIBLE;

    }
}

