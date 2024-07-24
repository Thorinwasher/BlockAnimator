package dev.thorinwasher.blockanimator.paper;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.FallingBlock;

public class EntityUtils {


    public static BlockDisplay spawnBLockDisplay(World world, BlockState blockState, Vector3D position) {
        Location location = VectorConverter.toLocation(position, world);
        BlockDisplay blockDisplay = world.spawn(location, BlockDisplay.class);
        blockDisplay.setBlock(blockState.getBlockData());
        blockDisplay.setPersistent(false);
        blockDisplay.setGravity(false);
        return blockDisplay;
    }

    public static FallingBlock spawnFallingBlock(World world, BlockState blockState, Vector3D position) {
        Location location = VectorConverter.toLocation(position, world);
        FallingBlock fallingBlock = world.spawnFallingBlock(location, blockState.getBlockData());
        fallingBlock.setPersistent(false);
        fallingBlock.setDropItem(false);
        fallingBlock.setGravity(false);
        return fallingBlock;
    }
}
