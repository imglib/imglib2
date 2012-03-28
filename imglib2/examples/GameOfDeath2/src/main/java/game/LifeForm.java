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

package game;

import java.util.Random;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LifeForm implements NumericType<LifeForm>, Comparable<LifeForm>, NativeType<LifeForm>
{
	final static protected Random rnd = new Random();
	
	protected int i = 0;
	
	protected int nameI = 0, weightI = 1;

	final NativeImg<LifeForm, ? extends IntAccess> storage;
	
	// the (sub)DirectAccessContainer that holds the information 
	IntAccess b;
	
	// this is the constructor if you want it to read from an array
	public LifeForm( NativeImg<LifeForm, ? extends IntAccess> lifeFormStorage )
	{
		storage = lifeFormStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public LifeForm( final int name, final float weight )
	{
		storage = null;
		b = new IntArray( 2 );
		
		set( name, Float.floatToIntBits(weight) );
	}

	// this is the constructor if you want it to be a variable
	public LifeForm() { this( 0, 0 ); }

	@Override
	public NativeImg<LifeForm, ? extends IntAccess> createSuitableNativeImg( final NativeImgFactory<LifeForm> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<LifeForm, ? extends IntAccess> container = storageFactory.createIntInstance( dim, 2 );
		
		// create a Type that is linked to the container
		final LifeForm linkedType = new LifeForm( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	@Override
	public void updateContainer( final Object c ) 
	{ 
		b = storage.update( c );		
	}
	
	public void setName( final int name ) { b.setValue( nameI, name ); }
	public void setWeight( final float weight ) { b.setValue( weightI, Float.floatToIntBits(weight) ); }
	
	public int getName() { return b.getValue( nameI ); }
	public float getWeight() { return Float.intBitsToFloat( b.getValue( weightI ) ); }
	
	public void set( final int name, final float weight )
	{
		setName( name );
		setWeight( weight );
	}

	@Override
	public void add( final LifeForm c )
	{
		final float a = getWeight();// * ( 0.9f + 0.2f * rnd.nextFloat() );
		final float b = c.getWeight();// * ( 0.9f + 0.2f * rnd.nextFloat() );
		
		final int na = getName();
		final int nb = c.getName();
		
		if ( na == nb )
			setWeight( a + b );
		else
		{
			if ( a < b )
				set( nb, b - a );
			else
				setWeight( a - b );
		}
	}

	@Override
	public void div( final LifeForm c )
	{
		throw new UnsupportedOperationException( "LifeForm.div() is not supported " );
	}

	@Override
	public void mul( final LifeForm c )
	{
		throw new UnsupportedOperationException( "LifeForm.mul() is not supported " );
	}

	@Override
	public void sub( final LifeForm c )
	{
		throw new UnsupportedOperationException( "LifeForm.sub() is not supported " );
	}
	
	@Override
	public void mul( final float c ) { setWeight( getWeight() * c );	}

	@Override
	public void mul( final double c ) { setWeight( (float)( getWeight() * c ) );	}

	@Override
	public void setOne() { setWeight( 1 ); }

	@Override
	public void setZero() { setWeight( 0 ); }

	@Override
	public LifeForm createVariable() { return new LifeForm(); }

	@Override
	public LifeForm duplicateTypeOnSameNativeImg() { return new LifeForm( storage ); }

	@Override
	public int getIndex() { return i; }

	@Override
	public void set( final LifeForm c ) { set( c.getName(), c.getWeight() ); }

	@Override
	public int compareTo( final LifeForm c )
	{
		final double a = getWeight();
		final double b = c.getWeight();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public LifeForm clone() { return new LifeForm( getName(), getWeight() ); }

	@Override
	public void updateIndex( final int i ) 
	{ 
		this.i = i;
		nameI = i * 2;
		weightI = i * 2 + 1;
	}
	
	@Override
	public void incIndex() 
	{ 
		++i;
		nameI += 2;
		weightI += 2;
	}
	@Override
	public void incIndex( final int increment ) 
	{ 
		i += increment; 
		
		final int inc2 = 2 * increment;		
		nameI += inc2;
		weightI += inc2;
	}
	@Override
	public void decIndex() 
	{ 
		--i; 
		nameI -= 2;
		weightI -= 2;
	}
	@Override
	public void decIndex( final int decrement ) 
	{ 
		i -= decrement; 
		final int dec2 = 2 * decrement;		
		nameI -= dec2;
		weightI -= dec2;
	}	
	
	@Override
	public String toString()
	{
		return "Race " + getName() + ", Weight " + getWeight();
	}
	@Override
	public LifeForm copy(){ return new LifeForm( getName(), getWeight() ); }
	@Override
	public int getEntitiesPerPixel(){ return 0; }
}
