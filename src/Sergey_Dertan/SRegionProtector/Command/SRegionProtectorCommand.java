package Sergey_Dertan.SRegionProtector.Command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.Map;

public abstract class SRegionProtectorCommand extends Command {

    private Map<String, String> messages;
    public static final String DEFAULT_MESSAGE = "Unknown message";

    public SRegionProtectorCommand(String name, Map<String, String> messages) {
        super(name);
        this.messages = messages;
        if (messages.containsKey("usage")) this.usageMessage = messages.get("usage");
        this.setPermissionMessage(messages.get("permission"));
    }

    protected final void sendMessage(CommandSender sender, String msg, String[] search, String[] replace) {
        String message = this.messages.getOrDefault(msg, DEFAULT_MESSAGE);
        if (search.length > 0 && search.length == replace.length) {
            for (int i = 0; i < search.length; ++i) {
                message = message.replace(search[i], replace[i]);
            }
        }
        sender.sendMessage(message);
    }

    protected final void sendMessage(CommandSender sender, String msg, String search, String replace) {
        this.sendMessage(sender, msg, new String[]{search}, new String[]{replace});
    }

    protected final void sendMessage(CommandSender sender, String msg) {
        this.sendMessage(sender, msg, new String[]{}, new String[]{});
    }
}
