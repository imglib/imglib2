package net.imglib2.ui;

import net.imglib2.concatenate.Concatenable;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineSet;

public interface RendererFactory
{
	public < A extends AffineSet & AffineGet & Concatenable< AffineGet > >
		Renderer< A > create( final AffineTransformType< A > transformType, final RenderTarget display, final PainterThread painterThread );
}