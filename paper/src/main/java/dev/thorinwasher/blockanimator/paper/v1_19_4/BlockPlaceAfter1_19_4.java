package dev.thorinwasher.blockanimator.paper.v1_19_4;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.joml.Vector3d;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Vector;

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
    public void blockMove(ImmutableVector3i identifier, Vector3d to, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplay blockDisplay = getOrSpawnBlockDisplay(identifier, to, blockSupplier);
        blockDisplay.teleport(VectorConverter.toLocation(to, world));
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplay blockDisplay = getOrSpawnBlockDisplay(identifier, identifier.asVector3d(), blockSupplier);
        blockDisplay.teleport(VectorConverter.toLocation(identifier, world));
        blockDisplay.setVelocity(new Vector());
        entitiesToRemove.add(identifier);
        if (entitiesToRemove.size() > maxEntities) {
            finishAnimation(blockSupplier);
        }
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        world.getBlockAt(VectorConverter.toLocation(identifier, world)).setType(Material.AIR);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockData> blockSupplier) {
        entitiesToRemove.forEach(identifier -> {
            BlockDisplay blockDisplay = blockDisplays.remove(identifier);
            blockDisplay.remove();
        });
        blockSupplier.placeBlocks(entitiesToRemove);
        entitiesToRemove.clear();
    }

    private BlockDisplay getOrSpawnBlockDisplay(ImmutableVector3i identifier, Vector3d position, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplay blockDisplay = blockDisplays.get(identifier);
        if (blockDisplay == null) {
            blockDisplay = EntityUtils.spawnBLockDisplay(world, blockSupplier.getBlock(identifier), position);
            blockDisplays.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }
}
