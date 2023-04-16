package net.imglib2.blocks;

import java.util.Arrays;
import java.util.function.Supplier;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;

class PrimitiveBlocksUtils
{
	static < T extends NativeType< T > > PrimitiveBlocks< T > threadSafe( final Supplier< PrimitiveBlocks< T > > supplier )
	{
		final ThreadLocal< PrimitiveBlocks< T > > tl = ThreadLocal.withInitial( supplier );
		return new PrimitiveBlocks< T >()
		{
			@Override
			public T getType()
			{
				return tl.get().getType();
			}

			@Override
			public void copy( final long[] srcPos, final Object dest, final int[] size )
			{
				tl.get().copy( srcPos, dest, size );
			}

			@Override
			public PrimitiveBlocks< T > threadSafe()
			{
				return this;
			}
		};
	}

	static < T extends NativeType< T > > Object extractOobValue( final T type, final Extension extension )
	{
		if ( extension.type() == Extension.Type.CONSTANT )
		{
			final T oobValue = ( ( ExtensionImpl.ConstantExtension< T > ) extension ).getValue();
			final ArrayImg< T, ? > img = new ArrayImgFactory<>( type ).create( 1 );
			img.firstElement().set( oobValue );
			return ( ( ArrayDataAccess< ? > ) ( img.update( null ) ) ).getCurrentStorageArray();
		}
		else
			return null;
	}

	// TODO replace with ra.getType() when that is available in imglib2 core
	static < T extends Type< T > > T getType( RandomAccessible< T > ra )
	{
		final Point p = new Point( ra.numDimensions() );
		if ( ra instanceof Interval )
			( ( Interval ) ra ).min( p );
		return ra.getAt( p ).createVariable();
	}

	/**
	 * Computes the inverse of (@code transform}. The {@code MixedTransform
	 * transform} is a pure axis permutation followed by inversion of some axes,
	 * that is
	 * <ul>
	 * <li>{@code numSourceDimensions == numTargetDimensions},</li>
	 * <li>the translation vector is zero, and</li>
	 * <li>no target component is zeroed out.</li>
	 * </ul>
	 * The computed inverse {@code MixedTransform} concatenates with {@code transform} to identity.
	 * @return the inverse {@code MixedTransform}
	 */
	static MixedTransform invPermutationInversion( MixedTransform transform )
	{
		final int n = transform.numTargetDimensions();
		final int[] component = new int[ n ];
		final boolean[] invert = new boolean[ n ];
		final boolean[] zero = new boolean[ n ];
		transform.getComponentMapping( component );
		transform.getComponentInversion( invert );
		transform.getComponentZero( zero );

		final int m = transform.numSourceDimensions();
		final int[] invComponent = new int[ m ];
		final boolean[] invInvert = new boolean[ m ];
		final boolean[] invZero = new boolean[ m ];
		Arrays.fill( invZero, true );
		for ( int i = 0; i < n; i++ )
		{
			if ( transform.getComponentZero( i ) == false )
			{
				final int j = component[ i ];
				invComponent[ j ] = i;
				invInvert[ j ] = invert[ i ];
				invZero[ j ] = false;
			}
		}
		MixedTransform invTransform = new MixedTransform( n, m );
		invTransform.setComponentMapping( invComponent );
		invTransform.setComponentInversion( invInvert );
		invTransform.setComponentZero( invZero );
		return invTransform;
	}

	/**
	 * Split {@code transform} into
	 * <ol>
	 * <li>{@code permuteInvert}, a pure axis permutation followed by inversion of some axes, and</li>
	 * <li>{@code remainder}, a remainder transformation,</li>
	 * </ol>
	 * such that {@code remainder * permuteInvert == transform}.
	 *
	 * @param transform transform to decompose
	 * @return {@code MixedTransform[]} array of {@code {permuteInvert, remainder}}
	 */
	static MixedTransform[] split( MixedTransform transform )
	{
		final int n = transform.numTargetDimensions();
		final int[] component = new int[ n ];
		final boolean[] invert = new boolean[ n ];
		final boolean[] zero = new boolean[ n ];
		final long[] translation = new long[ n ];
		transform.getComponentMapping( component );
		transform.getComponentInversion( invert );
		transform.getComponentZero( zero );
		transform.getTranslation( translation );

		final int m = transform.numSourceDimensions();
		final int[] splitComponent = new int[ m ];
		final boolean[] splitInvert = new boolean[ m ];

		int j = 0;
		for ( int i = 0; i < n; i++ )
		{
			if ( !zero[ i ] )
			{
				splitComponent[ j ] = component[ i ];
				splitInvert[ j ] = invert[ i ];
				component[ i ] = j++;
			}
		}

		final MixedTransform permuteInvert = new MixedTransform( m, m );
		permuteInvert.setComponentMapping( splitComponent );
		permuteInvert.setComponentInversion( splitInvert );

		final MixedTransform remainder = new MixedTransform( m, n );
		remainder.setComponentMapping( component );
		remainder.setComponentZero( zero );
		remainder.setTranslation( translation );

		return new MixedTransform[] { permuteInvert, remainder };
	}
}
