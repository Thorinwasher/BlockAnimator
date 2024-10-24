package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;

public class PlaceBlocksAfterBlockAnimator implements BlockAnimator<Block> {

    private final int maxAmount;
    private final Map<ImmutableVector3i, Entity> entitiesToRemove = new HashMap<>();
    private final Map<ImmutableVector3i, Entity> blockEntityMap = new HashMap<>();
    private final Instance instance;

    public PlaceBlocksAfterBlockAnimator(int maxAmount, Instance instance) {
        this.maxAmount = maxAmount;
        this.instance = instance;
    }

    @Override
    public void blockMove(ImmutableVector3i identifier, Vector3d position, BlockSupplier<Block> blockSupplier, Matrix4f transform) {
        Entity blockDisplay = spawnOrGetBLockDisplay(identifier, position, blockSupplier, transform);
        BlockAnimationUtils.applyTransform(blockDisplay, transform);
        Vec offset = BlockAnimationUtils.getOffset(blockDisplay);
        blockDisplay.teleport(VectorConversion.toVec(position).sub(offset).asPosition());
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = spawnOrGetBLockDisplay(identifier, identifier.asVector3d(), blockSupplier, new Matrix4f());
        blockDisplay.setVelocity(Vec.ZERO);
        BlockAnimationUtils.applyTransform(blockDisplay, new Matrix4f());
        blockDisplay.teleport(VectorConversion.toVec(identifier).sub(BlockAnimationUtils.getOffset(blockDisplay)).asPosition());
        if (entitiesToRemove.size() > maxAmount) {
            placeBlocks(blockSupplier);
        }
        entitiesToRemove.put(identifier, blockDisplay);
        blockEntityMap.remove(identifier);
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), Block.AIR);
    }

    private Entity spawnOrGetBLockDisplay(ImmutableVector3i identifier, Vector3d startingPosition, BlockSupplier<Block> blockSupplier, Matrix4f transform) {
        Entity blockDisplay = blockEntityMap.get(identifier);
        if (blockDisplay == null) {
            Block block = blockSupplier.getBlock(identifier);
            blockDisplay = BlockAnimationUtils.spawnBlockDisplay(startingPosition, block, instance, transform);
            blockEntityMap.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }

    private void placeBlocks(BlockSupplier<Block> blockSupplier) {
        entitiesToRemove.forEach((identifier, entity) -> {
            entity.getInstance().setBlock(VectorConversion.toVec(identifier), blockSupplier.getBlock(identifier));
            entity.remove();
        });
        entitiesToRemove.clear();
    }

    @Override
    public void finishAnimation(BlockSupplier<Block> blockSupplier) {
        placeBlocks(blockSupplier);
        blockEntityMap.values().forEach(Entity::remove);
        blockEntityMap.clear();
    }
}
