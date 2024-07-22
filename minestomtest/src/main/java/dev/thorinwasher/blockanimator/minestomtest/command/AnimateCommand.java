package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.animation.Animation;
import dev.thorinwasher.blockanimator.animation.CustomAnimation;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveLinear;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveQuadraticBezier;
import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.EaseOutCubicPathCompletionSupplier;
import dev.thorinwasher.blockanimator.minestom.Animator;
import dev.thorinwasher.blockanimator.minestomtest.Main;
import dev.thorinwasher.blockanimator.minestomtest.TestSupplier;
import dev.thorinwasher.blockanimator.selector.BlockSelector;
import dev.thorinwasher.blockanimator.selector.BottomFirstSelector;
import dev.thorinwasher.blockanimator.selector.RandomSpherical;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.timer.BlockTimer;
import dev.thorinwasher.blockanimator.timer.LinearBlockTimer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnimateCommand extends Command {
    public AnimateCommand() {
        super("animate");

        ArgumentWord motion = new ArgumentWord("motion").from("linear", "quadratic");
        ArgumentWord selector = new ArgumentWord("selector").from("random_spherical", "bottom_first");
        ArgumentInteger size = new ArgumentInteger("size");
        ArgumentInteger time = new ArgumentInteger("time");
        addSyntax(((sender, context) -> {
            if (!(sender instanceof Player player)) {
                throw new IllegalArgumentException("Only players can execute this command!");
            }
            BlockMoveAnimation blockMoveAnimation = switch (context.get(motion)) {
                case "linear" -> new BlockMoveLinear(Main.toVector3D(player.getPosition()), new EaseOutCubicPathCompletionSupplier(0.2));
                case "quadratic" -> new BlockMoveQuadraticBezier(Main.toVector3D(player.getPosition()), new EaseOutCubicPathCompletionSupplier(0.2), () -> 10D);
                default -> throw new IllegalArgumentException("Unknown block animator");
            };
            BlockSupplier<Block> blockSupplier = BlockSupplierUtil.getBlockSupplier(player, context.get(size));
            BlockTimer blockTimer = new LinearBlockTimer(context.get(time));
            BlockSelector blockSelector = switch(context.get(selector)) {
                case "random_spherical" -> new RandomSpherical();
                case "bottom_first" -> new BottomFirstSelector();
                default -> throw new IllegalArgumentException("Unknown block selector");
            };
            Animation<Block> animation = new CustomAnimation<>(blockSelector, blockMoveAnimation, blockSupplier, blockTimer, 100);
            Thread thread = new Thread(animation::compile);
            thread.start();
            Animator animator = new Animator(animation, player.getInstance());
            Task timer = MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
            animator.addOnCompletion(timer::cancel);
        }), motion, selector, size, time);
    }
}
