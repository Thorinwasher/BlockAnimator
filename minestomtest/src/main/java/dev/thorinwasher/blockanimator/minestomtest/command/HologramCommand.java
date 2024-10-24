package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.api.animation.hologram.HologramAnimation;
import dev.thorinwasher.blockanimator.api.animator.Animator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.minestom.PlaceBlocksAfterBlockAnimator;
import dev.thorinwasher.blockanimator.minestom.PlaceBlocksDirectlyBlockAnimator;
import dev.thorinwasher.blockanimator.minestom.VectorConversion;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.joml.Matrix4d;

public class HologramCommand extends Command {

    private static final long PERIOD = 400; // Ticks

    public HologramCommand() {
        super("hologram");
        setDefaultExecutor(((sender, context) -> {
            if (!(sender instanceof Player player)) {
                throw new IllegalArgumentException("Only players can execute this command!");
            }
            BlockSupplier<Block> blockSupplier = BlockSupplierUtil.getBlockSupplier(player, 10, player.getInstance());
            HologramAnimation<Block> animation = new HologramAnimation<>(blockSupplier, this::getTransform, VectorConversion.toImmutableVector3i(player.getPosition()), 2000, 100);
            Thread thread = new Thread(animation::compile);
            thread.start();
            Animator<Block> animator = new Animator<>(animation, new PlaceBlocksAfterBlockAnimator(Integer.MAX_VALUE, player.getInstance()));
            Task timer = MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
            animator.addOnCompletion(timer::cancel);
        }));
    }

    private Matrix4d getTransform(long time) {
        double cycleCompletion = ((double) time) / PERIOD;
        double yAxisAngle = cycleCompletion * 2 * Math.PI;
        double scale = (2 + Math.sin(yAxisAngle)) * 1/10;
        return new Matrix4d().rotation(yAxisAngle, 0, 1, 0).scale(scale);
    }
}
