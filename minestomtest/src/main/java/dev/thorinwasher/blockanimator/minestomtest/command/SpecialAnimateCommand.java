package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.animation.Animation;
import dev.thorinwasher.blockanimator.animation.GrowingStructureAnimation;
import dev.thorinwasher.blockanimator.animator.Animator;
import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.EaseOutCubicPathCompletionSupplier;
import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.minestom.PlaceBlocksAfterBlockAnimator;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;
import java.util.Random;

public class SpecialAnimateCommand extends Command {

    private static final Random RANDOM = new Random();

    public SpecialAnimateCommand() {
        super("specialanimate");

        ArgumentWord specialType = ArgumentType.Word("animation-type").from("growing");
        ArgumentInteger generateWidth = ArgumentType.Integer("size");

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                throw new IllegalArgumentException("Only players can execute this command!");
            }
            BlockSupplier<Block> blockSupplier = BlockSupplierUtil.getBlockSupplier(player, context.get(generateWidth));
            List<Vector3D> positions = blockSupplier.getPositions();
            Vector3D startingBlock = positions.get(RANDOM.nextInt(positions.size()));
            PathCompletionSupplier pathCompletionSupplier = new EaseOutCubicPathCompletionSupplier(0.05);
            Animation<Block> animation = switch (context.get(specialType)) {
                case "growing" -> new GrowingStructureAnimation<>(blockSupplier, startingBlock,
                        pathCompletionSupplier, 100);
                default -> throw new IllegalArgumentException();
            };
            Thread thread = new Thread(animation::compile);
            thread.start();
            Animator<Block> animator = new Animator<>(animation, new PlaceBlocksAfterBlockAnimator(1000, player.getInstance()));
            Task timer = MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
            animator.addOnCompletion(timer::cancel);
        }, specialType, generateWidth);
    }
}
