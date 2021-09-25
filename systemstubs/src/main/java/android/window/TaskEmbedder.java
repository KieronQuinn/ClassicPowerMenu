package android.window;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.IWindow;

import androidx.annotation.Nullable;

public class TaskEmbedder
{

    /**
     * A component which will host the task.
     */
    public interface Host {
        /** @return the screen area where touches should be dispatched to the embedded Task */
        Region getTapExcludeRegion();

        /** @return a matrix which transforms from screen-space to the embedded task surface */
        Matrix getScreenToTaskMatrix();

        /** @return the window containing the parent surface, if attached and available */
        @Nullable
        IWindow getWindow();

        /** @return the x/y offset from the origin of the window to the surface */
        Point getPositionInWindow();

        /** @return the screen bounds of the host */
        Rect getScreenBounds();

        /** @return whether this surface is able to receive pointer events */
        boolean canReceivePointerEvents();

        /** @return the width of the container for the embedded task */
        int getWidth();

        /** @return the height of the container for the embedded task */
        int getHeight();

        /**
         * Called to inform the host of the task's background color. This can be used to
         * fill unpainted areas if necessary.
         */
        void onTaskBackgroundColorChanged(TaskEmbedder ts, int bgColor);

        /**
         * Posts a runnable to be run on the host's handler.
         */
        boolean post(Runnable r);
    }

}
