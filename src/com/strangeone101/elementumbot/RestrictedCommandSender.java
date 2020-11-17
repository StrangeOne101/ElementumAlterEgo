package com.strangeone101.elementumbot;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.javacord.api.entity.message.Message;

import java.util.UUID;

public class RestrictedCommandSender extends FakeCommandSender {

    private User user;

    public RestrictedCommandSender(org.javacord.api.entity.user.User user, Message message, UUID uuid) {
        super(user, message);

        this.user = LuckPermsProvider.get().getUserManager().getUser(uuid);
    }

    @Override
    public boolean hasPermission(String perm) {
        return this.hasPermission(Bukkit.getServer().getPluginManager().getPermission(perm));
    }

    @Override
    public boolean hasPermission(Permission arg0) {
        for (Node node : this.user.getNodes()) {
            if (node.getKey().equalsIgnoreCase(arg0.getName()) && !node.hasExpired() && node.getContexts().isEmpty()) {
                return !node.isNegated();
            } else if (arg0.getDefault().getValue(false)) {
                return true;
            }
        }
        return false;
    }
}
