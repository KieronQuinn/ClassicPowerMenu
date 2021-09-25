package android.app;

import android.content.pm.IPackageManager;
import android.permission.IPermissionManager;

public class AppGlobals {

    /**
     * Return the first Application object made in the process.
     * NOTE: Only works on the main thread.
     */
    public static Application getInitialApplication() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Return the package name of the first .apk loaded into the process.
     * NOTE: Only works on the main thread.
     */
    public static String getInitialPackage() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Return the raw interface to the package manager.
     * @return The package manager.
     */
    public static IPackageManager getPackageManager() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Return the raw interface to the permission manager.
     * @return The permission manager.
     */
    public static IPermissionManager getPermissionManager() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets the value of an integer core setting.
     *
     * @param key The setting key.
     * @param defaultValue The setting default value.
     * @return The core settings.
     */
    public static int getIntCoreSetting(String key, int defaultValue) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets the value of a float core setting.
     *
     * @param key The setting key.
     * @param defaultValue The setting default value.
     * @return The core settings.
     */
    public static float getFloatCoreSetting(String key, float defaultValue) {
        throw new RuntimeException("Stub!");
    }

}
