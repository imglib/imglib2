package interactive;

import interactive.remote.VolatileRealType;
import interactive.remote.VolatileRealTypeARGBConverter;
import interactive.remote.openconnectome.VolatileOpenConnectomeRandomAccessibleInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.ui.InteractiveViewer3D;
import net.imglib2.view.Views;

public class InteractiveOpenConnectomeRotationExample
{
	final static public void main( final String[] args ) throws ImgIOException
	{
//		final OpenConnectomeRandomAccessibleInterval map = new OpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/kasthuri11", 21504, 26624, 1850, 64, 64, 8, 1 );
		final VolatileOpenConnectomeRandomAccessibleInterval map = new VolatileOpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/kasthuri11", 21504, 26624, 1850, 64, 64, 8, 1 );
		
		final int w = 400, h = 300;

		final double yScale = 1.0, zScale = 12.0;
		final AffineTransform3D initial = new AffineTransform3D();
		initial.set(
			1.0, 0.0, 0.0, ( w - map.dimension( 0 ) ) / 2.0,
			0.0, yScale, 0.0, ( h - map.dimension( 1 ) * yScale ) / 2.0,
			0.0, 0.0, zScale, -( map.dimension( 2 ) / 2.0 - 0.5 ) * zScale );

		final LogoPainter logo = new LogoPainter();
		//final RandomAccessible< UnsignedByteType > extended = Views.extendValue( map, new UnsignedByteType( 127 ) );
		final RandomAccessible< VolatileRealType< UnsignedByteType > > extended = Views.extendValue( map, new VolatileRealType< UnsignedByteType >( new UnsignedByteType( 127 ) ) );
		//new InteractiveViewer3D< UnsignedByteType >( w, h, extended, map, initial, new RealARGBConverter< UnsignedByteType >( 0, 255 ) )
		new InteractiveViewer3D< VolatileRealType< UnsignedByteType > >( w, h, extended, map, initial, new VolatileRealTypeARGBConverter( 0, 255 ) )
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
