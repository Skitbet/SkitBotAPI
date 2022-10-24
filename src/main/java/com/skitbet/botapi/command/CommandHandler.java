/*
 * @author skeet
 * Created At: 4/28/22, 9:29 AM
 * Project: Copper
 */

package com.skitbet.botapi.command;

import com.skitbet.botapi.SkitBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 This class is messy asf.. been reusing it since I made it (beginning of the year) planning on remaking it
 **/

public class CommandHandler extends ListenerAdapter {

    private final List<ICommand> regCommands = new ArrayList<>();
    private final CommandListUpdateAction commands;

    private final List<String> usersOnCooldown = new ArrayList<>();

    public CommandHandler(CommandListUpdateAction commands) {
        this.commands = commands;
    }

    public void addCommand(ICommand iCommand) {
        regCommands.add(iCommand);
    }

    public void registerCommands() {
        for (ICommand command : regCommands) {
            CommandDataImpl commandData = new CommandDataImpl(command.getName(), command.getDescription());
            if (command.getOptions() != null) {
                for (OptionData optionData : command.getOptions()) {
                    commandData.addOptions(optionData);
                }
            }

            if (command.getSubCommands() != null) {
                if (!command.getSubCommands().isEmpty()) {
                    for (SubcommandData subcommandData : command.getSubCommands()) {
                        commandData.addSubcommands(subcommandData);
                    }
                }
            }

            this.commands.addCommands(commandData);
            SkitBot.skitBotInstance.getJda().upsertCommand(commandData);
        }
        this.commands.queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() != null) {
            for (ICommand command : regCommands) {
                if (event.getName().equalsIgnoreCase(command.getName())) {
                    // Check cooldown
                    if (usersOnCooldown.contains(event.getUser().getId())) {
                        event.replyEmbeds(
                                new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setDescription("Please wait before running a command again!").build()
                        ).setEphemeral(true).queue();
                        return;
                    }
                    // Check permissions
                    if (command.getRequiredPermissions() != null) {
                        if (!command.getRequiredPermissions().isEmpty()) {
                            if (permCheck(command, event.getMember())) {
                                command.run(event);

                                runCooldown(event.getUser());

                                return;
                            }
                            event.replyEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Color.RED)
                                            .setDescription("You do not have the permission to run this command!").build()
                            ).setEphemeral(true).queue();
                            return;
                        }
                    }

                    // will run if there is no permissions required
                    command.run(event);

                    runCooldown(event.getUser());
                }
            }
        }
    }

    public void runCooldown(User user) {
        usersOnCooldown.add(user.getId());
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            usersOnCooldown.remove(user.getId());
        }, 5, TimeUnit.SECONDS);
    }

    public boolean permCheck(ICommand command, Member member) {
        List<Role> membersRoles = member.getRoles();

        for (Role role : membersRoles) {
            for (Permission permission : role.getPermissions()) {
                if (permission == Permission.ADMINISTRATOR) {
                    return true;
                }
                for (Permission required : command.getRequiredPermissions()) {
                    if (required == permission) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
