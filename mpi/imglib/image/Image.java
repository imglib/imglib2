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
package mpi.imglib.image;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import mpi.imglib.algorithm.math.MathLib;
import mpi.imglib.container.Container;
import mpi.imglib.container.ContainerFactory;
import mpi.imglib.container.ImageProperties;
import mpi.imglib.cursor.Cursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizablePlaneCursor;
import mpi.imglib.cursor.vector.Dimensionality;
import mpi.imglib.image.display.Display;
import mpi.imglib.image.display.imagej.ImageJFunctions;
import mpi.imglib.interpolation.Interpolator;
import mpi.imglib.interpolation.InterpolatorFactory;
import mpi.imglib.outside.OutsideStrategyFactory;
import mpi.imglib.type.Type;

public class Image<T extends Type<T>> implements ImageProperties, Dimensionality
{
	final protected ArrayList<Cursor<T>> cursors;
	final ContainerFactory storageFactory;
	final Container<T> container;
	final ImageFactory<T> imageFactory;
	final T type;

	final static AtomicInteger i = new AtomicInteger(), j = new AtomicInteger();
	protected String name;
	final protected int numDimensions, numPixels;
	final protected int[] dim;

	protected Display<T> display;

	private Image( Container<T> container, ImageFactory<T> imageFactory, int dim[], String name )
	{
		if (name == null || name.length() == 0)
			this.name = "image" + i.getAndIncrement();
		else
			this.name = name;

		if ( dim == null || dim.length < 1 )
		{
			System.err.print("Cannot instantiate Image, dimensions are null. Creating a 1D image of size 1.");
			dim = new int[]{1};
		}

		this.numDimensions = dim.length;
		int numPixels = 1;		
		for (int i = 0; i < numDimensions; i++)
		{
			if ( dim[i] <= 0 )
			{
				System.err.print("Warning: Image dimension " + (i+1) + " does not make sense: size=" + dim[i] + ". Replacing it by 1.");
				dim[i] = 1;	
			}
			numPixels *= dim[i];
		}
		this.numPixels = numPixels;
		
		this.dim = dim.clone();
		this.cursors = new ArrayList<Cursor<T>>();
		
		this.storageFactory = imageFactory.getContainerFactory();
		this.storageFactory.setOptimizedContainerUse( imageFactory.useOptimizedContainers() );
		
		this.imageFactory = imageFactory;

		// createType() needs the imageFactory
		this.type = createType();
		
		if ( container == null )
			this.container = createContainer();
		else
			this.container = container;
		
		setDefaultDisplay();	
	}
	
	protected Image( Container<T> container, ImageFactory<T> imageFactory, String name )
	{
		this( container, imageFactory, container.getDimensions(), name );
	}
	
	public Image( final ImageFactory<T> imageFactory, int dim[], final String name )	
	{	
		this ( null, imageFactory, dim, name );		
	}

	public Image<T> createNewImage( final int[] dimensions, final String name ) { return imageFactory.createImage( dimensions, name ); }
	public Image<T> createNewImage( final int[] dimensions ) { return createNewImage( dimensions, null ); }
	public Image<T> createNewImage( final String name ) { return createNewImage( dim, name); }
	public Image<T> createNewImage() { return createNewImage( dim, null ); }
	
	public Container<T> getContainer() { return container; }
	public T createType() { return imageFactory.createType(); }
	
	public Cursor<T> createCursor()
	{
		final T type = this.type.createType( container );
		Cursor<T> cursor = container.createCursor( type, this );
		addCursor( cursor );
		return cursor;	
	}
	
	public LocalizableCursor<T> createLocalizableCursor()
	{
		final T type = this.type.createType( container );
		LocalizableCursor<T> cursor = container.createLocalizableCursor( type, this );
		addCursor( cursor );
		return cursor;		
	}
	
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor()
	{
		final T type = this.type.createType( container );
		LocalizablePlaneCursor<T> cursor = container.createLocalizablePlaneCursor( type, this );
		addCursor( cursor );
		return cursor;				
	}
	
