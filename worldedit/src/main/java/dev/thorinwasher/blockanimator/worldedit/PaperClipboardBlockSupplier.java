package dev.thorinwasher.blockanimator.worldedit;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.block.BaseBlock;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.paper.ClassChecker;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class PaperClipboardBlockSupplier implements BlockSupplier<BlockData> {

    private final Clipboard clipboard;
    private final Vector3D origin;
    private final com.sk89q.worldedit.world.World world;
    private final Transform transform;
    private final Transform transformInverse;

    public PaperClipboardBlockSupplier(Clipboard clipboard, Vector3D origin, World world, Transform transform) throws WorldEditException {
        this.clipboard = clipboard;
        this.origin = new Vector3D(Math.round(origin.getX()), Math.round(origin.getY()), Math.round(origin.getZ()));
        this.world = BukkitAdapter.adapt(world);
        this.transform = transform;
        this.transformInverse = transform.inverse();
    }

    @Override
    public BlockData getBlock(Vector3D targetPosition) {
        BlockVector3 relativePosition = WEVectorConverter.toBlockVector3(targetPosition.subtract(origin));
        return BukkitAdapter.adapt(getBlock(applyTransformInverse(relativePosition).add(clipboard.getOrigin())));
    }

    @Override
    public List<Vector3D> getPositions() {
        List<Vector3D> output = new ArrayList<>();
        for (BlockVector3 blockVector3 : clipboard.getRegion()) {
            BlockVector3 relativePosition = applyTransform(blockVector3.subtract(clipboard.getOrigin()));
            output.add(WEVectorConverter.toVector3D(relativePosition).add(origin));
        }
        return output;
    }

    @Override
    public void placeBlock(Vector3D identifier) {
        BlockVector3 relativeWorldCoordinate = applyTransformInverse(WEVectorConverter.toBlockVector3(identifier.subtract(origin)));
        BlockVector3 regionPosition = relativeWorldCoordinate.add(clipboard.getOrigin());
        BaseBlock baseBlock = getBlock(regionPosition);
        runSession(editSession -> {
            editSession.setBlock(WEVectorConverter.toBlockVector3(identifier), baseBlock);
        });
    }

    @Override
    public void placeBlocks(List<Vector3D> identifiers) {
        runSession(editSession -> {
            for (Vector3D identifier : identifiers) {
                BlockVector3 relativeWorldCoordinate = applyTransformInverse(WEVectorConverter.toBlockVector3(identifier.subtract(origin)));
                BlockVector3 regionPosition = relativeWorldCoordinate.add(clipboard.getOrigin());
                BaseBlock baseBlock = getBlock(regionPosition);
                editSession.setBlock(WEVectorConverter.toBlockVector3(identifier), baseBlock);
            }
        });
    }

    private void runSession(EditSessionConsumer editSessionConsumer) {
        Runnable editSessionRunnable = () -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                editSession.setSideEffectApplier(SideEffectSet.none().with(SideEffect.LIGHTING, SideEffect.State.ON));
                editSessionConsumer.accept(editSession);
                Operations.complete(editSession.commit());
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        };
        if (ClassChecker.classExists("com.fastasyncworldedit.core.FaweAPI")) {
            FaweAPI.getTaskManager().runUnsafe(editSessionRunnable);
        } else {
            editSessionRunnable.run();
        }
    }

    private BaseBlock getBlock(BlockVector3 regionPosition) {
        BaseBlock baseBlock = clipboard.getFullBlock(regionPosition);
        return BlockTransformExtent.transform(baseBlock, transform);
    }

    private BlockVector3 applyTransform(BlockVector3 blockVector3) {
        return transform.apply(blockVector3.toVector3()).toBlockPoint();
    }

    private BlockVector3 applyTransformInverse(BlockVector3 blockVector3) {
        return transformInverse.apply(blockVector3.toVector3()).toBlockPoint();
    }

    interface EditSessionConsumer {

        void accept(EditSession editSession) throws WorldEditException;
    }
}
