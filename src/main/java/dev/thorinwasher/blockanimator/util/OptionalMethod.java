package dev.thorinwasher.blockanimator.util;

public class OptionalMethod {

    public static final boolean FOLIA = classIsImplemented("io.papermc.paper.threadedregions.RegionizedServer");


    private static boolean classIsImplemented(String className){
        try{
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
