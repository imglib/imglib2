/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.type.label;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.BasePairType;
import net.imglib2.type.NativeType;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.BasePairType;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Fraction;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class BasePairBitType implements BasePairType<BasePairBitType>, NativeType<BasePairBitType>
{
	// A mask for bit and, containing nBits of 1
	private final static long mask = 7; // 111 in binary
		
	// Maximum count is Integer.MAX_VALUE * (64 / getBitsPerPixel())
	protected int i = 0;

	final protected NativeImg<BasePairBitType, ? extends LongAccess> img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;
	
	public static enum Base { gap, N, A, T, G, C, U; }

	@Override
	public Fraction getEntitiesPerPixel() { return new Fraction( 3, 1 ); }
	
	// the adresses of the bits that we store
	int j1, j2, j3;
	
	// this is the constructor if you want it to read from an array
	public BasePairBitType( NativeImg<BasePairBitType, ? extends LongAccess> bitStorage )
	{
		img = bitStorage;
		updateIndex( 0 );
	}
	
	// this is the constructor if you want it to be a variable
	public BasePairBitType( final Base value )
	{
		img = null;
		updateIndex( 0 );
		dataAccess = new LongArray( 1 );
		set( value );
	}	

	// this is the constructor if you want it to be a variable
	public BasePairBitType() { this( Base.N ); }
	
	@Override
	public NativeImg<BasePairBitType, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<BasePairBitType> storageFactory, final long dim[] )	
	{
		// create the container
		final NativeImg<BasePairBitType, ? extends LongAccess> container = storageFactory.createLongInstance( dim, new Fraction( 3, 64 ) );
		
		// create a Type that is linked to the container
		final BasePairBitType linkedType = new BasePairBitType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	@Override
	public void updateContainer( final Object c ) { dataAccess = img.update( c ); }

	@Override
	public BasePairBitType duplicateTypeOnSameNativeImg() { return new BasePairBitType( img ); }
	
	@Override
	public int getIndex() { return i; }
	
	@Override
	public void updateIndex( final int index ) 
	{ 
		this.i = index;
		j1 = index * 3;
		j2 = j1 + 1;
		j3 = j1 + 2;
	}
	
	@Override
	public void incIndex() 
	{ 
		++i;
		j1 += 3;
		j2 += 3;
		j3 += 3;
	}
	@Override
	public void incIndex( final int increment ) 
	{ 
		i += increment; 
		
		final int inc3 = 3 * increment;		
		j1 += inc3;
		j2 += inc3;
		j3 += inc3;
	}
	@Override
	public void decIndex() 
	{ 
		--i;
		j1 -= 3;
		j2 -= 3;
		j3 -= 3;
	}
	@Override
	public void decIndex( final int decrement ) 
	{ 
		i -= decrement; 

		final int dec3 = 3 * decrement;		
		j1 -= dec3;
		j2 -= dec3;
		j3 -= dec3;
	}

	@Override
	public void set( final Base base ) 
	{
		// can go over longs
	}
	
	@Override
	public Base get() 
	{		
		// can go over longs
	}
	
	@Override
	public int compareTo( final BasePairBitType c ) 
	{ 
		final Base input = get();
		final Base compare = c.get();
		
		if ( input == compare )
		{
			return 0;
		}
		switch ( input )
		{
			case gap: return -1; 
			case N: return compare == Base.gap ? 1 : -1;
			case A: return compare == Base.gap || compare == Base.N ? 1 : -1;
			case T: return compare == Base.G || compare == Base.C ? -1 : 1;
			case G: return compare == Base.C ? -1 : 1;
			default: return 1;
		}
	}
	
	@Override
	public void complement() 
	{
		final Base base = get();
		switch ( base )
		{
			case A: set( Base.T ); break;
			case T: set( Base.A ); break;
			case G: set( Base.C ); break;
			case C: set( Base.G ); break;
		}
	}

	@Override
	public byte baseToValue()
	{
		final Base base = get();
		
		switch ( base )
		{
			case N: return 1;
			case A: return 2;
			case T: return 3;
			case G: return 4;
			case C: return 5;
			default: return 0;
		}		
	}
	
	@Override
	public void set( final BasePairBitType c ) { set( c.get() ); }

	@Override
	public BasePairBitType createVariable(){ return new BasePairBitType(); }

	@Override
	public BasePairBitType copy(){ return new BasePairBitType( this.get() ); }

	@Override
	public String toString() { return this.get().toString(); }
}
