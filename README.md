# Project status
This project is still in early development, expect issues and don't use in production.

# How to import
Firstly you need to add the repository used by this project:
```kts
repositories {
  maven{
    url = uri("https://maven.pkg.github.com/Thorinwasher/BlockAnimator")
  }
}
```
## Minestom
```kts
implementation("dev.thorinwasher.blockanimator:blockanimator-api:<version>")
implementation("dev.thorinwasher.blockanimator:blockanimator-minestom:<version>")
```
## Paper
```kts
implementation("dev.thorinwasher.blockanimator:blockanimator-api:<version>")
implementation("dev.thorinwasher.blockanimator:blockanimator-paper:<version>")
```
If it's not obvious, you need to shade BlockAnimator into your paper plugin (please relocate in case another plugin will use this library).

# Some examples on uses
## Minestom
```java
// How will the path complete?
PathCompletionSupplier pathCompletionSupplier = new EaseOutCubicPathCompletionSupplier(0.2);
// At what path will the blocks move?
BlockMoveAnimation blockMoveAnimation = new BlockMoveLinear(VectorConversion.toVector3D(startingPointLocation), pathCompletionSupplier);
// At what speed should block animations start
BlockTimer blockTimer = new LinearBLockTimer(blockQueryTime);
// How are the blocks provided?
BlockSupplier<Block> blockSupplier = new SchemBlockSupplier(hollowcubeSchemInstance, rotation, cornerPoint);
// How should blocks be selected?
BlockSelector blockSelector = new GrowingDendriteSelector(0.2);
Animation<Block> animation = new TimerAnimation<>(blockSelector, blockMoveAnimation, blockSupplier, blockTimer, 100);
Thread thread = new Thread(animation::compile);
// Compile the animation async, very important, otherwise your main thread will freeze.
thread.start();
Animator<Block> animator = new Animator<>(animation, new PlaceBlocksAfterBlockAnimator(1000, instance));
// Run the animator every tick.
Task timer = MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
animator.addOnCompletion(timer::cancel);
```
