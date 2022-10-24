/*
 * @author skeet
 * Created At: 8/28/22, 9:29 AM
 * Project: Copper
 */

package com.skitbet.botapi.message;

import com.skitbet.botapi.SkitBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class IMessage extends ListenerAdapter {

    /**
     * This class is used for advance messages like the /queue message!
     */

    protected final TextChannel textChannel;
    protected final Member member;

    public IMessage(TextChannel textChannel, Member member) {
        this.member = member;
        this.textChannel = textChannel;

        SkitBot.skitBotInstance.getJda().addEventListener(this);
    }

    public abstract void sendMessage();

    public List<Button> getButtons() {
        return null;
    }

    public abstract void handleButtons(ButtonInteractionEvent event);

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        handleButtons(event);
    }
}