package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.api.algorithms.ManhatanNearest;
import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaceBlocksDirectlyBlockAnimator implements BlockAnimator<Block> {

    private final Map<ImmutableVector3i, Entity> blockEntityMap = new HashMap<>();
    private final Instance instance;

    public PlaceBlocksDirectlyBlockAnimator(Instance instance) {
        this.instance = instance;
    }

    @Override
    public void blockMove(ImmutableVector3i identifier, Vector3d position, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = spawnOrGetBLockDisplay(identifier, position, blockSupplier);
        Pos from = blockDisplay.getPosition();
        Vec delta = VectorConversion.toVec(position).sub(from);
        BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) blockDisplay.getEntityMeta();
        blockDisplayMeta.setTranslation(delta);
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<Block> supplier) {
        Entity blockDisplay = blockEntityMap.remove(identifier);
        if (blockDisplay != null) {
            blockDisplay.remove();
        }
        instance.setBlock(VectorConversion.toVec(identifier), supplier.getBlock(identifier));
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), Block.AIR);
    }

    private Entity spawnOrGetBLockDisplay(ImmutableVector3i identifier, Vector3d startingPosition, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = blockEntityMap.get(identifier);
        if (blockDisplay == null) {
            Block block = blockSupplier.getBlock(identifier);
            Vector3d middlePoint = identifier.asVector3d().mul(0.5).add(new Vector3d(startingPosition).mul(0.5));
            Optional<Vector3d> position = ManhatanNearest.findClosestPosition(middlePoint, Vector3d -> instance.getBlock(VectorConversion.toVec(Vector3d)).isAir(), 10);
            blockDisplay = BlockAnimationUtils.spawnBlockDisplay(position.orElse(startingPosition), block, instance);
            blockEntityMap.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }

    @Override
    public void finishAnimation(BlockSupplier<Block> blockSupplier) {
        // Nothing needs to be done here, as the blocks are placed directly
    }
}
