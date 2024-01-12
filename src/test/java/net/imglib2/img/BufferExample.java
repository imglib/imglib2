package net.imglib2.img;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Set;
import net.imglib2.Dimensions;
import net.imglib2.blocks.PrimitiveBlocks;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.AccessFlags;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.nio.BufferAccess;
import net.imglib2.img.basictypeaccess.nio.ByteBufferAccess;
import net.imglib2.img.basictypeaccess.nio.CharBufferAccess;
import net.imglib2.img.basictypeaccess.nio.DoubleBufferAccess;
import net.imglib2.img.basictypeaccess.nio.FloatBufferAccess;
import net.imglib2.img.basictypeaccess.nio.IntBufferAccess;
import net.imglib2.img.basictypeaccess.nio.LongBufferAccess;
import net.imglib2.img.basictypeaccess.nio.ShortBufferAccess;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.PrimitiveType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;

import static net.imglib2.img.basictypeaccess.AccessFlags.DIRTY;

public class BufferExample
{
	public static void main( String[] args )
	{
		final long[] dim = { 100, 100 };
		final IntType type = new IntType();
		final ArrayImgFactory< IntType > factory = new ArrayImgFactory<>( type );
		final ArrayImg< IntType, ? > intsWithArray = factory.create( dim );
		final ArrayImg< IntType, ? > intsWithBuffer = create( dim, type, type.getNativeTypeFactory() );

//		System.out.println( "intsWithArray = " + intsWithArray );
//		System.out.println( "intsWithBuffer = " + intsWithBuffer );

		Random random = new Random( 1L );
		intsWithArray.forEach( t -> t.set( random.nextInt( 100 ) ) );

//		System.out.println( "intsWithArray:\n" + toString( intsWithArray ) );
//		System.out.println( "intsWithBuffer:\n" + toString( intsWithBuffer ) );

		LoopBuilder.setImages( intsWithBuffer, intsWithArray ).forEachPixel( ( o, i ) -> o.set( i.get() ) );

//		System.out.println( "intsWithArray:\n" + toString( intsWithArray ) );
//		System.out.println( "intsWithBuffer:\n" + toString( intsWithBuffer ) );

		final long[] srcPos = { 20, 10 };
		final int[] size = { 3, 3 };
		final int[] dest = new int[ ( int ) Intervals.numElements( size ) ];

		PrimitiveBlocks.of( intsWithArray ).copy( srcPos, dest, size );
		System.out.println( "dest:\n" + toString( ArrayImgs.ints( dest, size[ 0 ], size[ 1 ] ) ) );

		PrimitiveBlocks.of( intsWithBuffer ).copy( srcPos, dest, size );
		System.out.println( "dest:\n" + toString( ArrayImgs.ints( dest, size[ 0 ], size[ 1 ] ) ) );

		System.out.println( "isBufferBacked( intsWithArray ) = " + isBufferBacked( intsWithArray ) );
		System.out.println( "isBufferBacked( intsWithBuffer ) = " + isBufferBacked( intsWithBuffer ) );
	}


	static < T extends NativeType< T >, A > boolean isBufferBacked( NativeImg< T, A > img )
	{
		return getDataAccess( img ) instanceof BufferAccess;
	}

	/*
	 * TODO: There should be a better way to get straight to a DataAccess instance.
	 *       This is similar to the getType() problem.
	 *       For now, this will work.
	 *       Make an issue about getDataAccess() ...
	 */

	static < T extends NativeType< T >, A > A getDataAccess( NativeImg< T, A > img )
	{
		return img.update( img.cursor() );
	}

	static String toString( Img< IntType > img )
	{
		final StringBuilder sb = new StringBuilder();
		final int sx = ( int ) img.dimension( 0 );
		final int sy = ( int ) img.dimension( 1 );
		for ( int y = 0; y < sy; ++y )
		{
			for ( int x = 0; x < sx; ++x )
			{
				sb.append( img.getAt( x, y ).get() );
				if ( x < sx - 1 )
					sb.append( ", " );
			}
			if ( y < sy - 1 )
				sb.append( "\n" );
		}
		return sb.toString();
	}

	/*
	 * TODO: Generics below lie less in the code below than in ArrayDataAccessFactory/ArrayImgFactory.
	 *       Adopt this scheme there as well.
	 */

	private static < T extends NativeType< T >, A extends ArrayDataAccess< A > > ArrayImg< T, A > create(
			final long[] dimensions,
			final T type,
			final NativeTypeFactory< T, ? super A > typeFactory )
	{
		Dimensions.verify( dimensions );
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final int numEntities = ArrayImgFactory.numEntitiesRangeCheck( dimensions, entitiesPerPixel );
		final A access = BufferDataAccessFactory.get( typeFactory );
		final A data = access.createArray( numEntities );
		final ArrayImg< T, A > img = new ArrayImg<>( data, dimensions, entitiesPerPixel );
		img.setLinkedType( typeFactory.createLinkedType( img ) );
		return img;
	}


	public static class BufferDataAccessFactory
	{
		public static < T extends NativeType< T >, A extends ArrayDataAccess< A > > A get(
				final T type )
		{
			return get( type, AccessFlags.setOf() );
		}

		public static < T extends NativeType< T >, A extends ArrayDataAccess< A > > A get(
				final T type,
				final Set< AccessFlags > flags )
		{
			return get( type.getNativeTypeFactory().getPrimitiveType(), flags );
		}

		public static < A extends ArrayDataAccess< A > > A get(
				final NativeTypeFactory< ?, ? > typeFactory )
		{
			return get( typeFactory.getPrimitiveType(), AccessFlags.setOf() );
		}

		public static < A extends ArrayDataAccess< A > > A get(
				final NativeTypeFactory< ?, ? > typeFactory,
				final Set< AccessFlags > flags )
		{
			return get( typeFactory.getPrimitiveType(), flags );
		}

		@SuppressWarnings( "unchecked" )
		public static < A extends ArrayDataAccess< A > > A get(
				final PrimitiveType primitiveType,
				final Set< AccessFlags > flags )
		{
			final boolean dirty = flags.contains( DIRTY );
			if ( dirty )
				throw new UnsupportedOperationException( "TODO: implement DirtyByteBufferAccess etc." );
			final ByteBuffer buf = ByteBuffer.allocateDirect( 8 );
			switch ( primitiveType )
			{
			case BOOLEAN:
				throw new UnsupportedOperationException( "TODO: so far, no Boolean BufferAccess exists." );
			case BYTE:
				return ( A ) ByteBufferAccess.fromByteBuffer( buf, true );
			case CHAR:
				return ( A ) CharBufferAccess.fromByteBuffer( buf, true );
			case DOUBLE:
				return ( A ) DoubleBufferAccess.fromByteBuffer( buf, true );
			case FLOAT:
				return ( A ) FloatBufferAccess.fromByteBuffer( buf, true );
			case INT:
				return ( A ) IntBufferAccess.fromByteBuffer( buf, true );
			case LONG:
				return ( A ) LongBufferAccess.fromByteBuffer( buf, true );
			case SHORT:
				return ( A ) ShortBufferAccess.fromByteBuffer( buf, true );
			default:
				throw new IllegalArgumentException();
			}
		}
	}
}