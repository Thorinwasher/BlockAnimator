package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class BlockPlaceDirectly1_17_1 implements BlockAnimator<BlockData> {

    private final World world;
    private final Map<Vector3D, FallingBlock> fallingBlocks = new HashMap<>();

    public BlockPlaceDirectly1_17_1(World world) {
        this.world = world;
    }

    @Override
    public void blockMove(Vector3D identifier, Vector3D to, BlockSupplier<BlockData> blockSupplier) {
        FallingBlock fallingBlock = spawnOrGetFallingBlock(identifier, to, blockSupplier);
        Location toLocation = VectorConverter.toLocation(to, world).add(-0.5,0,-0.5);
        Vector delta = toLocation.clone().subtract(fallingBlock.getLocation()).toVector();
        if (toLocation.getBlock().getType().isAir()) {
            fallingBlock.setVelocity(delta);
        } else {
            fallingBlock.teleport(toLocation);
            fallingBlock.setVelocity(new Vector());
        }
    }

    @Override
    public void blockPlace(Vector3D identifier, BlockSupplier<BlockData> blockSupplier) {
        FallingBlock fallingBlock = fallingBlocks.remove(identifier);
        if (fallingBlock != null) {
            fallingBlock.remove();
        }
        blockSupplier.placeBlock(identifier);
    }

    @Override
    public void blockDestroy(Vector3D identifier) {
        world.getBlockAt(VectorConverter.toLocation(identifier, world)).setType(Material.AIR);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockData> blockSupplier) {
        // All blocks are already placed directly, nothing more needs to be done
    }

    private FallingBlock spawnOrGetFallingBlock(Vector3D identifier, Vector3D position, BlockSupplier<BlockData> blockSupplier) {
        FallingBlock fallingBlock = fallingBlocks.get(identifier);
        if (fallingBlock == null) {
            fallingBlock = EntityUtils.spawnFallingBlock(world, blockSupplier.getBlock(identifier), position);
            fallingBlocks.put(identifier, fallingBlock);
        }
        return fallingBlock;
    }
}
