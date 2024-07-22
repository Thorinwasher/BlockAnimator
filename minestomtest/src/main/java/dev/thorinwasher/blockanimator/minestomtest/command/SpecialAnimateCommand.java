package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.animation.Animation;
import dev.thorinwasher.blockanimator.animation.GrowingStructureAnimation;
import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.EaseOutCubicPathCompletionSupplier;
import dev.thorinwasher.blockanimator.minestom.Animator;
import dev.thorinwasher.blockanimator.minestom.VectorConversion;
import dev.thorinwasher.blockanimator.minestomtest.TestSupplier;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;

public class SpecialAnimateCommand extends Command {
    public SpecialAnimateCommand() {
        super("specialanimate");

        ArgumentWord specialType = ArgumentType.Word("animation-type").from("growing");
        ArgumentInteger generateWidth = ArgumentType.Integer("size");

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                throw new IllegalArgumentException("Only players can execute this command!");
            }
            Pos corner = player.getPosition().add(20, 0, 0);
            BlockSupplier<Block> blockSupplier = new TestSupplier(Block.DIAMOND_BLOCK, context.get(generateWidth), corner);

            Animation<Block> animation = switch (context.get(specialType)) {
                case "growing" ->
                        new GrowingStructureAnimation<>(blockSupplier, VectorConversion.toVector3D(new BlockVec(corner)),
                                new EaseOutCubicPathCompletionSupplier(0.05), 100);
                default -> throw new IllegalArgumentException();
            };
            Thread thread = new Thread(animation::compile);
            thread.start();
            Animator animator = new Animator(animation, player.getInstance());
            MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
        }, specialType, generateWidth);
    }
}
