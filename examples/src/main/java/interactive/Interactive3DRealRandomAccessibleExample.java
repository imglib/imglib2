package interactive;

import interactive.fractals.MandelbulbRealRandomAccess;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.ui.InteractiveRealViewer3D;
import net.imglib2.util.Intervals;

public class Interactive3DRealRandomAccessibleExample
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		final long maxIterations = 20;

		final RealRandomAccessible< LongType > mandelbulb = new RealRandomAccessible< LongType >()
		{
			@Override
			public int numDimensions()
			{
				return 3;
			}

			@Override
			public RealRandomAccess< LongType > realRandomAccess()
			{
				return new MandelbulbRealRandomAccess( maxIterations );
			}

			@Override
			public RealRandomAccess< LongType > realRandomAccess( final RealInterval interval )
			{
				return realRandomAccess();
			}
		};

		final int w = 400, h = 300;

		final double s = w / 4;
		final AffineTransform3D initial = new AffineTransform3D();
		initial.set(
			s, 0.0, 0.0, w / 2,
			0.0, s, 0.0, h / 2,
			0.0, 0.0, s, 0 );

		final LogoPainter logo = new LogoPainter();
		final Interval sourceInterval = Intervals.createMinMax( -2, -2, -2, 2, 2, 2 );
		final RealARGBConverter< LongType > converter = new RealARGBConverter< LongType >( 0, maxIterations );
		new InteractiveRealViewer3D< LongType >( w, h, mandelbulb, sourceInterval, initial, converter )
		{
			@Override
			public void drawScreenImage()
			{
				super.drawScreenImage();
				logo.paint( screenImage );
			}
		};
	}
}
