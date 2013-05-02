package interactive;

import interactive.catmaid.CATMAIDRandomAccessibleInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.ui.InteractiveViewer3D;
import net.imglib2.view.Views;

public class InteractiveCATMAIDRotationExample
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		final CATMAIDRandomAccessibleInterval map = new CATMAIDRandomAccessibleInterval( 6016, 4464, 803, "http://catmaid.mpi-cbg.de/map/c-elegans/" );
//		final CATMAIDRandomAccessibleInterval map = new CATMAIDRandomAccessibleInterval( 6016, 4464, 803, "http://localhost/catmaid/" );

		final int w = 400, h = 300;

		final double yScale = 1.0, zScale = 12.0;
		final AffineTransform3D initial = new AffineTransform3D();
		initial.set(
			1.0, 0.0, 0.0, ( w - map.dimension( 0 ) ) / 2.0,
			0.0, yScale, 0.0, ( h - map.dimension( 1 ) * yScale ) / 2.0,
			0.0, 0.0, zScale, -( map.dimension( 2 ) / 2.0 - 0.5 ) * zScale );

		final LogoPainter logo = new LogoPainter();
		final RandomAccessible< ARGBType > extended = Views.extendValue( map, new ARGBType( 0xff006600 ) );
		new InteractiveViewer3D< ARGBType >( w, h, extended, map, initial, new TypeIdentity< ARGBType >() )
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