	public LocalizableByDimCursor<T> createLocalizableByDimCursor()
	{
		final T type = this.type.createType( container );
		LocalizableByDimCursor<T> cursor = container.createLocalizableByDimCursor( type, this );
		addCursor( cursor );
		return cursor;						
	}
	
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( OutsideStrategyFactory<T> factory )
	{
		final T type = this.type.createType( container );
		LocalizableByDimCursor<T> cursor = container.createLocalizableByDimCursor( type, this, factory );
		addCursor( cursor );
		return cursor;								
	}
		
	protected Container<T> createContainer() { return type.createSuitableContainer( storageFactory, dim ); }

	public Interpolator<T> createInterpolator( final InterpolatorFactory<T> factory )
	{
		return factory.createInterpolator( this );
	}
	
	public void setDefaultDisplay() { this.display = type.getDefaultDisplay( this ); }

	public Display<T> getDisplay() { return display; }
	public void setDisplay( final Display<T> display ) { this.display = display; }
	
	public ImageJFunctions getImageJFunctions() { return new ImageJFunctions(); }

	final public synchronized static int createUniqueId() { return j.getAndIncrement(); }
	
	public void close()
	{ 
		closeAllCursors();
		container.close();
	}
	
	public int[] createPositionArray() { return new int[ getNumDimensions() ]; }
	
	@Override
	public int getNumDimensions() { return dim.length; }
	@Override
	public int[] getDimensions() { return dim.clone(); }
	@Override
	public int getNumPixels() { return numPixels; }

	@Override
	public String getName() { return name; }

	@Override
	public void setName(String name) { this.name = name; }
	
	@Override
	public String toString()
	{
		return "Image '" + this.getName() + "', dim=" + MathLib.printCoordinates( dim );
	}
	
	@Override
	public void getDimensions( int[] dimensions )
	{
		for (int i = 0; i < numDimensions; i++)
			dimensions[i] = this.dim[i];
	}

	@Override
	public int getDimension( int dim )
	{
		if ( dim < numDimensions && dim > -1 )
			return this.dim[ dim ];
		else
			return 1;		
	}
	
	@Override
	public Image<T> clone()
	{
		final Image<T> clone = this.createNewImage();
		
		final Cursor<T> c1 = this.createCursor();
		final Cursor<T> c2 = clone.createCursor();
		
		while ( c1.hasNext() )
		{
			c1.fwd();
			c2.fwd();
			
			c2.getType().set( c1.getType() );		
		}
		
		c1.close();
		c2.close();
		
		return clone;
	}

	public ContainerFactory getStorageFactory() { return storageFactory; }
	public ImageFactory<T> getImageFactory() { return imageFactory; }
	
	public void closeAllCursors()
	{
		for (Cursor<?> i : cursors)
			i.close();
	}	
	public ArrayList<Cursor<T>> getCursors() { return cursors; }	
	public ArrayList<Cursor<T>> getActiveCursors() 
	{ 
		ArrayList<Cursor<T>> activeCursors = new ArrayList<Cursor<T>>();
		
		for (Cursor<T> i : cursors)
			if (i.isActive())
				activeCursors.add(i);
		
		return activeCursors; 
	}	
	public synchronized void addCursor( final Cursor<T> c ) { cursors.add( c );	}
	public synchronized void closeLastCursor() 
	{ 
		if ( cursors.size() > 0 )
		{
			cursors.get( cursors.size() - 1 ).close();
			cursors.remove( cursors.size() - 1 );
		}
	}
	public int getNumCursors() { return cursors.size(); }
	public int getNumActiveCursors() 
	{
		int active = 0;
		
		for (Cursor<?> i : cursors)
			if (i.isActive())
				active++;
		
		return active;
	}	
}
