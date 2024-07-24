package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.HashMap;
import java.util.Map;

public class PlaceBlocksDirectlyBlockAnimator implements BlockAnimator<Block> {

    private final Map<Vector3D, Entity> blockEntityMap = new HashMap<>();
    private final Instance instance;

    public PlaceBlocksDirectlyBlockAnimator(Instance instance) {
        this.instance = instance;
    }

    @Override
    public void blockMove(Vector3D identifier, Vector3D position, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = spawnOrGetBLockDisplay(identifier, position, blockSupplier);
        Pos from = blockDisplay.getPosition();
        Vec delta = VectorConversion.toVec(position).sub(from);
        BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) blockDisplay.getEntityMeta();
        blockDisplayMeta.setTranslation(delta);
    }

    @Override
    public void blockPlace(Vector3D identifier, BlockSupplier<Block> supplier) {
        Entity blockDisplay = blockEntityMap.get(identifier);
        blockDisplay.remove();
        blockDisplay.getInstance().setBlock(VectorConversion.toVec(identifier), supplier.getBlock(identifier));
    }

    @Override
    public void blockDestroy(Vector3D identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), Block.AIR);
    }

    private Entity spawnOrGetBLockDisplay(Vector3D identifier, Vector3D startingPosition, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = blockEntityMap.get(identifier);
        if (blockDisplay == null) {
            Block block = blockSupplier.getBlock(identifier);
            Vector3D position = identifier.scalarMultiply(0.5).add(startingPosition.scalarMultiply(0.5));
            blockDisplay = BlockAnimationUtils.spawnBlockDisplay(position, block, instance);
            blockEntityMap.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }

    @Override
    public void finishAnimation(BlockSupplier<Block> blockSupplier) {
        // Nothing needs to be done here, as the blocks are placed directly
    }
}
