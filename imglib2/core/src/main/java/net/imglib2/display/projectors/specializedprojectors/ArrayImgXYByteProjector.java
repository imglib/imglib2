package net.imglib2.display.projectors.specializedprojectors;

import net.imglib2.display.projectors.Abstract2DProjector;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.GenericByteType;
import net.imglib2.util.IntervalIndexer;

/**
 * Fast implementation of a {@link Abstract2DProjector} that selects a 2D data plain from an ByteType ArrayImg. The map method implements
 * a normalization function. The resulting image is a ByteType ArrayImg.* 
 *  
 * @author zinsmaie
 *
 * @param <A>
 * @param <B>
 */
public class ArrayImgXYByteProjector< A extends GenericByteType< A >, B extends GenericByteType< B >> extends Abstract2DProjector< A, B >
{

	private final byte[] sourceArray;

	private final byte[] targetArray;

	private final double min;

	private final double normalizationFactor;

	private final boolean isSigned;

	private final long[] dims;

	public ArrayImgXYByteProjector( ArrayImg< A, ByteArray > source, ArrayImg< B, ByteArray > target, double normalizationFactor, double min, boolean isSigned )
	{
		super( source.numDimensions() );

		this.isSigned = isSigned;
		this.targetArray = target.update( null ).getCurrentStorageArray();
		this.normalizationFactor = normalizationFactor;
		this.min = min;
		this.dims = new long[ numDimensions ];
		source.dimensions( dims );

		sourceArray = source.update( null ).getCurrentStorageArray();

		assert ( sourceArray.length != this.targetArray.length );
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
				targetArray[ i ] = ( byte ) ( targetArray[ i ] - 0x80 );
			}
			minCopy += 0x80;
		}
		if ( normalizationFactor != 1 )
		{
			int max = 2 * Byte.MAX_VALUE + 1;
			for ( int i = 0; i < targetArray.length; i++ )
			{
				targetArray[ i ] = ( byte ) Math.min( max, Math.max( 0, ( Math.round( ( ( ( byte ) ( targetArray[ i ] + 0x80 ) ) + 0x80 - minCopy ) * normalizationFactor ) ) ) );

			}
		}
	}

}
