# Project status
The project is currently in late prototype stage. I Will find a way on how to publish this to a repository and so on  later on.

# How to import
As said above, I have not made a way to publish this to a repository at the moment, this becomes more of a placeholder guide
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
thread.start();
Animator<Block> animator = new Animator<>(animation, new PlaceBlocksAfterBlockAnimator(1000, instance));
Task timer = MinecraftServer.getSchedulerManager().scheduleTask(animator::nextTick, TaskSchedule.immediate(), TaskSchedule.tick(1));
animator.addOnCompletion(timer::cancel);
```
