package net.imglib2.ui;

import java.awt.image.BufferedImage;

import net.imglib2.concatenate.Concatenable;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineSet;

/**
 * Factory to create {@link Renderer Renderers}
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface RendererFactory
{
	/**
	 * Create a new {@link Renderer}.
	 *
	 * @param transformType
	 * @param display
	 *            Receiver for the rendered {@link BufferedImage BufferedImages}.
	 * @param painterThread
	 *            Thread that triggers repainting of the display. Requests for
	 *            repainting are send there.
	 * @return a {@link Renderer}
	 */
	public < A extends AffineSet & AffineGet & Concatenable< AffineGet > >
		Renderer< A > create( final AffineTransformType< A > transformType, final RenderTarget display, final PainterThread painterThread );
}