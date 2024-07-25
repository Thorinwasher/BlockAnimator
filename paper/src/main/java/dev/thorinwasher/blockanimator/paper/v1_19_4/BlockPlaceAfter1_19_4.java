package dev.thorinwasher.blockanimator.paper.v1_19_4;

import dev.thorinwasher.blockanimator.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPlaceAfter1_19_4 implements BlockAnimator<BlockState> {
    private final World world;
    private final int maxEntities;
    private final Map<Vector3D, BlockDisplay> blockDisplays = new HashMap<>();
    private final List<Vector3D> entitiesToRemove = new ArrayList<>();

    public BlockPlaceAfter1_19_4(World world, int maxEntities) {
        this.world = world;
        this.maxEntities = maxEntities;
    }

    @Override
    public void blockMove(Vector3D identifier, Vector3D to, BlockSupplier<BlockState> blockSupplier) {
        BlockDisplay blockDisplay = getOrSpawnBlockDisplay(identifier, to, blockSupplier);
        blockDisplay.teleport(VectorConverter.toLocation(to, world));
    }

    @Override
    public void blockPlace(Vector3D identifier, BlockSupplier<BlockState> blockSupplier) {
        BlockDisplay blockDisplay = getOrSpawnBlockDisplay(identifier, identifier, blockSupplier);
        blockDisplay.teleport(VectorConverter.toLocation(identifier, world));
        blockDisplay.setVelocity(new Vector());
        entitiesToRemove.add(identifier);
        if (entitiesToRemove.size() > maxEntities) {
            finishAnimation(blockSupplier);
        }
    }

    @Override
    public void blockDestroy(Vector3D identifier) {
        world.getBlockAt(VectorConverter.toLocation(identifier, world)).setType(Material.AIR);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockState> blockSupplier) {
        entitiesToRemove.forEach(identifier -> {
            BlockDisplay blockDisplay = blockDisplays.remove(identifier);
            blockDisplay.remove();
            blockSupplier.getBlock(identifier).update(true, false);
        });
        entitiesToRemove.clear();
    }

    private BlockDisplay getOrSpawnBlockDisplay(Vector3D identifier, Vector3D position, BlockSupplier<BlockState> blockSupplier) {
        BlockDisplay blockDisplay = blockDisplays.get(identifier);
        if (blockDisplay == null) {
            blockDisplay = EntityUtils.spawnBLockDisplay(world, blockSupplier.getBlock(identifier), position);
            blockDisplays.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }
}
