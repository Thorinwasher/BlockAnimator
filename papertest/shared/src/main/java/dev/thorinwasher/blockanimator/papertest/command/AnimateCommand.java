package dev.thorinwasher.blockanimator.papertest.command;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionManager;
import dev.thorinwasher.blockanimator.api.animation.Animation;
import dev.thorinwasher.blockanimator.api.animation.TimerAnimation;
import dev.thorinwasher.blockanimator.api.animator.Animator;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveLinear;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveQuadraticBezier;
import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.EaseOutCubicPathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.selector.*;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.timer.BlockTimer;
import dev.thorinwasher.blockanimator.api.timer.LinearBlockTimer;
import dev.thorinwasher.blockanimator.paper.blockanimator.PlaceBlocksDirectlyBlockAnimator;
import dev.thorinwasher.blockanimator.papertest.supplier.TestSupplier;
import dev.thorinwasher.blockanimator.worldedit.PaperClipboardBlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AnimateCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public AnimateCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        BlockMoveAnimation blockMoveAnimation = getMovement(args[0], player.getLocation());
        BlockSupplier<BlockData> blockSupplier = getBLockSupplier(player, Integer.parseInt(args[2]));
        BlockTimer blockTimer = new LinearBlockTimer(Integer.parseInt(args[3]));
        BlockSelector blockSelector = getBlockSelector(args[1]);
        Animation<BlockData> animation = new TimerAnimation<>(blockSelector, blockMoveAnimation, blockSupplier, blockTimer, 100);
        Animator<BlockData> animator = new Animator<>(animation, new PlaceBlocksDirectlyBlockAnimator(player.getWorld()));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, animation::compile);
        Bukkit.getScheduler().runTaskTimer(plugin, (task) -> {
            if (animator.nextTick()) {
                task.cancel();
            }
        }, 0, 1);
        return true;
    }

    private BlockSelector getBlockSelector(String blockSelectorString) {
        return switch (blockSelectorString) {
            case "layered_bottom_first" -> new LayeredBottomFirst();
            case "bottom_first" -> new BottomFirstSelector();
            case "random_spherical" -> new RandomSpherical();
            case "dendrite" -> new GrowingDendriteSelector(0.1);
            case "growing" -> new GrowingSelector();
            default -> throw new IllegalArgumentException("Unknown block selector");
        };
    }

    private BlockMoveAnimation getMovement(String argument, Location playerLocation) {
        return switch (argument) {
            case "linear" ->
                    new BlockMoveLinear(new Vector3D(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()), new EaseOutCubicPathCompletionSupplier(0.2));
            case "quadratic" ->
                    new BlockMoveQuadraticBezier(new Vector3D(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()), new EaseOutCubicPathCompletionSupplier(0.2), () -> 10D);
            default -> throw new IllegalArgumentException();
        };
    }

    private BlockSupplier<BlockData> getBLockSupplier(Player player, int width) {
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        Location targetPos = player.getLocation().add(player.getFacing().getDirection().multiply(20));
        if (sessionManager.contains(BukkitAdapter.adapt(player))) {
            try {
                ClipboardHolder clipboardHolder = sessionManager.get(BukkitAdapter.adapt(player)).getClipboard();
                return new PaperClipboardBlockSupplier(clipboardHolder.getClipboard(), targetPos, clipboardHolder.getTransform());
            } catch (EmptyClipboardException ignored) {
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
        return new TestSupplier(Material.DIAMOND_BLOCK, width, targetPos);
    }
}
