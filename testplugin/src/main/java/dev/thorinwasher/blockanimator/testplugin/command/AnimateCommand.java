package dev.thorinwasher.blockanimator.testplugin.command;

import dev.thorinwasher.blockanimator.Animation;
import dev.thorinwasher.blockanimator.CompiledAnimation;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveLinear;
import dev.thorinwasher.blockanimator.paper.Animator;
import dev.thorinwasher.blockanimator.selector.BlockSelector;
import dev.thorinwasher.blockanimator.selector.RandomSpherical;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.testplugin.supplier.TestSupplier;
import dev.thorinwasher.blockanimator.timer.BlockTimer;
import dev.thorinwasher.blockanimator.timer.LinearBlockTimer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
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
        BlockSupplier<BlockState> blockSupplier = new TestSupplier(Material.DIAMOND_BLOCK, Integer.parseInt(args[1]), player.getLocation().add(player.getFacing().getDirection().multiply(20)));
        BlockTimer blockTimer = new LinearBlockTimer(Integer.parseInt(args[2]));
        BlockSelector blockSelector = new RandomSpherical();
        CompiledAnimation<BlockState> compiledAnimation = new Animation<>(blockSelector, blockMoveAnimation, blockSupplier, blockTimer).compile();
        Animator animator = new Animator(compiledAnimation, player.getWorld());
        Bukkit.getScheduler().runTaskTimer(plugin, (task) -> {
            if (animator.nextTick()) {
                task.cancel();
            }
        }, 0, 1);
        return true;
    }

    private BlockMoveAnimation getMovement(String argument, Location playerLocation) {
        return switch (argument) {
            case "linear" ->
                    new BlockMoveLinear(new Vector3D(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()), 0.2);
            default -> throw new IllegalArgumentException();
        };
    }
}
