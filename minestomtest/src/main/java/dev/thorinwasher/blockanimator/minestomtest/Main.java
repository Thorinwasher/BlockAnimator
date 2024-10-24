package dev.thorinwasher.blockanimator.minestomtest;

import dev.thorinwasher.blockanimator.minestomtest.command.AnimateCommand;
import dev.thorinwasher.blockanimator.minestomtest.command.HologramCommand;
import dev.thorinwasher.blockanimator.minestomtest.command.LoadSchemCommand;
import dev.thorinwasher.blockanimator.minestomtest.command.SequentialAnimateCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import org.joml.Vector3d;

public class Main {

    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        InstanceManager manager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = manager.createInstanceContainer();

        instanceContainer.setGenerator(new FlatWorldGenerator());
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setTime(0);
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, (event) -> {
            event.setSpawningInstance(instanceContainer);
            event.getPlayer().setGameMode(GameMode.CREATIVE);
        });
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new SequentialAnimateCommand());
        commandManager.register(new AnimateCommand());
        commandManager.register(new LoadSchemCommand());
        commandManager.register(new HologramCommand());

        minecraftServer.start(System.getProperty("address", "0.0.0.0"), Integer.getInteger("port", 25565));
    }

    public static Vector3d toVector3d(Point point) {
        return new Vector3d(point.x(), point.y(), point.z());
    }
}