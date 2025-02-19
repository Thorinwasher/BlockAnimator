package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.api.animation.Animation;
import dev.thorinwasher.blockanimator.api.animation.TimerAnimation;
import dev.thorinwasher.blockanimator.api.animator.Animator;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveLinear;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveQuadraticBezier;
import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.EaseOutCubicPathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.blockanimations.transformation.ResizeTransformation;
import dev.thorinwasher.blockanimator.api.selector.*;
import dev.thorinwasher.blockanimator.minestom.PlaceBlocksAfterBlockAnimator;
import dev.thorinwasher.blockanimator.minestom.PlaceBlocksDirectlyBlockAnimator;
import dev.thorinwasher.blockanimator.minestomtest.Main;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.timer.BlockTimer;
import dev.thorinwasher.blockanimator.api.timer.LinearBlockTimer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public class AnimateCommand extends Command {
    public AnimateCommand() {
        super("animate");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("animate <motion> <selector> <size> <time>");
        });

        ArgumentWord motion = new ArgumentWord("motion").from("linear", "quadratic");
        ArgumentEnum<BlockSelectorType> selector = new ArgumentEnum<>("selector", BlockSelectorType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentInteger size = new ArgumentInteger("size");
        ArgumentInteger time = new ArgumentInteger("time");
        addSyntax(((sender, context) -> {
            if (!(sender instanceof Player player)) {
                throw new IllegalArgumentException("Only players can execute this command!");
            }
            BlockMoveAnimation blockMoveAnimation = switch (context.get(motion)) {
                case "linear" ->
                        new BlockMoveLinear(Main.toVector3d(player.getPosition()), new EaseOutCubicPathCompletionSupplier(0.2));
                case "quadratic" ->
                        new BlockMoveQuadraticBezier(Main.toVector3d(player.getPosition()), new EaseOutCubicPathCompletionSupplier(0.2), () -> 10D);
                default -> throw new IllegalArgumentException("Unknown block animator");
            };
            blockMoveAnimation.addBlockTransform(new ResizeTransformation(1F, 0.25F, value -> value));
            BlockSupplier<Block> blockSupplier = BlockSupplierUtil.getBlockSupplier(player, context.get(size), player.getInstance());
            BlockTimer blockTimer = new LinearBlockTimer(context.get(time));
            BlockSelector blockSelector = switch (context.get(selector)) {
                case RANDOM_SPHERICAL -> new RandomSpherical();
                case BOTTOM_FIRST -> new BottomFirstSelector();
                case LAYERED_BOTTOM_FIRST -> new LayeredBottomFirst();
                case DENDRITE -> new GrowingDendriteSelector(0.2);
                case GROWING -> new GrowingSelector();
            };
            Animation<Block> animation = new TimerAnimation<>(blockSelector, blockMoveAnimation, blockSupplier, blockTimer, 100);
            Thread thread = new Thread(animation::compile);
            thread.start();
            Animator<Block> animator = new Animator<>(animation, new PlaceBlocksDirectlyBlockAnimator(player.getInstance()));
            Task timer = MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
            animator.addOnCompletion(timer::cancel);
        }), motion, selector, size, time);
    }
}
