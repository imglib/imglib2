package net.imglib2.display.projectors.specializedprojectors;

import net.imglib2.display.projectors.Abstract2DProjector;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.numeric.integer.GenericShortType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.IntervalIndexer;

/**
 * Fast implementation of a {@link Abstract2DProjector} that selects a 2D data
 * plain from an ShortType ArrayImg. The map method implements a normalization
 * function. The resulting image is a ShortType ArrayImg. *
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 * 
 * @param <A>
 */
public class ArrayImgXYShortProjector< A extends GenericShortType< A >> extends Abstract2DProjector< A, UnsignedShortType >
{

	private final short[] sourceArray;

	private final short[] targetArray;

	private final double min;

	private final double normalizationFactor;

	private final boolean isSigned;

	private final long[] dims;

	public ArrayImgXYShortProjector( ArrayImg< A, ShortArray > source, ArrayImg< UnsignedShortType, ShortArray > target, double normalizationFactor, double min)
	{
		super( source.numDimensions() );

		this.isSigned = (source.firstElement() instanceof ShortType);
		this.targetArray = target.update( null ).getCurrentStorageArray();
		this.normalizationFactor = normalizationFactor;
		this.min = min;
		this.dims = new long[ n ];
		source.dimensions( dims );

		sourceArray = source.update( null ).getCurrentStorageArray();
	}

	@Override
	public void map()
	{
		double minCopy = min;
		int offset = 0;
		long[] tmpPos = position.clone();
		tmpPos[ 0 ] = 0;
		tmpPos[ 1 ] = 0;

		offset = ( int ) IntervalIndexer.positionToIndex( tmpPos, dims );

		System.arraycopy( sourceArray, offset, targetArray, 0, targetArray.length );

		if ( isSigned )
		{
			for ( int i = 0; i < targetArray.length; i++ )
			{
				targetArray[ i ] = ( short ) ( targetArray[ i ] - 0x8000 );
			}
			minCopy += 0x8000;
		}
		if ( normalizationFactor != 1 )
		{
			int max = 2 * Short.MAX_VALUE + 1;
			for ( int i = 0; i < targetArray.length; i++ )
			{
				targetArray[ i ] = ( short ) Math.min( max, Math.max( 0, ( Math.round( ( ( ( short ) ( targetArray[ i ] + 0x8000 ) ) + 0x8000 - minCopy ) * normalizationFactor ) ) ) );

			}
		}
	}

}
