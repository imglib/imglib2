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
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.NativeType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class UnsignedAnyBitType extends AbstractIntegerType<UnsignedAnyBitType> implements NativeType<UnsignedAnyBitType>
{
	private int i = 0;

	final protected NativeImg<UnsignedAnyBitType, ? extends BitAccess> img;

	// the adresses of the bits that we store
	final int[] j;
	final long[] jpow;

	// the DataAccess that holds the information
	protected BitAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public UnsignedAnyBitType( final NativeImg<UnsignedAnyBitType, ? extends BitAccess> bitStorage,
			final int nBits)
	{
		j = new int[ nBits ];
		jpow = new long[ nBits ];
		for (int k=0; k<nBits; ++k)
			jpow[k] = (long) Math.pow(2, k);

		img = bitStorage;
		updateIndex( 0 );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedAnyBitType( final long value, final int nBits )
	{
		this( (NativeImg<UnsignedAnyBitType, ? extends BitAccess>)null, nBits );
		updateIndex( 0 );
		dataAccess = new BitArray( nBits );
		setInteger( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public UnsignedAnyBitType( final BitAccess access, final int nBits )
	{
		this( (NativeImg<UnsignedAnyBitType, ? extends BitAccess>)null, nBits );
		updateIndex( 0 );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public UnsignedAnyBitType( final int nBits ) { this( 0, nBits ); }

	@Override
	public NativeImg<UnsignedAnyBitType, ? extends BitAccess> createSuitableNativeImg( final NativeImgFactory<UnsignedAnyBitType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<UnsignedAnyBitType, ? extends BitAccess> container = storageFactory.createBitInstance( dim, j.length );

		// create a Type that is linked to the container
		final UnsignedAnyBitType linkedType = new UnsignedAnyBitType( container, j.length );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer( final Object c ) { dataAccess = img.update( c ); }

	@Override
	public UnsignedAnyBitType duplicateTypeOnSameNativeImg() { return new UnsignedAnyBitType( img, j.length ); }

	public long get() {
		long value = 0;
		
		for (int k=0; k<j.length; ++k) {
			if ( dataAccess.getValue( j[k] ) ) value += jpow[k];
		}

		return value;
	}

	public void set( final long value ) {
		for (int k=0; k<j.length; ++k) {
			dataAccess.setValue( j[k], (value & jpow[k] ) == jpow[k] );
		}
	}	

	@Override
	public int getInteger() { return (int)getIntegerLong(); }

	@Override
	public long getIntegerLong() { return get(); }

	@Override
	public void setInteger( final int f ) { setInteger( (long)f ); }

	@Override
	public void setInteger( final long f ) { set( f ); }

	/** The maximum value that can be stored is {@code Math.pow(2, nBits) -1}. */
	@Override
	public double getMaxValue() { return Math.pow(2, j.length) -1; }
	@Override
	public double getMinValue()  { return 0; }

	@Override
	public int getIndex() { return i; }

	@Override
	public void updateIndex( final int index )
	{
		i = index;
		
		j[0] = index * j.length;
		for (int k=1; k<j.length; ++k) {
			j[k] = j[0] + k;
		}
	}

	@Override
	public void incIndex()
	{
		++i;
		
		for (int k=0; k<j.length; ++k) {
			j[k] += j.length;
		}
	}
	@Override
	public void incIndex( final int increment )
	{
		i += increment;

		final int inc = j.length * increment;
		for (int k=0; k<j.length; ++k) {
			j[k] += inc;
		}
	}
	@Override
	public void decIndex()
	{
		--i;
		for (int k=0; k<j.length; ++k) {
			j[k] -= j.length;
		}
	}
	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;

		final int dec = j.length * decrement;
		for (int k=0; k<j.length; ++k) {
			j[k] -= dec;
		}
	}

	@Override
	public UnsignedAnyBitType createVariable(){ return new UnsignedAnyBitType( j.length ); }

	@Override
	public UnsignedAnyBitType copy(){ return new UnsignedAnyBitType( get(), j.length ); }

	@Override
	public int getEntitiesPerPixel() { return 1; }

	@Override
	public int getBitsPerPixel() { return j.length; }
}
