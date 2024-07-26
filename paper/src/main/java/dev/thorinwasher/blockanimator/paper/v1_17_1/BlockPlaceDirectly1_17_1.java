package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class BlockPlaceDirectly1_17_1 implements BlockAnimator<BlockState> {

    private final World world;
    private final Map<Vector3D, FallingBlock> fallingBlocks = new HashMap<>();

    public BlockPlaceDirectly1_17_1(World world) {
        this.world = world;
    }

    @Override
    public void blockMove(Vector3D identifier, Vector3D to, BlockSupplier<BlockState> blockSupplier) {
        FallingBlock fallingBlock = spawnOrGetFallingBlock(identifier, to, blockSupplier);
        Location toLocation = VectorConverter.toLocation(to, world);
        Vector delta = toLocation.subtract(fallingBlock.getLocation()).toVector();
        double previousVelocityLength = fallingBlock.getVelocity().length();
        if (toLocation.getBlock().getType().isAir() && (previousVelocityLength == 0 || previousVelocityLength * 2 > delta.length())) {
            fallingBlock.setVelocity(delta);
        } else {
            fallingBlock.teleport(toLocation);
            fallingBlock.setVelocity(new Vector());
        }
    }

    @Override
    public void blockPlace(Vector3D identifier, BlockSupplier<BlockState> blockSupplier) {
        FallingBlock fallingBlock = fallingBlocks.remove(identifier);
        if (fallingBlock != null) {
            fallingBlock.remove();
        }
        BlockState blockState = blockSupplier.getBlock(identifier);
        blockState.update(true, false);
    }

    @Override
    public void blockDestroy(Vector3D identifier) {
        world.getBlockAt(VectorConverter.toLocation(identifier, world)).setType(Material.AIR);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockState> blockSupplier) {
        // All blocks are already placed directly, nothing more needs to be done
    }

    private FallingBlock spawnOrGetFallingBlock(Vector3D identifier, Vector3D position, BlockSupplier<BlockState> blockSupplier) {
        FallingBlock fallingBlock = fallingBlocks.get(identifier);
        if (fallingBlock == null) {
            fallingBlock = EntityUtils.spawnFallingBlock(world, blockSupplier.getBlock(identifier), position);
            fallingBlocks.put(identifier, fallingBlock);
        }
        return fallingBlock;
    }
}
