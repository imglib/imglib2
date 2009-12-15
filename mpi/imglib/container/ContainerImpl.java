/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpi.imglib.container;

import java.util.concurrent.atomic.AtomicInteger;

import mpi.imglib.type.Type;

public abstract class ContainerImpl<T extends Type<T>> implements Container<T>
{
	final static AtomicInteger idGen = new AtomicInteger(0);
	final protected int numPixels, numDimensions, entitiesPerPixel, numEntities, id;
	protected final int[] dim;
	
	final ContainerFactory factory;

	public ContainerImpl( final ContainerFactory factory, int[] dim, final int entitiesPerPixel )
	{
		this.numDimensions = dim.length;
		
		int numPixels = 1;		
		for (int i = 0; i < numDimensions; i++)
			numPixels *= dim[i];
		this.numPixels = numPixels;
		this.entitiesPerPixel = entitiesPerPixel;
		this.numEntities = numPixels * entitiesPerPixel;

		this.dim = dim.clone();
		this.factory = factory;
		this.id = idGen.getAndIncrement();
	}
		
	@Override
	public ContainerFactory getFactory() { return factory; }
	
	@Override
	public int getId(){ return id; }
	@Override
	public int getNumEntities() { return numEntities; }
	@Override
	public int getNumDimensions() { return dim.length; }
	@Override
	public int getNumEntitiesPerPixel(){ return entitiesPerPixel; }
	@Override
	public int[] getDimensions() { return dim.clone(); }
	
	@Override
	public void getDimensions( final int[] dimensions )
	{
		for (int i = 0; i < numDimensions; i++)
			dimensions[i] = this.dim[i];
	}

	@Override
	public int getDimension( final int dim )
	{
		if ( dim < numDimensions && dim > -1 )
			return this.dim[ dim ];
		else
			return 1;		
	}
	
	@Override
	public int getNumPixels() { return numPixels; }

	@Override
	public String toString()
	{
		String className = this.getClass().getName();
		className = className.substring( className.lastIndexOf(".") + 1, className.length());
		
		String description = className + ", id '" + getId() + "' [" + dim[ 0 ];
		
		for ( int i = 1; i < numDimensions; i++ )
			description += "x" + dim[ i ];
		
		description += "], " + entitiesPerPixel + " entities per pixel.";
		
		return description;
	}
	
	@Override
	public boolean compareStorageContainerDimensions( final Container<?> container )
	{
		if ( container.getNumDimensions() != this.getNumDimensions() )
			return false;
		
		for ( int i = 0; i < numDimensions; i++ )
			if ( this.dim[i] != container.getDimensions()[i])
				return false;
		
		return true;
	}		

	@Override
	public boolean compareStorageContainerCompatibility( final Container<?> container )
	{
		if ( compareStorageContainerDimensions( container ))
		{			
			if ( getFactory().getClass().isInstance( container.getFactory() ))
				return true;
			else
				return false;
		}
		else
		{
			return false;
		}
	}		
	
}
