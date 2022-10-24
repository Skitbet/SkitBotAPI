/*
 * @author skeet
 * Created At: 4/28/22, 9:31 AM
 * Project: Copper
 */

package com.skitbet.botapi.command;

import com.skitbet.botapi.SkitBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public abstract class ICommand {

    protected SkitBot skitBot = SkitBot.skitBotInstance;
    public abstract void run(SlashCommandInteractionEvent event);

    public abstract String getName();
    public abstract String getDescription();

    public List<OptionData> getOptions() {
        return null;
    }
    public List<SubcommandData> getSubCommands() {
        return null;
    }

    public List<Permission> getRequiredPermissions() {
        return null;
    }

}
