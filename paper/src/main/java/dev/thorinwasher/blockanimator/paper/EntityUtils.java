package dev.thorinwasher.blockanimator.paper;

import dev.thorinwasher.blockanimator.paper.v1_19_4.BlockDisplayUtil;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

public class EntityUtils {


    public static BlockDisplay spawnBLockDisplay(World world, BlockData blockData, Vector3d position, Matrix4f transformation) {
        Location location = VectorConverter.toLocation(position, world);
        return world.spawn(location, BlockDisplay.class, blockDisplay ->  {
            blockDisplay.setBlock(blockData);
            blockDisplay.setPersistent(false);
            blockDisplay.setGravity(false);
            BlockDisplayUtil.applyTransformation(blockDisplay, transformation);
        });
    }

    public static FallingBlock spawnFallingBlock(World world, BlockData blockData, Vector3d position) {
        Location location = VectorConverter.toLocation(position, world);
        FallingBlock fallingBlock = world.spawnFallingBlock(location, blockData);
        fallingBlock.setPersistent(false);
        fallingBlock.setDropItem(false);
        fallingBlock.setGravity(false);
        fallingBlock.setInvulnerable(true);
        return fallingBlock;
    }
}
