package dev.thorinwasher.blockanimator.testplugin;

import dev.thorinwasher.blockanimator.testplugin.command.AnimateCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("animate").setExecutor(new AnimateCommand(this));
    }
}
