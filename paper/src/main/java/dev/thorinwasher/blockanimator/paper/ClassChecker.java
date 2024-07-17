package dev.thorinwasher.blockanimator.paper;

public class ClassChecker {

    public static boolean methodExists(String className, String methodName, Class<?>... parameterTypes) {
        try {
            Class.forName(className).getMethod(methodName, parameterTypes);
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
