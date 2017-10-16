/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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


package net.imglib2.type;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;

/**
 * A {@link Type} with arbitrary bit depth up to maximum 64 bits.
 * The behavior beyond 64 bits is undefined.
 *
 * To set and get bits, we use longs. Therefore not more than 64 bits are supported. The long is not
 * supposed to have anything to do with math, it is simply an efficient way to hold an array of bits
 *
 * The performance of this type is traded off for the gain in memory storage.
 * The {@link #setBits(long)} operation takes have the time as the {@link #getBits} operation.
 * The performance may degrade very slightly with increasing bit depth, but the decrease is barely noticeable.
 *
 * @author Albert Cardona
 * @author Stephan Preibisch
 */
public abstract class AbstractBit64Type< T extends AbstractBit64Type< T > > extends AbstractBitType< T >
{
	// A mask for bit and, containing nBits of 1
	private final long mask;

	// The inverse of mask
	private final long invMask;

	// this is the constructor if you want it to read from an array
	public AbstractBit64Type( final NativeImg< ?, ? extends LongAccess > bitStorage, final int nBits )
	{
		super( bitStorage, nBits );

		if ( nBits < 1 || nBits > 64 )
			throw new IllegalArgumentException( "Supports only bit depths between 1 and 64, can't take " + nBits );
		else if ( nBits == 64 )
			this.mask = -1l; // all 1s
		else
			this.mask = ((long)(Math.pow(2, nBits) -1));
		this.invMask = ~mask;
	}

	// this is the constructor if you want it to be a variable
	public AbstractBit64Type( final long value, final int nBits )
	{
		this( ( NativeImg< ?, ? extends LongAccess > ) null, nBits );
		updateIndex( 0 );
		dataAccess = new LongArray( 1 );
		setBits( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public AbstractBit64Type( final LongAccess access, final int nBits )
	{
		this( ( NativeImg< ?, ? extends LongAccess > ) null, nBits );
		updateIndex( 0 );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public AbstractBit64Type( final int nBits ) { this( 0, nBits ); }

	/**
	 * Writes the current "subLong" location of the LongAccess into the lower nBits bits of the long value
	 *
	 * Note that "long value" does not refer to math, it is just a way to help to return arbitrary values. It
	 * is basically an array of bits.
	 *
	 * @return
	 */
	protected long getBits() {
		final long k = i * nBits;
		final int i1 = (int)(k >>> 6); // k / 64;
		final long shift = k & 63; // Same as k % 64;
		final long v = dataAccess.getValue(i1);
		if (0 == shift) {
			// Number contained in a single long, ending exactly at the first bit
			return v & mask;
		} else {
			final long antiShift = 64 - shift;
			if (antiShift < nBits) {
				// Number split between two adjacent long
				final long v1 = (v >>> shift) & (mask >>> (nBits - antiShift)); // lower part, stored at the upper end
				final long v2 = (dataAccess.getValue(i1 + 1) & (mask >>> antiShift)) << antiShift; // upper part, stored at the lower end
				return v1 | v2;
			} else {
				// Number contained inside a single long
				return (v >>> shift) & mask;
			}
		}
	}

	/**
	 * Sets the lower nBits bits of the long value into the current "subLong" location of the LongAccess
	 *
	 * Note that "long value" does not refer to math, it is just a way to help to set arbitrary values. It
	 * is basically an array of bits.
	 *
	 * @param value
	 */
	protected void setBits( final long value ) {
		final long k = i * nBits;
		final int i1 = (int)(k >>> 6); // k / 64;
		final long shift = k & 63; // Same as k % 64;
		final long safeValue = value & mask;
		synchronized ( dataAccess ) {
			if (0 == shift) {
				// Number contained in a single long, ending exactly at the first bit
				dataAccess.setValue(i1, (dataAccess.getValue(i1) & invMask) | safeValue);
			} else {
				final long antiShift = 64 - shift;
				final long v = dataAccess.getValue(i1);
				if (antiShift < nBits) {
					// Number split between two adjacent longs
					// 1. Store the lower bits of safeValue at the upper bits of v1
					final long v1 = (v & (0xffffffffffffffffL >>> antiShift)) // clear upper bits, keep other values
							| ((safeValue & (mask >>> (nBits - antiShift))) << shift); // the lower part of safeValue, stored at the upper end
					dataAccess.setValue(i1, v1);
					// 2. Store the upper bits of safeValue at the lower bits of v2
					final long v2 = (dataAccess.getValue(i1 + 1) & (0xffffffffffffffffL << (nBits - antiShift))) // other
							| (safeValue >>> antiShift); // upper part of safeValue, stored at the lower end
					dataAccess.setValue(i1 + 1, v2);
				} else {
					// Number contained inside a single long
					if (0 == v) {
						// Trivial case
						dataAccess.setValue(i1, safeValue << shift);
					} else {
						// Clear the bits first
						dataAccess.setValue(i1, (v & ~(mask << shift)) | (safeValue << shift));
					}
				}
			}
		}
	}
}
