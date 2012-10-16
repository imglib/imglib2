package interactive;
import interactive.fractals.MandelbrotRealRandomAccessible;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.ui.InteractiveRealViewer2D;

public class MandelbrotRealViewer2DExample< T extends NumericType< T > & NativeType< T > >
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		final int width = 800;
		final int height = 600;

		final int maxIterations = 100;
		final MandelbrotRealRandomAccessible mandelbrot = new MandelbrotRealRandomAccessible( maxIterations );

		final AffineTransform2D transform = new AffineTransform2D();
		transform.scale( 200 );
		transform.translate( width / 2.0, height / 2.0 );

		final LogoPainter logo = new LogoPainter();
		final RealARGBConverter< LongType > converter = new RealARGBConverter< LongType >( 0, maxIterations );
		new InteractiveRealViewer2D< LongType >( width, height, mandelbrot, transform, converter )
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
