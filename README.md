# SkitBotAPI 

SkitBotAPI is a API to make Discord JDA easier and lets you create bots faster, while keeping the code as JDA vanilla as possible. This API is simple and small, I plan to add more things to improve development for JDA developers. 

## Installation

The installation for SkitBotAPI is very simple and user-friendly.

To install you can either download the source and compile it or you can download the latest release and import it using Maven or Gradle!

To begin using the SkitBotAPI we first need to make a new bot which is very easy!

```
public class ExampleBot extends SkitBot {

    public AntiBitch() {
        super("token");
    }

    // onEnable will run once the bot has fully started.
    @Override
    protected void onEnable() {
        this.getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.competing("an endurance test."));
    }

    // You can either one by one add commands into the commands list or use the reflections API which I prefer.
    @Override
    public List<ICommand> getCommands() {
        List<ICommand> commands = new ArrayList<>();
      
        for (Class<?> clazz : new Reflections(getClass().getPackage().getName() + ".commands").getSubTypesOf(ICommand.class)) {
            try {
                ICommand command = (ICommand) clazz.newInstance();
                commands.add(command);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return commands;
    }
    
    @Override
    public List<ListenerAdapter> getEvents() {
        List<ListenerAdapter> listenerAdapters = new ArrayList<>();
        listenerAdapters.add(new RandomEvent());
        return listenerAdapters;
    }


    // start the java program
    public static void main(String[] args) {
        new AntiBitch();
    }
}
```


To make a new command all you have to is create a new class and extend it off of ICommand.class

Here is an example /clear command.
```
public class Clear extends ICommand {
    @Override
    public void run(SlashCommandInteractionEvent event) {
        int amount = event.getOption("amount").getAsInt();

        event.getChannel().getIterableHistory()
                .takeAsync(amount)
                .thenAccept(messages -> {

                    event.getChannel().purgeMessages(messages);
                    event.getChannel().sendMessageEmbeds(
                            new EmbedBuilder().setTitle("✅ I have successfully deleted " + messages.size() + " message(s)!").setColor(Color.GREEN).build()
                    ).queue();

                });

    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Bulk delete a certain amount of messages.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> optionData = new ArrayList<>();
        optionData.add(new OptionData(OptionType.INTEGER, "amount", "Amount of messages to delete.", true));
        return optionData;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.MESSAGE_MANAGE);
        return permissions;
    }
```
Make sure to register the command in your main class.
