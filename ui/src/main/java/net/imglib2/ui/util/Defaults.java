package net.imglib2.ui.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.imglib2.ui.MultiResolutionRenderer;
import net.imglib2.ui.Renderer;
import net.imglib2.ui.RendererFactory;
import net.imglib2.ui.viewer.InteractiveRealViewer2D;
import net.imglib2.ui.viewer.InteractiveRealViewer3D;
import net.imglib2.ui.viewer.InteractiveViewer2D;
import net.imglib2.ui.viewer.InteractiveViewer3D;

/**
 * Default rendering settings used by the convenience viewer classes
 * {@link InteractiveViewer2D}, {@link InteractiveRealViewer2D},
 * {@link InteractiveViewer3D}, and {@link InteractiveRealViewer3D}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Defaults
{
	/**
	 * Whether to discard the alpha components when drawing
	 * {@link BufferedImage} to {@link Graphics}.
	 */
	public static final boolean discardAlpha = true;

	/**
	 * Whether to use double buffered rendering.
	 */
	public static final boolean doubleBuffered = true;

	/**
	 * How many threads to use for rendering.
	 */
	public static final int numRenderingThreads = 3;

	/**
	 * For the {@link MultiResolutionRenderer}: Scale factors from the viewer
	 * canvas to screen images of different resolutions. A scale factor of 1
	 * means 1 pixel in the screen image is displayed as 1 pixel on the canvas,
	 * a scale factor of 0.5 means 1 pixel in the screen image is displayed as 2
	 * pixel on the canvas, etc.
	 */
	public static final double[] screenScales = new double[] { 1, 0.5, 0.25, 0.125 };

	/**
	 * Target rendering time in nanoseconds. The rendering time for the coarsest
	 * rendered scale in a {@link MultiResolutionRenderer} should be below this
	 * threshold.
	 */
	public static final long targetRenderNanos = 15 * 1000000;

	/**
	 * Factory to construct the default {@link Renderer} type with default settings.
	 */
	public static RendererFactory rendererFactory = new MultiResolutionRenderer.Factory( screenScales, targetRenderNanos, doubleBuffered, numRenderingThreads );
}
