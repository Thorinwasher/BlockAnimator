package dev.thorinwasher.blockanimator.paper.v1_19_4;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlockDisplayUtil {

    public static Vector getOffset(BlockDisplay blockDisplay) {
        Vector3f scale = blockDisplay.getTransformation().getScale();
        return new Vector(-scale.x()/2-0.5, -scale.y() -0.5, scale.z()/2 - 0.5);
    }

    public static void applyTransformation(BlockDisplay blockDisplay, Matrix4f transform){
        Transformation transformation = blockDisplay.getTransformation();
        Quaternionf rotation = new Quaternionf();
        transform.getUnnormalizedRotation(rotation);
        Vector3f translation = new Vector3f();
        transform.getTranslation(translation);
        Vector3f scale = new Vector3f();
        transform.getScale(scale);
        blockDisplay.setTransformation(new Transformation(translation, transformation.getLeftRotation(), scale, rotation));
    }
}
