package com.focess.dropitem.util;

import com.focess.dropitem.DropItem;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Command extends org.bukkit.command.Command {

    private static class Executer {

        private final int count;
        private CommandExecutor executer;
        private final String[] subcommands;

        private Executer(final int count, final String... subcommands) {
            this.subcommands = subcommands;
            this.count = count;
        }

        public Executer addExecuter(final CommandExecutor executer) {
            this.executer = executer;
            return this;
        }

        public boolean checkArgs(final String[] args) {
            for (int i = 0; i < this.subcommands.length; i++)
                if (!this.subcommands[i].equals(args[i]))
                    return false;
            return true;
        }

        public boolean checkCount(final int amount) {
            return this.subcommands.length + this.count == amount;
        }

        public void execute(final CommandSender sender, final String[] args) {
            this.executer.execute(sender, args);
        }

        public int getSubCommandsSize() {
            return this.subcommands.length;
        }
    }

    private static CommandMap commandMap;

    private static final List<Command> commands = Lists.newArrayList();

    static {
        try {
            Command.getCommandMap();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void getCommandMap() throws Exception {
        final Class<?> c = Bukkit.getServer().getClass();
        Command.commandMap = (CommandMap) c.getDeclaredMethod("getCommandMap", new Class[0]).invoke(Bukkit.getServer(),
                new Object[0]);
    }

    public static void register(final Command command) {
        Command.commands.add(command);
    }

    public static void unregister(final Command command) {
        command.unregister();
    }

    private final List<Executer> executers = Lists.newArrayList();

    public Command(final String name, final List<String> ali) {
        this(name, ali, null);
    }

    public Command(final String name, final List<String> ali, final String permission) {
        super(name, "", "", ali);
        if (permission != null)
            this.setPermission(permission);
        this.init();
        Command.commandMap.register(DropItem.getInstance().getName(), this);
    }

    public final void addExecutor(final int count, final CommandExecutor executor, final String... subcommands) {
        this.executers.add(new Executer(count, subcommands).addExecuter(executor));
    }

    @Override
    public final boolean execute(final CommandSender sender, final String cmd, final String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(DropItem.getMessage("HaveNoPermission"));
            return true;
        }
        final int amount = args.length;
        boolean flag = false;
        for (final Executer executer : this.executers)
            if (executer.checkCount(amount) && executer.checkArgs(args)) {
                executer.execute(sender, Arrays.copyOfRange(args, executer.getSubCommandsSize(), args.length));
                flag = true;
                break;
            }
        if (!flag)
            this.usage(sender);
        return true;
    }

    protected abstract List<String> getCompleteLists(CommandSender sender, String cmd, String[] args);

    public abstract void init();

    @Override
    public final List<String> tabComplete(final CommandSender sender, final String cmd, final String[] args) {
        final List<String> ret = this.getCompleteLists(sender, cmd, args);
        if (args == null || args.length == 0)
            return ret;
        else
            return ret.parallelStream().filter(str -> str.startsWith(args[args.length - 1]))
                    .collect(Collectors.toList());
    }

    public final void unregister() {
        this.unregister(Command.commandMap);
    }

    public abstract void usage(CommandSender commandSender);
}
