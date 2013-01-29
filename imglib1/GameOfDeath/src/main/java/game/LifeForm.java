/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package game;

import java.util.Random;

import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.type.numeric.NumericType;

/**
 * TODO
 *
 */
public class LifeForm implements NumericType<LifeForm>, Comparable<LifeForm>
{
	final static protected Random rnd = new Random();
	
	protected int i = 0;
	
	protected int nameI = 0, weightI = 1;

	final DirectAccessContainer<LifeForm, ? extends IntAccess> storage;
	
	// the (sub)DirectAccessContainer that holds the information 
	IntAccess b;
	
	// this is the constructor if you want it to read from an array
	public LifeForm( DirectAccessContainer<LifeForm, ? extends IntAccess> lifeFormStorage )
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
	public DirectAccessContainer<LifeForm, ? extends IntAccess> createSuitableDirectAccessContainer( final DirectAccessContainerFactory storageFactory, final int dim[] )
	{
		// create the container
		final DirectAccessContainer<LifeForm, ? extends IntAccess> container = storageFactory.createIntInstance( dim, 2 );
		
		// create a Type that is linked to the container
		final LifeForm linkedType = new LifeForm( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	@Override
	public void updateContainer( final Cursor<?> c ) 
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
	public LifeForm[] createArray1D(int size1) { return new LifeForm[ size1 ]; }

	@Override
	public LifeForm[][] createArray2D(int size1, int size2) { return new LifeForm[ size1 ][ size2 ]; }

	@Override
	public LifeForm[][][] createArray3D(int size1, int size2, int size3) { return new LifeForm[ size1 ][ size2 ][ size3 ]; }

	@Override
	public LifeForm createVariable() { return new LifeForm(); }

	@Override
	public LifeForm duplicateTypeOnSameDirectAccessContainer() { return new LifeForm( storage ); }

	@Override
	public Display<LifeForm> getDefaultDisplay( final Image<LifeForm> image ) { return new LifeFormDisplay( image ); }

	@Override
	public int getIndex() { return i;	}

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
