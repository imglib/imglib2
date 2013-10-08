package interactive;

public class InteractiveOpenConnectomeRotationExample
{
/*
	final static public void main( final String[] args ) throws ImgIOException
	{
		//final OpenConnectomeRandomAccessibleInterval map = new OpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/kasthuri11", 21504, 26624, 1850, 64, 64, 8, 1, 0 );
//		final VolatileOpenConnectomeRandomAccessibleInterval map = new VolatileOpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/bock11", 135424, 119808, 1239, 128, 128, 16, 2917, 0 );
		final VolatileOpenConnectomeRandomAccessibleInterval map = new VolatileOpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/bock11", 135424, 119808, 1239, 256, 256, 32, 2917, 0 );
		//final VolatileOpenConnectomeRandomAccessibleInterval map = new VolatileOpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/kasthuri11", 21504, 26624, 1850, 50, 50, 5, 1, 0 );

		final int w = 720, h = 405;

		final double yScale = 1.0, zScale = 10.0;
		final AffineTransform3D initial = new AffineTransform3D();
		initial.set(
			1.0, 0.0, 0.0, ( w - map.dimension( 0 ) ) / 2.0,
			0.0, yScale, 0.0, ( h - map.dimension( 1 ) * yScale ) / 2.0,
			0.0, 0.0, zScale, -( map.dimension( 2 ) / 2.0 - 0.5 ) * zScale );

		final LogoPainter logo = new LogoPainter();
//		final RandomAccessible< UnsignedByteType > extended = Views.extendValue( map, new UnsignedByteType( 127 ) );
		final RandomAccessible< VolatileRealType< UnsignedByteType > > extended = Views.extendValue( map, new VolatileRealType< UnsignedByteType >( new UnsignedByteType( 127 ) ) );
//		new InteractiveViewer3D< UnsignedByteType >( w, h, extended, map, initial, new RealARGBConverter< UnsignedByteType >( 0, 255 ) )
		new InteractiveViewer3D< VolatileRealType< UnsignedByteType > >( w, h, extended, map, initial, new VolatileRealTypeARGBConverter( 0, 255 ) )
		{
			@Override
			public boolean drawScreenImage()
			{
				final boolean valid = super.drawScreenImage();
				logo.paint( screenImage );
				return valid;
			}

			@Override
			protected Volatile2DRandomAccessibleProjector< UnsignedByteType, VolatileRealType< UnsignedByteType >, ARGBType > createProjector()
			{
				final InterpolatorFactory< VolatileRealType< UnsignedByteType >, RandomAccessible< VolatileRealType< UnsignedByteType > > > interpolatorFactory;
				switch ( interpolation )
				{
				case 0:
					interpolatorFactory = new NearestNeighborInterpolatorFactory< VolatileRealType< UnsignedByteType > >();
					break;
				case 1:
				default:
					interpolatorFactory = new NLinearInterpolatorFactory< VolatileRealType< UnsignedByteType > >();
					break;
				}
				final Interpolant< VolatileRealType< UnsignedByteType >, RandomAccessible< VolatileRealType< UnsignedByteType > > > interpolant =
						new Interpolant< VolatileRealType< UnsignedByteType >, RandomAccessible< VolatileRealType< UnsignedByteType > > >( source, interpolatorFactory );
				final AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet > mapping =
						new AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet >( interpolant, sourceToScreen.inverse() );
				return new Volatile2DRandomAccessibleProjector< UnsignedByteType, VolatileRealType< UnsignedByteType >, ARGBType >( mapping, screenImage, converter );
			}

//			@Override
//			protected XYRandomAccessibleProjector< UnsignedByteType, ARGBType > createProjector()
//			{
//				final InterpolatorFactory< UnsignedByteType, RandomAccessible< UnsignedByteType > > interpolatorFactory;
//				switch ( interpolation )
//				{
//				case 0:
//					interpolatorFactory = new NearestNeighborInterpolatorFactory< UnsignedByteType >();
//					break;
//				case 1:
//				default:
//					interpolatorFactory = new NLinearInterpolatorFactory< UnsignedByteType >();
//					break;
//				}
//				final Interpolant< UnsignedByteType, RandomAccessible< UnsignedByteType > > interpolant =
//						new Interpolant< UnsignedByteType, RandomAccessible< UnsignedByteType > >( source, interpolatorFactory );
//				final AffineRandomAccessible< UnsignedByteType, AffineGet > mapping =
//						new AffineRandomAccessible< UnsignedByteType, AffineGet >( interpolant, sourceToScreen.inverse() );
//				return new XYRandomAccessibleProjector< UnsignedByteType, ARGBType >( mapping, screenImage, converter );
//			}
		};
	}
*/
}
