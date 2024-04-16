/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.display.projector.specialized;

import net.imglib2.display.projector.AbstractProjector2D;
import net.imglib2.display.screenimage.awt.UnsignedByteAWTScreenImage;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.GenericByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.IntervalIndexer;

/**
 * Fast implementation of a {@link AbstractProjector2D} that selects a 2D data
 * plain from an ByteType ArrayImg. The map method implements a normalization
 * function. The resulting image is a ByteType ArrayImg.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * 
 * @param <A>
 *            source
 */
public class ArrayImgXYByteProjector< A extends GenericByteType< A >> extends AbstractProjector2D
{

	private final byte[] sourceArray;

	private final byte[] targetArray;

	private final double min;

	private final double normalizationFactor;

	private final boolean isSigned;

	private final long[] dims;

	/**
	 * Normalizes an ArrayImg and writes the result into target. This can be
	 * used in conjunction with {@link UnsignedByteAWTScreenImage} for direct
	 * displaying. The normalization is based on a normalization factor and a
	 * minimum value with the following dependency:<br>
	 * <br>
	 * normalizationFactor = (typeMax - typeMin) / (newMax - newMin) <br>
	 * min = newMin <br>
	 * <br>
	 * A value is normalized by: normalizedValue = (value - min) *
	 * normalizationFactor.<br>
	 * Additionally the result gets clamped to the type range of target (that
	 * allows playing with saturation...).
	 * 
	 * @param source
	 *            Signed/Unsigned input data
	 * @param target
	 *            Unsigned output
	 * @param normalizationFactor
	 * @param min
	 */
	public ArrayImgXYByteProjector( final ArrayImg< A, ByteArray > source, final ArrayImg< UnsignedByteType, ByteArray > target, final double normalizationFactor, final double min )
	{
		super( source.numDimensions() );

		this.isSigned = source.firstElement().getMinValue() < 0;
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
		final long[] tmpPos = position.clone();
		tmpPos[ 0 ] = 0;
		tmpPos[ 1 ] = 0;

		offset = ( int ) IntervalIndexer.positionToIndex( tmpPos, dims );

		// copy the selected part of the source array (e.g. a xy plane at time t
		// in a video) into the target array.
		System.arraycopy( sourceArray, offset, targetArray, 0, targetArray.length );

		if ( isSigned )
		{
			for ( int i = 0; i < targetArray.length; i++ )
			{
				// -128 => 0 && 127 => 255 => unsigned byte
				//
				// -128_byte = 11111111_byte => convert => -128_int
				// (-128_int - 128_int) => calculate => -256_int (11..100000000)
				// => convert (11..1|00000000) => 0_unsignedByte
				//
				// 127_byte => convert => 127_int
				// (127_int - 128_int) => calculate => -1_int (11..111111111) =>
				// convert (11..1|11111111) => 255_unsignedByte
				targetArray[ i ] = ( byte ) ( targetArray[ i ] - 0x80 );
			}
			// old min + 128 => unsigned byte minimum
			minCopy += 0x80;
		}

		//
		// target[] contains now unsigned values
		//

		if ( normalizationFactor != 1 )
		{
			for ( int i = 0; i < targetArray.length; i++ )
			{
				// | ensure 0 <= x <= 255 |
				// | | calculate newValue: x
				// | | unsigned_byte => int
				// | | value - min * normalizationFactor
				targetArray[ i ] = ( byte ) Math.min( 255, Math.max( 0, ( Math.round( ( ( targetArray[ i ] & 0xFF ) - minCopy ) * normalizationFactor ) ) ) );
			}
		}
	}

}
