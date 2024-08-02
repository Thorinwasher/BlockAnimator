package dev.thorinwasher.blockanimator.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.paper.ClassChecker;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.List;

public class PaperClipboardBlockSupplier implements BlockSupplier<BlockData> {

    private final BlockSupplier<BlockData> handle;

    public PaperClipboardBlockSupplier(Clipboard clipboard, Vector3D origin, World world, Transform transform) throws WorldEditException {
        if (ClassChecker.methodExists("com.sk89q.worldedit.extent.clipboard.Clipboard", "transform", Transform.class)) {
            handle = new PaperClipboardBlockSupplierV7_2_20(clipboard, origin, world, transform);
        } else {
            handle = new PaperClipboardBlockSupplierV7_2_20(clipboard, origin, world, transform);
        }
    }

    @Override
    public BlockData getBlock(Vector3D targetPosition) {
        return handle.getBlock(targetPosition);
    }

    @Override
    public List<Vector3D> getPositions() {
        return handle.getPositions();
    }

    @Override
    public void placeBlock(Vector3D identifier) {
        handle.placeBlock(identifier);
    }
}
