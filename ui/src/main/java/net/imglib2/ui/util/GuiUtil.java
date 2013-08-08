package net.imglib2.ui.util;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import net.imglib2.display.ARGBScreenImage;

public class GuiUtil
{
	/**
	 * Get a {@link GraphicsConfiguration} from the default screen
	 * {@link GraphicsDevice} that matches the
	 * {@link ColorModel#getTransparency() transparency} of the given
	 * <code>colorModel</code>. If no matching configuration is found, the
	 * default configuration of the {@link GraphicsDevice} is returned.
	 */
	public static final GraphicsConfiguration getSuitableGraphicsConfiguration( final ColorModel colorModel )
	{
		final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		final GraphicsConfiguration defaultGc = device.getDefaultConfiguration();

		final int transparency = colorModel.getTransparency();

		if ( defaultGc.getColorModel( transparency ).equals( colorModel ) )
			return defaultGc;

		for ( final GraphicsConfiguration gc : device.getConfigurations() )
			if ( gc.getColorModel( transparency ).equals( colorModel ) )
				return gc;

		return defaultGc;
	}

	/**
	 * TODO
	 */
	public static final boolean defaultDoubleBuffered = true;

	/**
	 * TODO
	 */
	public static final int defaultNumRenderingThreads = 3;

	/**
	 * Whether to discard the alpha components when drawing
	 * {@link BufferedImage} to {@link Graphics}.
	 */
	public static final boolean defaultDiscardAlpha = true;

	public static final ColorModel ARGB_COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);

	public static final ColorModel RGB_COLOR_MODEL = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);

	/**
	 * Get a {@link BufferedImage} for the given {@link ARGBScreenImage}.
	 *
	 * @param screenImage
	 *            the image.
	 * @param discardAlpha
	 *            Whether to discard the <code>screenImage</code> alpha components
	 *            when drawing.
	 */
	public static final BufferedImage getBufferedImage( final ARGBScreenImage screenImage, final boolean discardAlpha )
	{
		final BufferedImage si = screenImage.image();
		if ( discardAlpha && ( si.getTransparency() != Transparency.OPAQUE ) )
		{
			final SampleModel sampleModel = RGB_COLOR_MODEL.createCompatibleWritableRaster( 1, 1 ).getSampleModel().createCompatibleSampleModel( si.getWidth(), si.getHeight() );
			final DataBuffer dataBuffer = si.getRaster().getDataBuffer();
			final WritableRaster rgbRaster = Raster.createWritableRaster( sampleModel, dataBuffer, null );
			return new BufferedImage( RGB_COLOR_MODEL, rgbRaster, false, null );
		}
		else
			return si;
	}

	/**
	 * Get a {@link BufferedImage} for the given {@link ARGBScreenImage}.
	 * {@link #defaultDiscardAlpha} determines whether to discard the
	 * <code>screenImage</code> alpha components when drawing.
	 *
	 * @param screenImage
	 *            the image.
	 */
	public static final BufferedImage getBufferedImage( final ARGBScreenImage screenImage )
	{
		return getBufferedImage( screenImage, defaultDiscardAlpha );
	}
}
