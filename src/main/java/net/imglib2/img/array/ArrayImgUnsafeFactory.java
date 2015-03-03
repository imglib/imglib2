package net.imglib2.img.array;

import net.imglib2.Dimensions;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.basictypeaccess.unsafe.ByteUnsafeAccess;
import net.imglib2.img.basictypeaccess.unsafe.FloatUnsafeAccess;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;

public class ArrayImgUnsafeFactory< T extends NativeType<T> > extends NativeImgFactory< T >
{
	@Override
	public ArrayImg< T, ? > create( final long[] dim, final T type )
	{
		return ( ArrayImg< T, ? > ) type.createSuitableNativeImg( this, dim );
	}

	@Override
	public  ArrayImg< T, ? > create( final Dimensions dim, final T type )
	{
		final long[] size = new long[ dim.numDimensions() ];
		dim.dimensions( size );
		return create( size, type );
	}

	public static int numEntitiesRangeCheck( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final long numEntities = entitiesPerPixel.mulCeil( AbstractImg.numElements( dimensions ) );

		if ( numEntities > Integer.MAX_VALUE )
			throw new RuntimeException( "Number of elements in Container too big, use for example CellContainer instead: " + numEntities + " > " + Integer.MAX_VALUE );

		return ( int ) numEntities;
	}

	@Override
	public ArrayImg< T, ByteUnsafeAccess > createByteInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, ByteUnsafeAccess >( new ByteUnsafeAccess( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, CharArray> createCharInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayImg< T, DoubleArray > createDoubleInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayImg< T, FloatUnsafeAccess > createFloatInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, FloatUnsafeAccess >( new FloatUnsafeAccess( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, IntArray > createIntInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayImg< T, LongArray > createLongInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayImg< T, ShortArray > createShortInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) ) return new ArrayImgBufferFactory();
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
