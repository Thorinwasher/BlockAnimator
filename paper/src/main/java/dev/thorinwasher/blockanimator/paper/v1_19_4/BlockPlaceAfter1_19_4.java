package dev.thorinwasher.blockanimator.paper.v1_19_4;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPlaceAfter1_19_4 implements BlockAnimator<BlockData> {
    private final World world;
    private final int maxEntities;
    private final Map<ImmutableVector3i, BlockDisplay> blockDisplays = new HashMap<>();
    private final List<ImmutableVector3i> entitiesToRemove = new ArrayList<>();

    public BlockPlaceAfter1_19_4(World world, int maxEntities) {
        this.world = world;
        this.maxEntities = maxEntities;
    }

    @Override
    public void blockMove(ImmutableVector3i identifier, Vector3d to, BlockSupplier<BlockData> blockSupplier, Matrix4f transform) {
        BlockDisplay blockDisplay = getOrSpawnBlockDisplay(identifier, to, blockSupplier, transform);
        BlockDisplayUtil.applyTransformation(blockDisplay, transform);
        blockDisplay.teleport(VectorConverter.toLocation(to, world).subtract(BlockDisplayUtil.getOffset(blockDisplay)));
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplay blockDisplay = getOrSpawnBlockDisplay(identifier, identifier.asVector3d(), blockSupplier, new Matrix4f());
        blockDisplay.teleport(VectorConverter.toLocation(identifier, world));
        blockDisplay.setVelocity(new Vector());
        entitiesToRemove.add(identifier);
        if (entitiesToRemove.size() > maxEntities) {
            placeBlocks(blockSupplier);
        }
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        world.getBlockAt(VectorConverter.toLocation(identifier, world)).setType(Material.AIR);
    }

    private void placeBlocks(BlockSupplier<BlockData> blockSupplier) {
        entitiesToRemove.forEach(identifier -> {
            BlockDisplay blockDisplay = blockDisplays.remove(identifier);
            blockDisplay.remove();
        });
        blockSupplier.placeBlocks(entitiesToRemove);
        entitiesToRemove.clear();
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockData> blockSupplier) {
        placeBlocks(blockSupplier);
        blockDisplays.values().forEach(BlockDisplay::remove);
        blockDisplays.clear();
    }

    private BlockDisplay getOrSpawnBlockDisplay(ImmutableVector3i identifier, Vector3d position, BlockSupplier<BlockData> blockSupplier, Matrix4f transform) {
        BlockDisplay blockDisplay = blockDisplays.get(identifier);
        if (blockDisplay == null) {
            blockDisplay = EntityUtils.spawnBLockDisplay(world, blockSupplier.getBlock(identifier), position, transform);
            blockDisplays.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }
}
