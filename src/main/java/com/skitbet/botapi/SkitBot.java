package com.skitbet.botapi;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skitbet.botapi.command.CommandHandler;
import com.skitbet.botapi.command.ICommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * Main class to extend to create a new discord bot.
 */
public abstract class SkitBot {

    private String token;
    private Collection<GatewayIntent> intents;

    // JDA
    private JDA jda;

    private CommandHandler commandHandler;

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();
    public static SkitBot skitBotInstance;

    /**
     * Sets the token and some other params to prepare the bot.
     * @param token
     */
    public SkitBot(String token) {
        this.token = token;
        this.intents = new ArrayList<>();

        this.skitBotInstance = this;
        try {
            this.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the token and some other params to prepare the bot.
     * @param token
     * @param intents
     */
    public SkitBot(String token, Collection<GatewayIntent> intents) {
        this.token = token;
        this.intents = intents;

        this.skitBotInstance = this;
        try {
            this.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all commands that should be registered
     */
    public abstract List<ICommand> getCommands();

    /**
     * Get all events that bot should be listening for.
     */
    public abstract List<ListenerAdapter> getEvents();

    /**
     * Main method to start the bot.
     * @throws Exception
     */
    private void run() throws Exception {
        JDABuilder builder = JDABuilder.createDefault(token, (intents.isEmpty() ? EnumSet.allOf(GatewayIntent.class) : intents))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(EnumSet.allOf(CacheFlag.class))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("Loading..."))
                .setRawEventsEnabled(true);

        this.jda = builder.build();

        // Setup command handler
        this.commandHandler = new CommandHandler(this.jda.updateCommands());
        for (ICommand command : getCommands()) {
            this.commandHandler.addCommand(command);
        }
        this.commandHandler.registerCommands();
        this.jda.addEventListener(this.commandHandler);

        // Register events
        for (ListenerAdapter adapter : getEvents()) {
            this.jda.addEventListener(adapter);
        }

        this.onEnable();
    }

    protected abstract void onEnable();

    public JDA getJda() {
        return jda;
    }
}
