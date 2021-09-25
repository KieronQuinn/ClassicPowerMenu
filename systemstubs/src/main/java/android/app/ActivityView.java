/**
 * Copyright (c) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.app;

import android.annotation.TestApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.display.VirtualDisplay;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.IWindow;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Task container that allows launching activities into itself.
 * <p>Activity launching into this container is restricted by the same rules that apply to launching
 * on VirtualDisplays.
 * @hide
 */
@TestApi
public class ActivityView extends ViewGroup implements android.window.TaskEmbedder.Host {

    public ActivityView(Context context) {
        this(context, null /* attrs */);
    }

    public ActivityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /* defStyle */);
    }

    public ActivityView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, false /*singleTaskInstance*/);
    }

    public ActivityView(Context context, AttributeSet attrs, int defStyle,
                        boolean singleTaskInstance) {
        this(context, attrs, defStyle, singleTaskInstance, false /* usePublicVirtualDisplay */);
    }

    /**
     * This constructor let's the caller explicitly request a public virtual display as the backing
     * display. Using a public display is not recommended as it exposes it to other applications,
     * but it might be needed for backwards compatibility.
     */
    public ActivityView(
            @NonNull Context context, @NonNull AttributeSet attrs, int defStyle,
            boolean singleTaskInstance, boolean usePublicVirtualDisplay) {
        this(context, attrs, defStyle, singleTaskInstance, usePublicVirtualDisplay,
                false /* disableSurfaceViewBackgroundLayer */);
    }

    public ActivityView(
            @NonNull Context context, @NonNull AttributeSet attrs, int defStyle,
            boolean singleTaskInstance, boolean usePublicVirtualDisplay,
            boolean disableSurfaceViewBackgroundLayer) {
        this(context, attrs, defStyle, singleTaskInstance, usePublicVirtualDisplay,
                disableSurfaceViewBackgroundLayer, false /* useTrustedDisplay */);
    }

    public ActivityView(
            @NonNull Context context, @NonNull AttributeSet attrs, int defStyle,
            boolean singleTaskInstance, boolean usePublicVirtualDisplay,
            boolean disableSurfaceViewBackgroundLayer, boolean useTrustedDisplay) {
        super(context, attrs, defStyle);
    }

    /** Callback that notifies when the container is ready or destroyed. */
    public abstract static class StateCallback {

        /**
         * Called when the container is ready for launching activities. Calling
         * {@link #startActivity(Intent)} prior to this callback will result in an
         * {@link IllegalStateException}.
         *
         * @see #startActivity(Intent)
         */
        public abstract void onActivityViewReady(ActivityView view);

        /**
         * Called when the container can no longer launch activities. Calling
         * {@link #startActivity(Intent)} after this callback will result in an
         * {@link IllegalStateException}.
         *
         * @see #startActivity(Intent)
         */
        public abstract void onActivityViewDestroyed(ActivityView view);

        /**
         * Called when a task is created inside the container.
         * This is a filtered version of {@link TaskStackListener}
         */
        public void onTaskCreated(int taskId, ComponentName componentName) { }

        /**
         * Called when a task visibility changes.
         * @hide
         */
        public void onTaskVisibilityChanged(int taskId, boolean visible) { }

        /**
         * Called when a task is moved to the front of the stack inside the container.
         * This is a filtered version of {@link TaskStackListener}
         */
        public void onTaskMovedToFront(int taskId) { }

        /**
         * Called when a task is about to be removed from the stack inside the container.
         * This is a filtered version of {@link TaskStackListener}
         */
        public void onTaskRemovalStarted(int taskId) { }

        /**
         * Called when back is pressed on the root activity of the task.
         * @hide
         */
        public void onBackPressedOnTaskRoot(int taskId) { }
    }

    /**
     * Set the callback to be notified about state changes.
     * <p>This class must finish initializing before {@link #startActivity(Intent)} can be called.
     * <p>Note: If the instance was ready prior to this call being made, then
     * {@link StateCallback#onActivityViewReady(ActivityView)} will be called from within
     * this method call.
     *
     * @param callback The callback to report events to.
     *
     * @see StateCallback
     * @see #startActivity(Intent)
     */
    public void setCallback(StateCallback callback) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Sets the corner radius for the Activity displayed here. The corners will be
     * cropped from the window painted by the contained Activity.
     *
     * @param cornerRadius the radius for the corners, in pixels
     * @hide
     */
    public void setCornerRadius(float cornerRadius) {
        throw new RuntimeException("Stub!");
    }

    /**
     * @hide
     */
    public float getCornerRadius() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Control whether the surface is clipped to the same bounds as the View. If true, then
     * the bounds set by {@link #setSurfaceClipBounds(Rect)} are applied to the surface as
     * window-crop.
     *
     * @param clippingEnabled whether to enable surface clipping
     * @hide
     */
    public void setSurfaceClippingEnabled(boolean clippingEnabled) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Sets an area on the contained surface to which it will be clipped
     * when it is drawn. Setting the value to null will remove the clip bounds
     * and the surface will draw normally, using its full bounds.
     *
     * @param clipBounds The rectangular area, in the local coordinates of
     * this view, to which future drawing operations will be clipped.
     * @hide
     */
    public void setSurfaceClipBounds(Rect clipBounds) {
        throw new RuntimeException("Stub!");
    }

    /**
     * @hide
     */
    public boolean getSurfaceClipBounds(Rect outRect) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Launch an activity represented by {@link ShortcutInfo} into this container.
     * <p>The owner of this container must be allowed to access the shortcut information,
     * as defined in {@link LauncherApps#hasShortcutHostPermission()} to use this method.
     * <p>Activity resolved by the provided {@link ShortcutInfo} must have
     * {@link android.R.attr#resizeableActivity} attribute set to {@code true} in order to be
     * launched here. Also, if activity is not owned by the owner of this container, it must allow
     * embedding and the caller must have permission to embed.
     * <p>Note: This class must finish initializing and
     * {@link StateCallback#onActivityViewReady(ActivityView)} callback must be triggered before
     * this method can be called.
     *
     * @param shortcut the shortcut used to launch the activity.
     * @param options for the activity.
     * @param sourceBounds the rect containing the source bounds of the clicked icon to open
     *                     this shortcut.
     * @see StateCallback
     * @see LauncherApps#startShortcut(ShortcutInfo, Rect, Bundle)
     *
     * @hide
     */
    public void startShortcutActivity(@NonNull ShortcutInfo shortcut,
                                      @NonNull ActivityOptions options, @Nullable Rect sourceBounds) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Launch a new activity into this container.
     * <p>Activity resolved by the provided {@link Intent} must have
     * {@link android.R.attr#resizeableActivity} attribute set to {@code true} in order to be
     * launched here. Also, if activity is not owned by the owner of this container, it must allow
     * embedding and the caller must have permission to embed.
     * <p>Note: This class must finish initializing and
     * {@link StateCallback#onActivityViewReady(ActivityView)} callback must be triggered before
     * this method can be called.
     *
     * @param intent Intent used to launch an activity.
     *
     * @see StateCallback
     * @see #startActivity(PendingIntent)
     */
    public void startActivity(@NonNull Intent intent) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Launch a new activity into this container.
     * <p>Activity resolved by the provided {@link Intent} must have
     * {@link android.R.attr#resizeableActivity} attribute set to {@code true} in order to be
     * launched here. Also, if activity is not owned by the owner of this container, it must allow
     * embedding and the caller must have permission to embed.
     * <p>Note: This class must finish initializing and
     * {@link StateCallback#onActivityViewReady(ActivityView)} callback must be triggered before
     * this method can be called.
     *
     * @param intent Intent used to launch an activity.
     * @param user The UserHandle of the user to start this activity for.
     *
     *
     * @see StateCallback
     * @see #startActivity(PendingIntent)
     */
    public void startActivity(@NonNull Intent intent, UserHandle user) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Launch a new activity into this container.
     * <p>Activity resolved by the provided {@link PendingIntent} must have
     * {@link android.R.attr#resizeableActivity} attribute set to {@code true} in order to be
     * launched here. Also, if activity is not owned by the owner of this container, it must allow
     * embedding and the caller must have permission to embed.
     * <p>Note: This class must finish initializing and
     * {@link StateCallback#onActivityViewReady(ActivityView)} callback must be triggered before
     * this method can be called.
     *
     * @param pendingIntent Intent used to launch an activity.
     *
     * @see StateCallback
     * @see #startActivity(Intent)
     */
    public void startActivity(@NonNull PendingIntent pendingIntent) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Launch a new activity into this container.
     * <p>Activity resolved by the provided {@link PendingIntent} must have
     * {@link android.R.attr#resizeableActivity} attribute set to {@code true} in order to be
     * launched here. Also, if activity is not owned by the owner of this container, it must allow
     * embedding and the caller must have permission to embed.
     * <p>Note: This class must finish initializing and
     * {@link StateCallback#onActivityViewReady(ActivityView)} callback must be triggered before
     * this method can be called.
     *
     * @param pendingIntent Intent used to launch an activity.
     * @param fillInIntent Additional Intent data, see {@link Intent#fillIn Intent.fillIn()}.
     * @param options options for the activity
     *
     * @see StateCallback
     * @see #startActivity(Intent)
     */
    public void startActivity(@NonNull PendingIntent pendingIntent, @Nullable Intent fillInIntent,
                              @NonNull ActivityOptions options) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Release this container if it is initialized. Activity launching will no longer be permitted.
     */
    public void release() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Triggers an update of {@link ActivityView}'s location in window to properly set tap exclude
     * regions and avoid focus switches by touches on this view.
     */
    public void onLocationChanged() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Sets the alpha value when the content of {@link SurfaceView} needs to show or hide.
     * <p>Note: The surface view may ignore the alpha value in some cases. Refer to
     * {@link SurfaceView#setAlpha} for more details.
     *
     * @param alpha The opacity of the view.
     */
    @Override
    public void setAlpha(float alpha) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public float getAlpha() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public boolean gatherTransparentRegion(Region region) {
        throw new RuntimeException("Stub!");
    }

    /**
     * @return the display id of the virtual display.
     */
    public int getVirtualDisplayId() {
        throw new RuntimeException("Stub!");
    }

    /**
     * @hide
     * @return virtual display.
     */
    public VirtualDisplay getVirtualDisplay() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Injects a pair of down/up key events with keycode {@link KeyEvent#KEYCODE_BACK} to the
     * virtual display.
     */
    public void performBackPress() {
        throw new RuntimeException("Stub!");
    }

    @Override
    protected void finalize() throws Throwable {
        throw new RuntimeException("Stub!");
    }

    /**
     * Set forwarded insets on the virtual display.
     *
     * @see IWindowManager#setForwardedInsets
     */
    public void setForwardedInsets(Insets insets) {
        throw new RuntimeException("Stub!");
    }

    // Host

    /** @hide */
    @Override
    public void onTaskBackgroundColorChanged(android.window.TaskEmbedder ts, int bgColor) {
        throw new RuntimeException("Stub!");
    }

    /** @hide */
    @Override
    public Region getTapExcludeRegion() {
        throw new RuntimeException("Stub!");
    }

    /** @hide */
    @Override
    public Matrix getScreenToTaskMatrix() {
        throw new RuntimeException("Stub!");
    }

    @Nullable
    @Override
    public IWindow getWindow() {
        return null;
    }

    /** @hide */
    @Override
    public Point getPositionInWindow() {
        throw new RuntimeException("Stub!");
    }

    /** @hide */
    @Override
    public Rect getScreenBounds() {
        throw new RuntimeException("Stub!");
    }

    /** @hide */
    @Override
    public boolean canReceivePointerEvents() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Overridden by instances that require the use of the task organizer implementation instead of
     * the virtual display implementation.  Not for general use.
     * @hide
     */
    protected boolean useTaskOrganizer() {
        throw new RuntimeException("Stub!");
    }
}