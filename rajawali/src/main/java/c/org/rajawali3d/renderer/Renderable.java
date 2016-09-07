package c.org.rajawali3d.renderer;

import android.support.annotation.Nullable;
import c.org.rajawali3d.annotations.GLThread;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public interface Renderable {

    /**
     * Sets the active {@link Renderer} this {@link Renderable} is registered with, or clears it if {@code null} is
     * provided.
     *
     * @param renderer The active {@link Renderer} or {@code null}.
     */
    @GLThread
    void setRenderer(@Nullable Renderer renderer);

    /**
     * Notifies this {@link Renderable} object that the render surface dimensions have changed.
     *
     * @param width {@code int} The surface width in pixels.
     * @param height {@code int} The surface height in pixels.
     * @throws IllegalStateException Thrown if this {@link Renderable} does not know about it's {@link Renderer}.
     */
    void onRenderSurfaceSizeChanged(int width, int height) throws IllegalStateException;

    void clearOverrideViewportDimensions();

    void setOverrideViewportDimensions(int width, int height);

    int getOverrideViewportWidth();

    int getOverrideViewportHeight();

    int getViewportWidth();

    int getViewportHeight();

    @GLThread
    void render(final long ellapsedRealtime, final double deltaTime) throws InterruptedException;
}