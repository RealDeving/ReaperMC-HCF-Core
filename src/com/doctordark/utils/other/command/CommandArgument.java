package com.doctordark.utils.other.command;

import org.apache.commons.lang3.*;
import org.bukkit.command.*;
import java.util.*;

public abstract class CommandArgument {
    private final String name;
    protected boolean isPlayerOnly;
    protected String description;
    protected String permission;
    protected String[] aliases;

    public CommandArgument(final String name, final String description) {
        this(name, description, (String) null);
    }

    public CommandArgument(final String name, final String description, final String permission) {
        this(name, description, permission, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public CommandArgument(final String name, final String description, final String[] aliases) {
        this(name, description, null, aliases);
    }

    public CommandArgument(final String name, final String description, final String permission, final String[] aliases) {
        this.isPlayerOnly = false;
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }

    public final String getName() {
        return this.name;
    }

    public boolean isPlayerOnly() {
        return this.isPlayerOnly;
    }

    public final String getDescription() {
        return this.description;
    }

    public final String getPermission() {
        return this.permission;
    }

    public final String[] getAliases() {
        if (this.aliases == null) {
            this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return Arrays.copyOf(this.aliases, this.aliases.length);
    }

    public abstract String getUsage(final String p0);

    public abstract boolean onCommand(final CommandSender p0, final Command p1, final String p2, final String[] p3);

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandArgument)) {
            return false;
        }
        final CommandArgument that = (CommandArgument) o;
        Label_0054: {
            if (this.name != null) {
                if (this.name.equals(that.name)) {
                    break Label_0054;
                }
            } else if (that.name == null) {
                break Label_0054;
            }
            return false;
        }
        Label_0087: {
            if (this.description != null) {
                if (this.description.equals(that.description)) {
                    break Label_0087;
                }
            } else if (that.description == null) {
                break Label_0087;
            }
            return false;
        }
        if (this.permission != null) {
            if (this.permission.equals(that.permission)) {
                return Arrays.equals(this.aliases, that.aliases);
            }
        } else if (that.permission == null) {
            return Arrays.equals(this.aliases, that.aliases);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = (this.name != null) ? this.name.hashCode() : 0;
        result = 31 * result + ((this.description != null) ? this.description.hashCode() : 0);
        result = 31 * result + ((this.permission != null) ? this.permission.hashCode() : 0);
        result = 31 * result + ((this.aliases != null) ? Arrays.hashCode(this.aliases) : 0);
        return result;
    }
}
