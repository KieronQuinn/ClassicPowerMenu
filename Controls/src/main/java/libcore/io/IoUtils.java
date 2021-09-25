package libcore.io;

import androidx.annotation.Nullable;

public class IoUtils {

    /**
     * Closes {@link AutoCloseable} instance, ignoring any checked exceptions.
     *
     * @param closeable is AutoClosable instance, null value is ignored.
     *
     */
    public static void closeQuietly(@Nullable AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

}
