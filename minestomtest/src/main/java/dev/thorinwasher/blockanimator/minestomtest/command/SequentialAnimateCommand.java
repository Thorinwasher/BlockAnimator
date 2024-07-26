package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.api.animation.Animation;
import dev.thorinwasher.blockanimator.api.animation.SequentialAnimation;
import dev.thorinwasher.blockanimator.api.animator.Animator;
import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.EaseOutCubicPathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.selector.*;
import dev.thorinwasher.blockanimator.minestom.PlaceBlocksAfterBlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.Random;

public class SequentialAnimateCommand extends Command {

    private static final Random RANDOM = new Random();

    public SequentialAnimateCommand() {
        super("sequentialanimate");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(super.getName() + " <selector> <size>");
        });

        ArgumentEnum<BlockSelectorType> specialType = ArgumentType.Enum("selector", BlockSelectorType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentInteger generateWidth = ArgumentType.Integer("size");

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                throw new IllegalArgumentException("Only players can execute this command!");
            }
            BlockSupplier<Block> blockSupplier = BlockSupplierUtil.getBlockSupplier(player, context.get(generateWidth));
            PathCompletionSupplier pathCompletionSupplier = new EaseOutCubicPathCompletionSupplier(0.05);
            BlockSelector blockSelector = switch (context.get(specialType)) {
                case DENDRITE -> new GrowingDendriteSelector(0.1);
                case BOTTOM_FIRST -> new BottomFirstSelector();
                case LAYERED_BOTTOM_FIRST -> new LayeredBottomFirst();
                case RANDOM_SPHERICAL -> new RandomSpherical();
                case GROWING -> new GrowingSelector();
            };
            Animation<Block> animation = new SequentialAnimation<>(blockSupplier, pathCompletionSupplier, blockSelector, 200);
            Thread thread = new Thread(animation::compile);
            thread.start();
            Animator<Block> animator = new Animator<>(animation, new PlaceBlocksAfterBlockAnimator(Integer.MAX_VALUE, player.getInstance()));
            Task timer = MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
            animator.addOnCompletion(timer::cancel);
        }, specialType, generateWidth);
    }
}
