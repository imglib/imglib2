package interactive;

import interactive.remote.openconnectome.VolatileOpenConnectomeRandomAccessibleInterval;

import java.util.ArrayList;

import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.display.VolatileRealType;
import net.imglib2.display.VolatileRealTypeARGBConverter;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.ui.viewer.InteractiveViewer3D;
import net.imglib2.view.Views;

public class InteractiveHierarchyOpenConnectomeRotationExample
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		final int w = 720, h = 405;
		
		final long[][] levelDimensions = new long[][]{
				{ 21504, 26624, 1850 },
				{ 10752, 13312, 1850 },
				{ 5376, 6656, 1850 },
				{ 2816, 3328, 1850 },
				{ 1536, 1792, 1850 },
				{ 768, 1024, 1850 },
				{ 512, 512, 1850 },
				{ 256, 256, 1850 } };
		
		final double[][] levelScales = new double[][]{
				{ 1, 1, 10 },
				{ 2, 2, 10 },
				{ 4, 4, 10 },
				{ 8, 8, 10 },
				{ 16, 16, 10 },
				{ 32, 32, 10 },
				{ 64, 64, 10 },
				{ 128, 128, 10 } };
		
//		final int[][] levelCellDimensions = new int[][]{
//				{ 128, 128, 16 },
//				{ 128, 128, 16 },
//				{ 128, 128, 16 },
//				{ 128, 128, 16 },
//				{ 128, 128, 16 },
//				{ 128, 128, 16 },
//				{ 64, 64, 64 },
//				{ 64, 64, 64 } };

//		final int[][] levelCellDimensions = new int[][]{
//				{ 50, 50, 5 },
//				{ 50, 50, 10 },
//				{ 50, 50, 20 },
//				{ 50, 50, 40 },
//				{ 20, 20, 20 },
//				{ 20, 20, 20 },
//				{ 20, 20, 20 },
//				{ 20, 20, 20 } };
		
		final int[][] levelCellDimensions = new int[][]{
				{ 128, 128, 1 },
				{ 128, 128, 1 },
				{ 128, 128, 1 },
				{ 128, 128, 1 },
				{ 128, 128, 1 },
				{ 128, 128, 1 },
				{ 64, 64, 1 },
				{ 64, 64, 1 } };
		
		final ArrayList< ExtendedRandomAccessibleInterval< VolatileRealType< UnsignedByteType >, ? > > sources = new ArrayList< ExtendedRandomAccessibleInterval< VolatileRealType< UnsignedByteType >, ? > >();
		final ArrayList< AffineTransform3D > sourceTransforms = new ArrayList< AffineTransform3D >();
		final ArrayList< AffineTransform3D > sourceToScreens = new ArrayList< AffineTransform3D >();
		
		for ( int level = 0; level < levelScales.length; level++ )
		{
			final AffineTransform3D levelTransform = new AffineTransform3D();
			for ( int d = 0; d < 3; ++d )
			{
				levelTransform.set( levelScales[ level ][ d ], d, d );
				//levelTransform.set( 0.5 * ( levelScales[ level ][ d ] - 1 ), d, 3 );
			}
			/* TODO This is only a hack for the current sourceTransform defining the bound ing box extents */
			levelTransform.set( levelScales[ level ][ 2 ] / levelScales[ 0 ][ 2 ], 2, 2 );
			levelTransform.set( -0.5 * levelScales[ level ][ 0 ], 0, 3 );
			levelTransform.set( -0.5 * levelScales[ level ][ 1 ], 1, 3 );
			
			sourceTransforms.add( levelTransform );
			sourceToScreens.add( new AffineTransform3D() );
			
			System.out.println( levelTransform );
			
			final VolatileOpenConnectomeRandomAccessibleInterval source = new VolatileOpenConnectomeRandomAccessibleInterval(
					"http://openconnecto.me/emca/kasthuri11",
					levelDimensions[ level ][ 0 ],
					levelDimensions[ level ][ 1 ],
					levelDimensions[ level ][ 2 ],
					levelCellDimensions[ level ][ 0 ],
					levelCellDimensions[ level ][ 1 ],
					levelCellDimensions[ level ][ 2 ],
					1, level );
			
			sources.add( Views.extendValue( source, new VolatileRealType< UnsignedByteType >( new UnsignedByteType( 127 ) ) ) );
		}
		
		/* transform sources */
		final AffineTransform3D viewerTransform = new AffineTransform3D();
		viewerTransform.set(
				1.0, 0.0, 0.0, ( w - levelDimensions[ 0 ][ 0 ] ) / 2.0,
				0.0, 1.0, 0.0, ( h - levelDimensions[ 0 ][ 1 ] ) / 2.0,
				0.0, 0.0, levelScales[ 0 ][ 2 ], -( levelDimensions[ 0 ][ 2 ] / 2.0 - 0.5 ) * levelScales[ 0 ][ 2 ] );
		

		final LogoPainter logo = new LogoPainter();
		new InteractiveViewer3D< VolatileRealType< UnsignedByteType > >(
				w,
				h,
				sources.get( 0 ),
				new FinalInterval( levelDimensions[ 0 ] ),
				viewerTransform,
				new VolatileRealTypeARGBConverter( 0, 255 ) )
		{
			@Override
			public boolean drawScreenImage()
			{
				final HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > > p =
						( HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > > )projector;

				synchronized( viewerTransform )
				{
					for ( final AffineTransform3D t : sourceToScreens )
						t.set( viewerTransform );
				}
				
				for ( int i = 0; i < sourceTransforms.size(); ++i )
				{
					/* TODO This is a hack for sourceTransform currently defining the bounding box */
					sourceToScreens.get( i ).concatenate( sourceTransform );
					sourceToScreens.get( i ).concatenate( sourceTransforms.get( i ) );
				}
				
				p.map();
				
				logo.paint( screenImage );
				
				return ( ( HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > > )projector ).isValid();
			}
			
			@Override
			protected HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > > createProjector()
			{
				final ArrayList< AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet > > mappings =
						new ArrayList< AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet > >();
				
				for ( int i = 0; i < sources.size(); ++i )
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
							new Interpolant< VolatileRealType< UnsignedByteType >, RandomAccessible< VolatileRealType< UnsignedByteType > > >( sources.get( i ), interpolatorFactory );
					final AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet > mapping =
							new AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet >( interpolant, sourceToScreens.get( i ).inverse() );
					mappings.add( mapping );
				}
				
				final HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > > p =
						new HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > >( mappings, screenImage, converter );
				p.clear();
				return p;
			}
			
			/**
			 * There is no single sourceTransform in a hierarchy.
			 */
			@Override
			public void setSourceTransform( final AffineTransform3D transform ) {}
			
			@Override
			public void transformChanged( final AffineTransform3D transform )
			{
				synchronized( viewerTransform )
				{
					viewerTransform.set( transform );
				}
				( ( HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > > )projector ).clearMask();
			}
		};
	}

}
