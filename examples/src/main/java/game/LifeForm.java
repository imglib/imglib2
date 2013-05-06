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

package game;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;

/**
 * The LifeForm class stores the current state of a life form, i.e. its name and its weight. 
 * Furthermore, it implements a subset of the NumericType methods (add, mul) that allows to 
 * run a Gaussian convolution on a dataset consisting of life forms.
 *  
 * LifeForm implements the interfaces NumericType and NativeType. The NumericType type interface 
 * requires implementation of the standard mathematical operation add(), mul(), div() and sub(). 
 * This enables to run Gaussian Convolution on a dataset consisting of LifeForms. As we only 
 * require add() and mul() for the simulation, we simply did not implement div() and sub().
 * The NativeType interface requires the implementation of methods that allow LifeForm to act 
 * as proxy type, which means it can map itself into a Java native array. To store the name 
 * and weight we use an native integer-backed array using two int's per pixel. The second int 
 * storing the weight is bit-wise converted to a float. Note that the simulation would run as 
 * well if it would not implement NativeType, however using significantly more memory as each 
 * pixel has to be an individual object. Addtionally this requires using a ListImg instead of 
 * an ArrayImg or CellImg.
 *  
 * The mathematical operations are implemented as follows: 
 *  
 * add( LifeForm c )
 *  - if 'c' has the same name, the weight of 'c' will be added to this LifeForm
 *  - if 'c' has a different name, this LifeForm will take the name of the LifeForm with the 
 *    higher weight, but the weight of the defeated LifeForm is subtracted
 *  
 * mul( double c )
 *  - the weight of this LifeForm is multiplied by 'c'
 * 
 * Running a Gaussian Convolution on such a dataset will simulate the diffusion of each of the 
 * LifeForms to its neighboring area. 
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LifeForm implements NumericType<LifeForm>, NativeType<LifeForm>
{
	// the current index of the cursor or randomAccess
	protected int i = 0;
	
	// the index of name and weight in the current IntAccess
	protected int nameI = 0, weightI = 1;

	// the underlying container holding the data (could be ArrayImg, CellImg, ...)
	final NativeImg<LifeForm, ? extends IntAccess> storage;
	
	// the current array that holds the information, which one (e.g which cell
	// of a CellImg) it is is defined by the cursor/randomAccess 
	IntAccess b;
	
	// this is the constructor for initializing with an array
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

	// called by the NativeImgFactory to create a new NativeImg using the type of
	// data the LifeForm requires (in this case an integer array with 2 entries per pixel)
	@Override
	public NativeImg<LifeForm, ? extends IntAccess> createSuitableNativeImg( final NativeImgFactory<LifeForm> storageFactory, final long dim[] )
	{
		// create the container (int, 2 values per pixel)
		final NativeImg<LifeForm, ? extends IntAccess> container = storageFactory.createIntInstance( dim, 2 );
		
		// create a Type that is linked to the container
		final LifeForm linkedType = new LifeForm( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	// called by the cursor/randomAccess to update the currently active IntAccess
	@Override
	public void updateContainer( final Object c ) 
	{ 
		b = storage.update( c );		
	}
	
	/**
	 * Set the name of this LifeForm
	 * 
	 * @param name - the new name
	 */
	
	public void setName( final int name ) { b.setValue( nameI, name ); }
	/**
	 * Set the weight of this LifeForm
	 * 
	 * @param weight - the new weight
	 */
	public void setWeight( final float weight ) { b.setValue( weightI, Float.floatToIntBits(weight) ); }
	
	/**
	 * @return - the name of this LifeForm
	 */
	
	public int getName() { return b.getValue( nameI ); }
	/**
	 * @return - the weight of this LifeForm
	 */
	public float getWeight() { return Float.intBitsToFloat( b.getValue( weightI ) ); }
	
	/**
	 * Set name and weight of this LifeForm
	 * @param name - the new name
	 * @param weight - the new weight
	 */
	public void set( final int name, final float weight )
	{
		setName( name );
		setWeight( weight );
	}

	/**
	 * @param c - the LifeForm to "add" to this one
	 */
	@Override
	public void add( final LifeForm c )
	{
		final float a = getWeight();
		final float b = c.getWeight();
		
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

	/**
	 * @param c - multiply weight of this LifeForm by 'c'
	 */
	@Override
	public void mul( final float c ) { setWeight( getWeight() * c );	}

	/**
	 * @param c - multiply weight of this LifeForm by 'c'
	 */
	@Override
	public void mul( final double c ) { setWeight( (float)( getWeight() * c ) );	}

	/**
	 * not required/not defined
	 */
	@Override
	public void div( final LifeForm c )
	{
		throw new UnsupportedOperationException( "LifeForm.div() is not supported " );
	}

	/**
	 * not required/not defined
	 */
	@Override
	public void mul( final LifeForm c )
	{
		throw new UnsupportedOperationException( "LifeForm.mul() is not supported " );
	}

	/**
	 * not required/not defined
	 */
	@Override
	public void sub( final LifeForm c )
	{
		throw new UnsupportedOperationException( "LifeForm.sub() is not supported " );
	}
	
	/**
	 * set weight of this LifeForm to 1
	 */
	@Override
	public void setOne() { setWeight( 1 ); }

	/**
	 * set weight of this LifeForm to 0
	 */
	@Override
	public void setZero() { setWeight( 0 ); }

	/**
	 * create a new, uninitialized LifeForm
	 */
	@Override
	public LifeForm createVariable() { return new LifeForm(); }

	/**
	 * for internal use, create a new LifeForm that works on the defined NativeImg
	 */
	@Override
	public LifeForm duplicateTypeOnSameNativeImg() { return new LifeForm( storage ); }

	/**
	 * the currnet index in the IntAccess, definied by the cursor/randomAccess 
	 */
	@Override
	public int getIndex() { return i; }

	/**
	 * Set the values of this LifeForm to the values of LifeForm 'c'
	 * @param c - new LifeForm
	 */
	@Override
	public void set( final LifeForm c ) { set( c.getName(), c.getWeight() ); }
	
	/**
	 * @param i - set the index in the IntAccess (called by cursor/randomAccess)
	 */
	@Override
	public void updateIndex( final int i ) 
	{ 
		this.i = i;
		nameI = i * 2;
		weightI = i * 2 + 1;
	}
	
	/**
	 * increase the index in the IntAccess by one (called by cursor/randomAccess)
	 */
	@Override
	public void incIndex() 
	{ 
		++i;
		nameI += 2;
		weightI += 2;
	}

	/**
	 * increase the index in the IntAccess by 'increment' (called by cursor/randomAccess)
	 * @param increment
	 */
	@Override
	public void incIndex( final int increment ) 
	{ 
		i += increment; 
		
		final int inc2 = 2 * increment;		
		nameI += inc2;
		weightI += inc2;
	}

	/**
	 * decrease the index in the IntAccess by one (called by cursor/randomAccess)
	 */
	@Override	
	public void decIndex() 
	{ 
		--i; 
		nameI -= 2;
		weightI -= 2;
	}

	/**
	 * decrease the index in the IntAccess by 'increment' (called by cursor/randomAccess)
	 */
	@Override
	public void decIndex( final int decrement ) 
	{ 
		i -= decrement; 
		final int dec2 = 2 * decrement;		
		nameI -= dec2;
		weightI -= dec2;
	}	
	
	/**
	 * create a String representation of this LifeForm
	 */
	@Override
	public String toString()
	{
		return "Race " + getName() + ", Weight " + getWeight();
	}
	
	/**
	 * clone this LifeForm
	 */
	@Override
	public LifeForm clone() { return copy(); }

	/**
	 * copy this LifeForm
	 */
	@Override
	public LifeForm copy(){ return new LifeForm( getName(), getWeight() ); }
	
	/**
	 * @return - how many entities per pixel are used
	 */
	@Override
	public int getEntitiesPerPixel(){ return 2; }
}
