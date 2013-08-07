/**
 * License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package interactive;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import interactive.remote.openconnectome.VolatileOpenConnectomeRandomAccessibleInterval;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.Projector;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.Volatile;
import net.imglib2.display.VolatileRealType;
import net.imglib2.display.VolatileRealTypeARGBConverter;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * {@link Projector} for a hierarchy of {@link Volatile} inputs.  After each
 * {@link #map()} call, the projector has a {@link #isValid() state} that
 * signalizes whether all projected pixels were valid.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class HierarchyProjector< T, A extends Volatile< T > > extends Point implements Projector< A, ARGBType >
{
	final protected ArrayList< RandomAccessible< A > > sources = new ArrayList< RandomAccessible< A > >();
	final protected Converter< ? super A, ARGBType > converter;
	final protected long[] min;
	final protected long[] max;
	final protected ARGBScreenImage target;
	final protected ArrayImg< IntType, IntArray > mask;
	protected boolean valid = false;
	int s = 0;
	
	public HierarchyProjector(
			final List< ? extends RandomAccessible< A > > sources,
			final ARGBScreenImage target,
			final Converter< ? super A, ARGBType > converter )
	{
		super( Math.max( 2, sources.get( 0 ).numDimensions() ) );

		this.sources.addAll( sources );
		s = sources.size();
		this.converter = converter;
	
		// as this is an XY projector, we need at least two dimensions,
		// even if the source is one-dimensional
		min = new long[ n ];
		max = new long[ n ];
		this.target = target;
		
		mask = ArrayImgs.ints( target.dimension( 0 ), target.dimension( 1 ) );
	}
	
	public void setSources( final List< RandomAccessible< A > > sources )
	{
		synchronized ( this.sources )
		{
			this.sources.addAll( sources );
			s = sources.size();
		}
	}
	
	/**
	 * @return true if all mapped pixels were {@link Volatile#isValid() valid}.
	 */
	public boolean isValid()
	{
		return valid;
	}
	
	/**
	 * Set all pixels in target to 100% transparent zero, and mask to all
	 * Integer.MAX_VALUE.
	 */
	public void clear()
	{
		final ArrayCursor< ARGBType > targetCursor = target.cursor();
		final ArrayCursor< IntType > maskCursor = mask.cursor();
		
		/* Despite the extra cmparison, is consistently 60% faster than
		 * while ( targetCursor.hasNext() )
		 * {
		 *	targetCursor.next().set( 0x00000000 );
		 *	maskCursor.next().set( Integer.MAX_VALUE );
		 * }
		 * because it exploits CPU caching better.
		 */
		while ( targetCursor.hasNext() )
			targetCursor.next().set( 0x00000000 );
		while ( maskCursor.hasNext() )
			maskCursor.next().set( Integer.MAX_VALUE );
		
		s = sources.size();
	}
	
	/**
	 * Set all pixels in target to 100% transparent zero, and mask to all
	 * Integer.MAX_VALUE.
	 */
	public void clearMask()
	{
		final ArrayCursor< IntType > maskCursor = mask.cursor();
		
		while ( maskCursor.hasNext() )
			maskCursor.next().set( Integer.MAX_VALUE );
		
		s = sources.size();
	}
	
	@Override
	public void map()
	{
		System.out.println( "Mapping " + s + " levels." );
		for ( int d = 2; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		max[ 0 ] = target.max( 0 );
		max[ 1 ] = target.max( 1 );
		final FinalInterval sourceInterval = new FinalInterval( min, max );

		final long width = target.dimension( 0 );
		final long height = target.dimension( 1 );
		final long cr = -width;
		
		final ArrayRandomAccess< ARGBType > targetRandomAccess = target.randomAccess( target );
		final ArrayRandomAccess< IntType > maskRandomAccess = mask.randomAccess( target );
		
		int i;
		
		valid = false;
		
		synchronized ( this.sources )
		{
			for ( i = 0; i < s && !valid; ++i )
			{
				valid = true;
				
				final RandomAccess< A > sourceRandomAccess = sources.get( i ).randomAccess( sourceInterval );
				sourceRandomAccess.setPosition( min );
				targetRandomAccess.setPosition( min[ 0 ], 0 );
				targetRandomAccess.setPosition( min[ 1 ], 1 );
				maskRandomAccess.setPosition( min[ 0 ], 0 );
				maskRandomAccess.setPosition( min[ 1 ], 1 );
			
				for ( long y = 0; y < height; ++y )
				{
					for ( long x = 0; x < width; ++x )
					{
						final IntType m = maskRandomAccess.get();
						if ( m.get() > i )
						{
							final A a = sourceRandomAccess.get();
							final boolean v = a.isValid();
							if ( v )
							{
								converter.convert( a, targetRandomAccess.get() );
								m.set( i );
							}
							valid &= v;
						}
						sourceRandomAccess.fwd( 0 );
						targetRandomAccess.fwd( 0 );
						maskRandomAccess.fwd( 0 );
					}
					sourceRandomAccess.move( cr, 0 );
					targetRandomAccess.move( cr, 0 );
					maskRandomAccess.move( cr, 0 );
					sourceRandomAccess.fwd( 1 );
					targetRandomAccess.fwd( 1 );
					maskRandomAccess.fwd( 1 );
				}
			}
			if ( valid )
				s = i - 1;
			valid = s == 1;
		}
	}
	
	final public void draw()
	{
		final ImagePlus imp = new ImagePlus( "test", new ColorProcessor( ( int )target.dimension( 0 ), ( int )target.dimension( 1 ) ) );
		imp.show();
		
		long t, nTrials = 0;
		while ( s > 0 )
		{
			t = System.currentTimeMillis();
			map();
			System.out.println( "trial " + ( ++nTrials ) + ": s = " + s + " took " + ( System.currentTimeMillis() - t ) + "ms" );
			
			imp.setImage( target.image() );
		}
	}
	
	final static public void benchmark()
	{
		final HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > > projector =
				new HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > >(
						new ArrayList< RandomAccessible< VolatileRealType< UnsignedByteType > > >(), new ARGBScreenImage( 1024, 768 ), new RealARGBConverter< VolatileRealType< UnsignedByteType> >() );
		
		long t;
		
		for ( int i = 0; i < 10; ++i )
		{
			t = System.currentTimeMillis();
			for ( int d = 0; d < 1000; ++d )
				projector.clear();
			t = System.currentTimeMillis() - t;
			System.out.println( "clearTarget: " + t + "ms" );
			
		}
	}
	
	final static public void main( final String... args )
	{
		new ImageJ();
		
		final int[][] levelDimensions = new int[][]{
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

		final int[][] levelCellDimensions = new int[][]{
				{ 50, 50, 5 },
				{ 50, 50, 10 },
				{ 50, 50, 20 },
				{ 50, 50, 40 },
				{ 20, 20, 20 },
				{ 20, 20, 20 },
				{ 20, 20, 20 },
				{ 20, 20, 20 } };
		
		final VolatileOpenConnectomeRandomAccessibleInterval[] sources = new VolatileOpenConnectomeRandomAccessibleInterval[ levelScales.length ];
		final ArrayList< RandomAccessible< VolatileRealType< UnsignedByteType > > > transformedSources = new ArrayList< RandomAccessible< VolatileRealType< UnsignedByteType > > >();

		final AffineTransform3D[] sourceTransforms = new AffineTransform3D[ levelScales.length ];

		for ( int level = 0; level < levelScales.length; level++ )
		{
			final AffineTransform3D levelTransform = new AffineTransform3D();
			for ( int d = 0; d < 3; ++d )
			{
				levelTransform.set( levelScales[ level ][ d ], d, d );
				//levelTransform.set( 0.5 * ( levelScales[ level ][ d ] - 1 ), d, 3 );
			}
			levelTransform.set( -0.5 * levelScales[ level ][ 0 ], 0, 3 );
			levelTransform.set( -0.5 * levelScales[ level ][ 1 ], 1, 3 );
			
			sourceTransforms[ level ] = levelTransform;
			
			System.out.println( sourceTransforms[ level ] );
			
			sources[ level ] = new VolatileOpenConnectomeRandomAccessibleInterval(
					"http://openconnecto.me/emca/kasthuri11",
					levelDimensions[ level ][ 0 ],
					levelDimensions[ level ][ 1 ],
					levelDimensions[ level ][ 2 ],
					levelCellDimensions[ level ][ 0 ],
					levelCellDimensions[ level ][ 1 ],
					levelCellDimensions[ level ][ 2 ],
					1, level );
			
			/* transform sources */
			final AffineTransform3D transform = new AffineTransform3D();
//			transform.set(
//					0.25, 0, 0, -21504 / 2.0 * 0.25,
//					0, 0.25, 0, -26624 / 2.0 * 0.25,
//					0, 0, 0.25, -18500 / 2.0 * 0.25 );
			transform.set(
					1.0, 0, 0, -21504 / 2.0,
					0, 1.0, 0, -26624 / 2.0,
					0, 0, 1.0, -18500 / 2.0 );
			
			transform.concatenate( levelTransform );
			
//			final int interpolation = 0;
			final int interpolation = 1;
			
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
					new Interpolant< VolatileRealType< UnsignedByteType >, RandomAccessible< VolatileRealType< UnsignedByteType > > >( sources[ level ], interpolatorFactory );
			final AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet > mapping =
					new AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet >( interpolant, transform.inverse() );
			transformedSources.add( mapping );
		}
		
		final HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > > projector =
				new HierarchyProjector< UnsignedByteType, VolatileRealType< UnsignedByteType > >( transformedSources, new ARGBScreenImage( 800, 600 ), new VolatileRealTypeARGBConverter( 0, 255 ) );
		
		projector.clear();
		projector.draw();
	}
}
