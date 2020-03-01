package com.focess.dropitem.util;

import com.focess.dropitem.DropItem;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Command extends org.bukkit.command.Command {

    private final List<Executor> executors = Lists.newArrayList();
    private boolean registered;

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
        command.registered = true;
        Command.commands.add(command);
    }

    public static void unregister(final Command command) {
        command.unregister();
    }

    public final void addExecutor(final int count, final CommandExecutor executor, final String... subCommands) {
        this.executors.add(new Executor(count, subCommands).addExecutor(executor));
    }

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

    @Override
    public final boolean execute(final CommandSender sender, final String cmd, final String[] args) {
        if (!this.registered)
            return true;
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(DropItemConfiguration.getMessage("HaveNoPermission"));
            return true;
        }
        final int amount = args.length;
        boolean flag = false;
        for (final Executor executer : this.executors)
            if (executer.checkCount(amount) && executer.checkArgs(args)) {
                executer.execute(sender, Arrays.copyOfRange(args, executer.getSubCommandsSize(), args.length));
                flag = true;
                break;
            }
        if (!flag)
            this.usage(sender);
        return true;
    }

    public final void unregister() {
        final SimpleCommandMap commandMap = (SimpleCommandMap) Command.commandMap;
        this.unregister(Command.commandMap);
        try {
            final Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            final Map<String, org.bukkit.command.Command> commands = (Map<String, org.bukkit.command.Command>) field.get(commandMap);
            commands.remove(this.getName());
            field.set(commandMap, commands);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        this.registered = false;
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

    private static class Executor {

        private final int count;
        private final String[] subCommands;
        private CommandExecutor executor;

        private Executor(final int count, final String... subCommands) {
            this.subCommands = subCommands;
            this.count = count;
        }

        public Executor addExecutor(final CommandExecutor executor) {
            this.executor = executor;
            return this;
        }

        public boolean checkArgs(final String[] args) {
            for (int i = 0; i < this.subCommands.length; i++)
                if (!this.subCommands[i].equals(args[i]))
                    return false;
            return true;
        }

        public boolean checkCount(final int amount) {
            return this.subCommands.length + this.count == amount;
        }

        public void execute(final CommandSender sender, final String[] args) {
            this.executor.execute(sender, args);
        }

        public int getSubCommandsSize() {
            return this.subCommands.length;
        }
    }

    public abstract void usage(CommandSender commandSender);
}
