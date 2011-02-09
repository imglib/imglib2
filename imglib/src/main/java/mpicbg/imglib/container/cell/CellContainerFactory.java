package mpicbg.imglib.container.cell;

import mpicbg.imglib.container.ImgFactory;
import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.BitAccess;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.CharAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.LongAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.container.basictypecontainer.array.BitArray;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.basictypecontainer.array.CharArray;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.container.basictypecontainer.array.LongArray;
import mpicbg.imglib.container.basictypecontainer.array.ShortArray;
import mpicbg.imglib.exception.IncompatibleTypeException;
import mpicbg.imglib.type.NativeType;

public class CellContainerFactory< T extends NativeType<T> > extends NativeContainerFactory< T >
{
	protected int[] defaultCellDimensions = { 10 };

	public CellContainerFactory()
	{
	}
	
	public CellContainerFactory( final int cellSize )
	{
		defaultCellDimensions[ 0 ] = cellSize;
	}

	public CellContainerFactory( final int[] cellDimensions )
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
	public NativeContainer< T, ? extends BitAccess > createBitInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellContainer< T, BitArray >( this, new BitArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ? extends ByteAccess > createByteInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellContainer< T, ByteArray >( this, new ByteArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ? extends CharAccess > createCharInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellContainer< T, CharArray >( this, new CharArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ? extends ShortAccess > createShortInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellContainer< T, ShortArray >( this, new ShortArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ? extends IntAccess > createIntInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellContainer< T, IntArray >( this, new IntArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ? extends LongAccess > createLongInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellContainer< T, LongArray >( this, new LongArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ? extends FloatAccess > createFloatInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellContainer< T, FloatArray >( this, new FloatArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ? extends DoubleAccess > createDoubleInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellContainer< T, DoubleArray >( this, new DoubleArray( 1 ), dimensions, cellSize, entitiesPerPixel );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <S> ImgFactory<S> imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new CellContainerFactory( defaultCellDimensions );
		else
			throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
