package net.imglib2.display.projectors.specializedprojectors;

import net.imglib2.display.projectors.AbstractProjector2D;
import net.imglib2.display.projectors.screenimages.ByteScreenImage;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.GenericByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.IntervalIndexer;

/**
 * Fast implementation of a {@link AbstractProjector2D} that selects a 2D data
 * plain from a ByteType PlanarImg. The map method implements a normalization
 * function. The resulting image is a ByteType ArrayImg. *
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 * 
 * @param <A>
 */
public class PlanarImgXYByteProjector< A extends GenericByteType< A >> extends AbstractProjector2D< A, UnsignedByteType >
{

	private final PlanarImg< A, ByteArray > source;

	private final byte[] targetArray;

	private final double min;

	private final double normalizationFactor;

	private final boolean isSigned;

	private final long[] dims;

	/**
	 * Normalizes a PlanarImg and writes the result into target. This can be used in conjunction with {@link ByteScreenImage} for direct displaying.
	 * The normalization is based on a normalization factor and a minimum value with the following dependency:<br>
	 * <br>
	 * normalizationFactor = (typeMax - typeMin) / (newMax - newMin) <br>
	 * min = newMin <br>
	 * <br>
	 * A value is normalized by: normalizedValue = (value - min) * normalizationFactor.<br>
	 * Additionally the result gets clamped to the type range of target (that allows playing with saturation...).
	 *  
	 * @param source Signed/Unsigned input data
	 * @param target Unsigned output
	 * @param normalizationFactor
	 * @param min
	 */
	public PlanarImgXYByteProjector( PlanarImg< A, ByteArray > source, ArrayImg< UnsignedByteType, ByteArray > target, double normalizationFactor, double min)
	{
		super( source.numDimensions() );

		this.isSigned = (source.firstElement() instanceof ByteType);
		this.targetArray = target.update( null ).getCurrentStorageArray();
		this.normalizationFactor = normalizationFactor;
		this.min = min;
		this.dims = new long[ n ];
		source.dimensions( dims );

		this.source = source;
	}

	@Override
	public void map()
	{
		//more detailed documentation of the binary arithmetic can be found in ArrayImgXYByteProjector
		
		double minCopy = min;
		int offset = 0;

		// positioning for every call to map because the plane index is
		// position dependent
		int planeIndex;
		if ( position.length > 2 )
		{
			long[] tmpPos = new long[ position.length - 2 ];
			long[] tmpDim = new long[ position.length - 2 ];
			for ( int i = 0; i < tmpDim.length; i++ )
			{
				tmpPos[ i ] = position[ i + 2 ];
				tmpDim[ i ] = source.dimension( i + 2 );
			}
			planeIndex = ( int ) IntervalIndexer.positionToIndex( tmpPos, tmpDim );
		}
		else
		{
			planeIndex = 0;
		}

		byte[] sourceArray = source.update( new PlanarImgContainerSamplerImpl( planeIndex ) ).getCurrentStorageArray();

		// copy the selected part of the source array (e.g. a xy plane at time t
		// in a video) into the target array.
		System.arraycopy( sourceArray, offset, targetArray, 0, targetArray.length );

		if ( isSigned )
		{
			for ( int i = 0; i < targetArray.length; i++ )
			{
				// -128 => 0 && 127 => 255 => unsigned byte
				targetArray[ i ] = ( byte ) ( targetArray[ i ] - 0x80 );
			}
			// old min + 128 => unsigned byte minimum
			minCopy += 0x80;
		}
		if ( normalizationFactor != 1 )
		{
			for ( int i = 0; i < targetArray.length; i++ )
			{
				// normalizedValue = (oldValue - min) * normalizationFactor
				// clamped to 0 .. 255
				targetArray[ i ] = ( byte ) Math.min( 255, Math.max( 0, ( Math.round( ( (targetArray[i] & 0xFF) - minCopy ) * normalizationFactor ) ) ) );
			}
		}
	}

}
