package interactive;
import interactive.fractals.JuliaRealRandomAccessible;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import net.imglib2.converter.Converter;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.ui.InteractiveRealViewer2D;

public class JuliaRealViewer2DExample
{
	final protected ComplexDoubleType c;
	final protected JuliaRealRandomAccessible juliaset;
	final protected InteractiveRealViewer2D< LongType > viewer;

	public double getScale()
	{
		final AffineTransform2D a = viewer.getDisplay().getViewerTransform();
		final double ax = a.get( 0, 0 );
		final double ay = a.get( 0, 1 );
		return Math.sqrt( ax * ax + ay * ay );
	}

	public class JuliaListener implements MouseMotionListener, MouseListener
	{
		protected int oX, oY, dX, dY;

		@Override
		public void mouseDragged( final MouseEvent e )
		{
			final int modifiers = e.getModifiersEx();
			if ( ( modifiers & MouseEvent.BUTTON3_DOWN_MASK ) != 0 )
			{
				dX = e.getX() - oX;
				dY = e.getY() - oY;
				oX += dX;
				oY += dY;
				c.set( c.getRealDouble() + dX / 2000.0 / getScale(), c.getImaginaryDouble() + dY / 2000.0 / getScale() );
			}
		}

		@Override
		public void mouseMoved( final MouseEvent e ){}
		@Override
		public void mouseClicked( final MouseEvent e ){}
		@Override
		public void mouseEntered( final MouseEvent e ){}
		@Override
		public void mouseExited( final MouseEvent e ){}
		@Override
		public void mouseReleased( final MouseEvent e ){}
		@Override
		public void mousePressed( final MouseEvent e )
		{
			oX = e.getX();
			oY = e.getY();
		}
	};
	private final int width = 800;
	private final int height = 600;

	public JuliaRealViewer2DExample(
			final ComplexDoubleType c,
			final int maxIterations,
			final int maxAmplitude,
			final Converter< LongType, ARGBType > converter )
	{
		this.c = c;
		juliaset = new JuliaRealRandomAccessible( c, maxIterations, maxAmplitude );

		/* center shift */
		final AffineTransform2D centerShift = new AffineTransform2D();
		centerShift.set(
				1, 0, -width / 2.0,
				0, 1, -height / 2.0 );

		/* center un-shift */
		final AffineTransform2D centerUnShift = new AffineTransform2D();
		centerUnShift.set(
				1, 0, width / 2.0,
				0, 1, height / 2.0 );

		/* initialize rotation */
		final AffineTransform2D rotation = new AffineTransform2D();
		rotation.scale( 200 );

		rotation.preConcatenate( centerUnShift );

		final LogoPainter logo = new LogoPainter();
		viewer = new InteractiveRealViewer2D< LongType >( width, height, juliaset, rotation, converter )
		{
			@Override
			public void drawScreenImage()
			{
				super.drawScreenImage();
				logo.paint( screenImage );
			}
		};
		viewer.getDisplay().addHandler( new JuliaListener() );
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
					final double r = 1.0 - ( double )i / maxIterations;
					final double g = Math.sin( Math.PI * r );
					final double b = 0.5 - 0.5 * Math.cos( Math.PI * g );

					final int ri = ( int )Math.round( Math.max( 0, 255 * r ) );
					final int gi = ( int )Math.round( Math.max( 0, 255 * g ) );
					final int bi = ( int )Math.round( Math.max( 0, 255 * b ) );

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
