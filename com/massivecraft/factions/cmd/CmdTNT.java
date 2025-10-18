/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.tnt.CmdTNTDeposit;
import com.massivecraft.factions.cmd.tnt.CmdTNTFill;
import com.massivecraft.factions.cmd.tnt.CmdTNTInfo;
import com.massivecraft.factions.cmd.tnt.CmdTNTSiphon;
import com.massivecraft.factions.cmd.tnt.CmdTNTWithdraw;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdTNT
extends FCommand {
    private final CmdTNTInfo infoCmd;

    public CmdTNT() {
        this.aliases.add("tnt");
        this.aliases.add("trinitrotoluene");
        this.infoCmd = new CmdTNTInfo();
        this.addSubCommand(this.infoCmd);
        this.addSubCommand(new CmdTNTFill());
        this.addSubCommand(new CmdTNTDeposit());
        this.addSubCommand(new CmdTNTWithdraw());
        this.addSubCommand(new CmdTNTSiphon());
        this.requirements = new CommandRequirements.Builder(Permission.TNT_INFO).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        commandContext.commandChain.add(this);
        this.infoCmd.execute(commandContext);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_INFO_DESCRIPTION;
    }
}

