package dev.thorinwasher.blockanimator.animation;

public interface Animation extends Runnable{

    /**
     * Get the length of this animation in ticks
     * @return <p>The length of this animation</p>
     */
    int getLength();
}
