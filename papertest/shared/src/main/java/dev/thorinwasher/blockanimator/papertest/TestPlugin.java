package dev.thorinwasher.blockanimator.papertest;

import dev.thorinwasher.blockanimator.papertest.command.AnimateCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("animate").setExecutor(new AnimateCommand(this));
    }
}
