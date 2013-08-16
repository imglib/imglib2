package net.imglib2.ui.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.imglib2.ui.MultiResolutionRenderer;
import net.imglib2.ui.RendererFactory;

public class Defaults
{
	/**
	 * Whether to discard the alpha components when drawing
	 * {@link BufferedImage} to {@link Graphics}.
	 */
	public static final boolean discardAlpha = true;

	/**
	 * TODO
	 */
	public static final boolean doubleBuffered = true;

	/**
	 * TODO
	 */
	public static final int numRenderingThreads = 3;

	/**
	 * TODO
	 */
	public static final double[] screenScales = new double[] { 1, 0.5, 0.25, 0.125 };

	/**
	 * TODO
	 */
	public static final long targetRenderNanos = 15 * 1000000;

	/**
	 * TODO
	 */
	public static RendererFactory rendererFactory = new MultiResolutionRenderer.Factory( screenScales, targetRenderNanos, doubleBuffered, numRenderingThreads );
}
