/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package interactive;

import interactive.fractals.JuliaRealRandomAccessible;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import net.imglib2.converter.Converter;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.ui.InteractiveDisplayCanvas;
import net.imglib2.ui.TransformEventHandler2D;
import net.imglib2.ui.TransformListener;
import net.imglib2.ui.overlay.LogoPainter;
import net.imglib2.ui.viewer.InteractiveRealViewer2D;

public class JuliaRealViewer2DExample
{
	final protected ComplexDoubleType c;

	final protected InteractiveRealViewer2D< LongType > viewer;

	protected double scale;

	protected class TransformScaleHandler implements TransformListener< AffineTransform2D >
	{
		@Override
		public void transformChanged( final AffineTransform2D transform )
		{
			final double ax = transform.get( 0, 0 );
			final double ay = transform.get( 0, 1 );
			scale = Math.sqrt( ax * ax + ay * ay );
		}
	}

	protected class JuliaListener extends TransformEventHandler2D
	{
		public JuliaListener( final TransformListener< AffineTransform2D > listener )
		{
			super( listener );
		}

		@Override
		public void mouseDragged( final MouseEvent e )
		{
			final int modifiers = e.getModifiersEx();
			if ( ( modifiers & InputEvent.BUTTON3_DOWN_MASK ) != 0 )
			{
				final double dX = e.getX() - oX;
				final double dY = e.getY() - oY;
				oX += dX;
				oY += dY;
				c.set( c.getRealDouble() + dX / 2000.0 / scale, c.getImaginaryDouble() + dY / 2000.0 / scale );
				update();
			}
			else
				super.mouseDragged( e );
		}
	}

	private final int width = 800;

	private final int height = 600;

	public JuliaRealViewer2DExample( final ComplexDoubleType c, final int maxIterations, final int maxAmplitude, final Converter< LongType, ARGBType > converter )
	{
		this.c = c;
		final JuliaRealRandomAccessible juliaset = new JuliaRealRandomAccessible( c, maxIterations, maxAmplitude );

		/* center shift */
		final AffineTransform2D centerShift = new AffineTransform2D();
		centerShift.set( 1, 0, -width / 2.0, 0, 1, -height / 2.0 );

		/* center un-shift */
		final AffineTransform2D centerUnShift = new AffineTransform2D();
		centerUnShift.set( 1, 0, width / 2.0, 0, 1, height / 2.0 );

		/* initialize rotation */
		final AffineTransform2D rotation = new AffineTransform2D();
		rotation.scale( 200 );

		rotation.preConcatenate( centerUnShift );

		viewer = new InteractiveRealViewer2D< LongType >( width, height, juliaset, rotation, converter );
		final InteractiveDisplayCanvas< AffineTransform2D > canvas = viewer.getDisplayCanvas();
		canvas.setTransformEventHandler( new JuliaListener( canvas ) );
		canvas.addTransformListener( new TransformScaleHandler() );
		canvas.addOverlayRenderer( new LogoPainter() );
		viewer.requestRepaint();
	}

	final static public void main( final String[] args ) throws ImgIOException
	{
		final int maxIterations = 100;
		final ComplexDoubleType c = new ComplexDoubleType( -0.4, 0.6 );
		final int maxAmplitude = 4096;

		final Converter< LongType, ARGBType > lut = new Converter< LongType, ARGBType >()
		{

			final protected int[] rgb = new int[ maxIterations + 1 ];
			{
				for ( int i = 0; i <= maxIterations; ++i )
				{
					final double r = 1.0 - ( double ) i / maxIterations;
					final double g = Math.sin( Math.PI * r );
					final double b = 0.5 - 0.5 * Math.cos( Math.PI * g );

					final int ri = ( int ) Math.round( Math.max( 0, 255 * r ) );
					final int gi = ( int ) Math.round( Math.max( 0, 255 * g ) );
					final int bi = ( int ) Math.round( Math.max( 0, 255 * b ) );

					rgb[ i ] = ( ( ( ri << 8 ) | gi ) << 8 ) | bi | 0xff000000;
				}
			}

			@Override
			public void convert( final LongType input, final ARGBType output )
			{
				output.set( rgb[ input.getInteger() ] );
			}
		};

		new JuliaRealViewer2DExample( c, maxIterations, maxAmplitude, lut );
	}

}
