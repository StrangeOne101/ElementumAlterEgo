package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.util.StringUtil;
import org.javacord.api.entity.permission.Role;

public class QOTDCommand extends CommandRunnable {
    public QOTDCommand() {
        super("qotd");
    }

    @Override
    public void runCommand(Command command) {
        if (!command.hasOppedPower()) return;

        if (command.getArguments().length < 2) {
            command.reply("Error: Command usage is `!qotd <question>`!");
            return;
        }

        if (AlterEgoPlugin.QOTDHook == null) {
            command.reply("QOTD hook not found.");
            return;
        }

        try {
            AlterEgoPlugin.QOTDHook.updateAvatar(command.getSender().getAvatar()).get();
            AlterEgoPlugin.QOTDHook.updateName(command.getSender().getDisplayName()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Role role = AlterEgoPlugin.QOTDHook.getServer().get().getRolesByName("QOTD").get(0);

        final String message = StringUtil.combine(command.getArguments());

        AlterEgoPlugin.QOTDHook.asIncomingWebhook().get().sendMessage(role.getMentionTag() + "\n❓ " + message);

        if (command.getOriginal().canYouDelete() &&
                AlterEgoPlugin.QOTDHook.getChannelId() == command.getOriginal().getChannel().getId()) {
            command.getOriginal().delete();
        }

    }


}
