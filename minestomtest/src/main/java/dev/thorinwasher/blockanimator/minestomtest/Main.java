package dev.thorinwasher.blockanimator.minestomtest;

import dev.thorinwasher.blockanimator.Animation;
import dev.thorinwasher.blockanimator.CompiledAnimation;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveLinear;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveQuadraticBezier;
import dev.thorinwasher.blockanimator.minestom.Animator;
import dev.thorinwasher.blockanimator.selector.BlockSelector;
import dev.thorinwasher.blockanimator.selector.RandomSpherical;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.timer.BlockTimer;
import dev.thorinwasher.blockanimator.timer.LinearBlockTimer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

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
        Command command = new Command("animate");

        ArgumentWord motion = new ArgumentWord("motion").from("linear", "quadratic");
        ArgumentInteger size = new ArgumentInteger("size");
        ArgumentInteger time = new ArgumentInteger("time");
        command.addSyntax(((sender, context) -> {
            if (!(sender instanceof Player player)) {
                throw new IllegalArgumentException("Only players can execute this command!");
            }
            BlockMoveAnimation blockMoveAnimation = switch (context.get(motion)) {
                case "linear" -> new BlockMoveLinear(toVector3D(player.getPosition()), 0.2);
                case "quadratic" -> new BlockMoveQuadraticBezier(toVector3D(player.getPosition()), 0.2, () -> 10D);
                default -> throw new IllegalArgumentException("Unknown animator");
            };
            BlockSupplier<Block> blockSupplier = new TestSupplier(Block.DIAMOND_BLOCK, context.get(size), player.getPosition().add(20, 0, 0));
            BlockTimer blockTimer = new LinearBlockTimer(context.get(time));
            BlockSelector blockSelector = new RandomSpherical();
            CompiledAnimation<Block> compiledAnimation = new Animation<>(blockSelector, blockMoveAnimation, blockSupplier, blockTimer).compile();
            Animator animator = new Animator(compiledAnimation, player.getInstance());
            MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
        }), motion, size, time);
        commandManager.register(command);

        minecraftServer.start(System.getProperty("address", "0.0.0.0"), Integer.getInteger("port", 25565));
    }

    private static Vector3D toVector3D(Point point) {
        return new Vector3D(point.x(), point.y(), point.z());
    }
}