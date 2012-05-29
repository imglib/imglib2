/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.type.numeric.integer;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;

/**
 * A {@link Type} with arbitrary bit depth up to maximum 64 bits.
 * The behavior beyond 64 bits is undefined.
 * 
 * The performance of this type is traded off for the gain in memory storage.
 * The {@link #set(long)} operation takes have the time as the {@link #get} operation.
 * The performance may degrade very slightly with increasing bit depth, but the decrease is barely noticeable.
 *
 * @author Albert Cardona
 */
public class UnsignedBit64Type extends AbstractIntegerType<UnsignedBit64Type> implements NativeType<UnsignedBit64Type>
{
	private int i = 0;

	final protected NativeImg<UnsignedBit64Type, ? extends LongAccess> img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;
	
	// the number of bits per pixel
	private final int nBits;

	// A mask for bit and, containing nBits of 1
	private final long mask;
	
	// The inverse of mask
	private final long invMask;

	// this is the constructor if you want it to read from an array
	public UnsignedBit64Type(
			final NativeImg<UnsignedBit64Type,
			? extends LongAccess> bitStorage,
			final int nBits)
	{
		if (nBits < 1 || nBits > 64)
			throw new IllegalArgumentException("Supports only bit depths between 1 and 64, can't take " + nBits);
		img = bitStorage;
		this.nBits = nBits;
		this.mask = ((long)(Math.pow(2, nBits) -1));
		this.invMask = ~mask;
		updateIndex( 0 );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedBit64Type( final long value, final int nBits )
	{
		this( (NativeImg<UnsignedBit64Type, ? extends LongAccess>)null, nBits );
		updateIndex( 0 );
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public UnsignedBit64Type( final LongAccess access, final int nBits )
	{
		this( (NativeImg<UnsignedBit64Type, ? extends LongAccess>)null, nBits );
		updateIndex( 0 );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public UnsignedBit64Type( final int nBits ) { this( 0, nBits ); }

	@Override
	public NativeImg<UnsignedBit64Type, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<UnsignedBit64Type> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<UnsignedBit64Type, ? extends LongAccess> container = storageFactory.createLongInstance( dim, 64 / nBits + (0 == 64 % nBits ? 0 : 1) );

		// create a Type that is linked to the container
		final UnsignedBit64Type linkedType = new UnsignedBit64Type( container, nBits );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer( final Object c ) { dataAccess = img.update( c ); }

	@Override
	public UnsignedBit64Type duplicateTypeOnSameNativeImg() { return new UnsignedBit64Type( img, nBits ); }

	public long get() {
		final int k = i * nBits;
		final int i1 = k >>> 6; // k / 64;
		final long shift = k % 64;
		final long antiShift = 64 - shift;
		if (0 == shift) {
			// Number contained in a single long, ending exactly at the first bit
			return dataAccess.getValue(i1) & mask;
		} else {
			if (antiShift < nBits) {
				// Number split between two adjacent long
				final long v1 = (dataAccess.getValue(i1) >>> shift) & (mask >>> (nBits - antiShift)); // lower part, stored at the upper end
				final long v2 = (dataAccess.getValue(i1 + 1) & (mask >>> antiShift)) << antiShift; // upper part, stored at the lower end
				return v1 | v2;
			} else {
				// Number contained inside a single long
				return (dataAccess.getValue(i1) >>> shift) & mask;
			}
		}
	}

	// Crops value to within mask
	public void set( final long value ) {
		final int k = i * nBits;
		final int i1 = k >>> 6; // k / 64;
		final long shift = k % 64;
		final long antiShift = 64 - shift;
		final long safeValue = value & mask;
		if (0 == shift) {
			// Number contained in a single long, ending exactly at the first bit
			dataAccess.setValue(i1, (dataAccess.getValue(i1) & invMask) | safeValue);
		} else {
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

	@Override
	public int getInteger() { return (int)get(); }

	@Override
	public long getIntegerLong() { return get(); }

	@Override
	public void setInteger( final int f ) { set( f ); }

	@Override
	public void setInteger( final long f ) { set( f ); }

	/** The maximum value that can be stored is {@code Math.pow(2, nBits) -1}. */
	@Override
	public double getMaxValue() { return Math.pow(2, nBits) -1; }
	@Override
	public double getMinValue()  { return 0; }

	@Override
	public int getIndex() { return i; }

	@Override
	public void updateIndex( final int index )
	{
		i = index;
	}

	@Override
	public void incIndex()
	{
		++i;
	}
	@Override
	public void incIndex( final int increment )
	{
		i += increment;
	}
	@Override
	public void decIndex()
	{
		--i;
	}
	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
	}

	@Override
	public UnsignedBit64Type createVariable(){ return new UnsignedBit64Type( nBits ); }

	@Override
	public UnsignedBit64Type copy(){ return new UnsignedBit64Type( get(), nBits ); }

	@Override
	public int getEntitiesPerPixel() { return 1; }

	@Override
	public int getBitsPerPixel() { return nBits; }

	@Override
	public void inc() {
		set(get() + 1);
	}

	@Override
	public void dec() {
		set(get() - 1);
	}

	@Override
	public void add(final UnsignedBit64Type t) {
		set(get() + t.get());
	}

	@Override
	public void sub(final UnsignedBit64Type t) {
		set(get() - t.get());
	}

	@Override
	public void mul(final UnsignedBit64Type t) {
		set(get() * t.get());
	}

	@Override
	public void div(final UnsignedBit64Type t) {
		set(get() / t.get());
	}
}