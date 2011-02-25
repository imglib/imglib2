package mpicbg.imglib.img.cell;

import mpicbg.imglib.exception.IncompatibleTypeException;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.NativeImgFactory;
import mpicbg.imglib.img.basictypeaccess.BitAccess;
import mpicbg.imglib.img.basictypeaccess.ByteAccess;
import mpicbg.imglib.img.basictypeaccess.CharAccess;
import mpicbg.imglib.img.basictypeaccess.DoubleAccess;
import mpicbg.imglib.img.basictypeaccess.FloatAccess;
import mpicbg.imglib.img.basictypeaccess.IntAccess;
import mpicbg.imglib.img.basictypeaccess.LongAccess;
import mpicbg.imglib.img.basictypeaccess.ShortAccess;
import mpicbg.imglib.img.basictypeaccess.array.BitArray;
import mpicbg.imglib.img.basictypeaccess.array.ByteArray;
import mpicbg.imglib.img.basictypeaccess.array.CharArray;
import mpicbg.imglib.img.basictypeaccess.array.DoubleArray;
import mpicbg.imglib.img.basictypeaccess.array.FloatArray;
import mpicbg.imglib.img.basictypeaccess.array.IntArray;
import mpicbg.imglib.img.basictypeaccess.array.LongArray;
import mpicbg.imglib.img.basictypeaccess.array.ShortArray;
import mpicbg.imglib.type.NativeType;

public class CellImgFactory< T extends NativeType<T> > extends NativeImgFactory< T >
{
	protected int[] defaultCellDimensions = { 10 };

	public CellImgFactory()
	{
	}
	
	public CellImgFactory( final int cellSize )
	{
		defaultCellDimensions[ 0 ] = cellSize;
	}

	public CellImgFactory( final int[] cellDimensions )
	{
		if ( cellDimensions == null || cellDimensions.length == 0 )
		{
			System.err.println( "CellContainerFactory(): cellSize is null. Using equal cell size of " + defaultCellDimensions[0]);
			return;
		}

		for ( int i = 0; i < cellDimensions.length; i++ )
		{
			if ( cellDimensions[ i ] <= 0 )
			{
				System.err.println( "CellContainerFactory(): cell size in dimension " + i + " is <= 0, using a size of " + defaultCellDimensions[ 0 ] + "." );
				cellDimensions[ i ] = defaultCellDimensions[ 0 ];
			}
		}

		defaultCellDimensions = cellDimensions;
	}

	protected long[] checkDimensions( long dimensions[] )
	{
		if ( dimensions == null || dimensions.length == 0 )
		{
			System.err.println( "CellContainerFactory(): dimensionality is null. Creating a 1D cell with size 1." );
			dimensions = new long[] { 1 };
		}

		for ( int i = 0; i < dimensions.length; i++ )
		{
			if ( dimensions[ i ] <= 0 )
			{
				System.err.println( "CellContainerFactory(): size of dimension " + i + " is <= 0, using a size of 1." );
				dimensions[ i ] = 1;
			}
		}

		return dimensions;
	}

	protected int[] checkCellSize( int[] cellDimensions, long[] dimensions )
	{
		if ( cellDimensions == null )
		{
			cellDimensions = new int[ dimensions.length ];
			for ( int i = 0; i < cellDimensions.length; i++ )
				cellDimensions[ i ] = defaultCellDimensions[ ( i < defaultCellDimensions.length ) ? i : 0 ];
		}

		if ( cellDimensions.length != dimensions.length )
		{
			// System.err.println( "CellContainerFactory(): dimensionality of image is unequal to dimensionality of cells, adjusting cell dimensionality." );
			int[] cellDimensionsNew = new int[ dimensions.length ];

			for ( int i = 0; i < dimensions.length; i++ )
			{
				if ( i < cellDimensions.length )
					cellDimensionsNew[ i ] = cellDimensions[ i ];
				else
					cellDimensionsNew[ i ] = defaultCellDimensions[ ( i < defaultCellDimensions.length ) ? i : 0 ];
			}

			cellDimensions = cellDimensionsNew;
		}

		return cellDimensions;
	}

	@Override
	public CellImg< T, ? > create( final long[] dim, final T type )
	{
		return ( CellImg< T, ? > ) type.createSuitableNativeImg( this, dim );
	}

	@Override
	public CellImg< T, ? extends BitAccess > createBitInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, BitArray >( this, new BitArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public CellImg< T, ? extends ByteAccess > createByteInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, ByteArray >( this, new ByteArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public CellImg< T, ? extends CharAccess > createCharInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, CharArray >( this, new CharArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public CellImg< T, ? extends ShortAccess > createShortInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, ShortArray >( this, new ShortArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public CellImg< T, ? extends IntAccess > createIntInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, IntArray >( this, new IntArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public CellImg< T, ? extends LongAccess > createLongInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, LongArray >( this, new LongArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public CellImg< T, ? extends FloatAccess > createFloatInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, FloatArray >( this, new FloatArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public CellImg< T, ? extends DoubleAccess > createDoubleInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, DoubleArray >( this, new DoubleArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <S> ImgFactory<S> imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new CellImgFactory( defaultCellDimensions );
		else
			throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
