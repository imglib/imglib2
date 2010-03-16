/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.image;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.ImageProperties;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.cursor.vector.Dimensionality;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public class Image<T extends Type<T>> implements ImageProperties, Dimensionality
{
	final protected ArrayList<Cursor<T>> cursors;
	final ContainerFactory storageFactory;
	final Container<T> container;
	final ImageFactory<T> imageFactory;
	final T type;

	final static AtomicInteger i = new AtomicInteger(), j = new AtomicInteger();
	protected String name;
	
	// this has to be read from the container as it might change during processing	
	//final protected int numDimensions, numPixels;	
	//final protected int[] dim;
	
	final protected float[] calibration;

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

		for (int i = 0; i < dim.length; i++)
		{
			if ( dim[i] <= 0 )
			{
				System.err.print("Warning: Image dimension " + (i+1) + " does not make sense: size=" + dim[i] + ". Replacing it by 1.");
				dim[i] = 1;	
			}
		}
		this.cursors = new ArrayList<Cursor<T>>();		
		this.storageFactory = imageFactory.getContainerFactory();		
		this.imageFactory = imageFactory;

		// createType() needs the imageFactory
		this.type = createType();
		
		if ( container == null )
			this.container = createContainer( dim );
		else
			this.container = container;
		
		setDefaultDisplay();	
		
		calibration = new float[ getContainer().getNumDimensions() ];
		for ( int d = 0; d < getContainer().getNumDimensions(); ++d )
			calibration[ d ] = 1;
	}
	
	protected Image( Container<T> container, ImageFactory<T> imageFactory, String name )
	{
		this( container, imageFactory, container.getDimensions(), name );
	}
	
	protected Image( final ImageFactory<T> imageFactory, int dim[], final String name )	
	{	
		this ( null, imageFactory, dim, name );		
	}

	/**
	 * Creates a new {@link Image} with the same {@link ContainerFactory} and {@link Type} as this one.
	 * @param dimensions - the dimensions of the {@link Image}
	 * @return - a new empty {@link Image}
	 */
	public Image<T> createNewImage( final int[] dimensions, final String name ) { return imageFactory.createImage( dimensions, name ); }

	/**
	 * Creates a new {@link Image} with the same {@link ContainerFactory} and {@link Type} as this one, the name is given automatically.
	 * @param dimensions - the dimensions of the {@link Image}
	 * @return - a new empty {@link Image}
	 */
	public Image<T> createNewImage( final int[] dimensions ) { return createNewImage( dimensions, null ); }

	/**
	 * Creates a new {@link Image} with the same dimensions, {@link ContainerFactory} and {@link Type} as this one as this one.
	 * @return - a new empty {@link Image}
	 */
	public Image<T> createNewImage( final String name ) { return createNewImage( getContainer().getDimensions(), name); }
	
	/**
	 * Creates a new {@link Image} with the same dimensions, {@link ContainerFactory} and {@link Type} as this one, the name is given automatically.
	 * @return - a new empty {@link Image}
	 */
	public Image<T> createNewImage() { return createNewImage( getContainer().getDimensions(), null ); }
	
	public float[] getCalibration() { return calibration.clone(); }
	public float getCalibration( final int dim ) { return calibration[ dim ]; }
	public void setCalibration( final float[] calibration ) 
	{ 
		for ( int d = 0; d < getContainer().getNumDimensions(); ++d )
			this.calibration[ d ] = calibration[ d ];
	}
	public void setCalibration( final float calibration, final int dim ) { this.calibration[ dim ] = calibration; } 

	/**
	 * Returns the {@link Container} that is used for storing the image data.
	 * @return Container<T> - the typed {@link Container}
	 */
	public Container<T> getContainer() { return container; }
	
	/**
	 * Creates a {@link Type} that the {@link Image} is typed with.
	 * @return T - an instance of {@link Type} for computing
	 */
	public T createType() { return imageFactory.createType(); }
	
	/**
	 * Return a {@link Cursor} that will traverse the image's pixel data in a memory-optimized fashion.
	 * @return Cursor<T> - the typed {@link Cursor}
	 */
	public Cursor<T> createCursor()
	{
		final T type = this.type.createType( container );
		Cursor<T> cursor = container.createCursor( type, this );
		addCursor( cursor );
		return cursor;	
	}
	
	/**
	 * Return a {@link LocalizableCursor} that will traverse the image's pixel data in a memory-optimized fashion 
	 * and keeps track of its position
	 * @return LocalizableCursor<T> - the typed {@link LocalizableCursor}
	 */
	public LocalizableCursor<T> createLocalizableCursor()
	{
		final T type = this.type.createType( container );
		LocalizableCursor<T> cursor = container.createLocalizableCursor( type, this );
		addCursor( cursor );
		return cursor;		
	}
	
	/**
	 * Creates a {@link LocalizablePlaneCursor} which is optimized to iterate arbitrary 2-dimensional planes within an {@link Image}.
	 * This is very important for the {@link Display}.
	 * 
	 * @return - {@link LocalizablePlaneCursor}
	 */
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor()
	{
		final T type = this.type.createType( container );
		LocalizablePlaneCursor<T> cursor = container.createLocalizablePlaneCursor( type, this );
		addCursor( cursor );
		return cursor;				
	}
	
	/**
	 * Creates a {@link LocalizableByDimCursor} which is able to move freely within the {@link Image}.
	 * When exiting the {@link Image} this {@link LocalizableByDimCursor} will fail!! Therefore it is faster.
	 * @return - a {@link LocalizableByDimCursor} that cannot leave the {@link Image}
	 */
	public LocalizableByDimCursor<T> createLocalizableByDimCursor()
	{
		final T type = this.type.createType( container );
		LocalizableByDimCursor<T> cursor = container.createLocalizableByDimCursor( type, this );
		addCursor( cursor );
		return cursor;						
	}

	/**
	 * Creates a {@link LocalizableByDimCursor} which is able to move freely within and OUTSIDE of the {@link Image}
	 * given a {@link OutsideStrategyFactory} which defines the behaviour outside the {@link Image}.
	 * @param factory - the {@link OutsideStrategyFactory}
	 * @return - a {@link LocalizableByDimCursor} that can leave the {@link Image}
	 */
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( OutsideStrategyFactory<T> factory )
	{
		final T type = this.type.createType( container );
		LocalizableByDimCursor<T> cursor = container.createLocalizableByDimCursor( type, this, factory );
		addCursor( cursor );
		return cursor;								
	}
		
	/**
	 * This method is called internally, it asks the {@link Type} to create a suitable {@link Container} for the {@link Type} and
	 * the dimensionality
	 * @return {@link Container} - the instantiated Container
	 */
	protected Container<T> createContainer( final int[] dim ) { return type.createSuitableContainer( storageFactory, dim ); }

	/**
	 * Creates and {@link Interpolator} on this {@link Image} given a certain {@link InterpolatorFactory}.
	 * @param factory - the {@link InterpolatorFactory} to use
	 * @return {@link Interpolator}
	 */
	public Interpolator<T> createInterpolator( final InterpolatorFactory<T> factory ) { return factory.createInterpolator( this ); }
	
	/**
	 * Sets the default {@link Display} of this {@link Image} as defined by the {@link Type}
	 */
	public void setDefaultDisplay() { this.display = type.getDefaultDisplay( this ); }

	/**
	 * Return the current {@link Display} of the {@link Image}
	 * @return - the {@link Display}
	 */
	public Display<T> getDisplay() { return display; }
	
	/**
	 * Sets the {@link Display} associated with this {@link Image}.
	 * @param display - the {@link Display}
	 */
	public void setDisplay( final Display<T> display ) { this.display = display; }
	
	public ImageJFunctions getImageJFunctions() { return new ImageJFunctions(); }

	final public synchronized static int createUniqueId() { return j.getAndIncrement(); }
	
	/**
	 * Closes the {@link Image} by closing all {@link Cursor}s and the {@link Container}
	 */
	public void close()
	{ 
		closeAllCursors();
		container.close();
	}
	
	/**
	 * Creates an int array of the same dimensionality as this {@link Image} which can be used for addressing {@link Cursor}s. 
	 * @return - empty int[]
	 */
	public int[] createPositionArray() { return new int[ getNumDimensions() ]; }
	
	@Override
	public int getNumDimensions() { return getContainer().getNumDimensions(); }
	@Override
	public int[] getDimensions() { return getContainer().getDimensions(); }
	@Override
	public int getNumPixels() { return getContainer().getNumPixels(); }

	@Override
	public String getName() { return name; }

	@Override
	public void setName(String name) { this.name = name; }
	
	@Override
	public String toString()
	{
		return "Image '" + this.getName() + "', dim=" + MathLib.printCoordinates( getContainer().getDimensions() );
	}
	
	@Override
	public void getDimensions( final int[] dimensions )
	{
		for (int i = 0; i < getContainer().getNumDimensions(); i++)
			dimensions[i] = getContainer().getDimension( i );
	}

	@Override
	public int getDimension( final int dim ) { return getContainer().getDimension( dim ); }
	
	/**
	 * Clones this {@link Image}, i.e. creates this {@link Image} containing the same content.
	 * No {@link Cursor}s will be instantiated and the name will be given automatically.
	 */
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

	/**
	 * Returns the {@link ContainerFactory} of this {@link Image}.
	 * @return - {@link ContainerFactory}
	 */
	public ContainerFactory getStorageFactory() { return storageFactory; }
	
	/**
	 * Returns the {@link ImageFactory} of this {@link Image}.
	 * @return - {@link ImageFactory}
	 */
	public ImageFactory<T> getImageFactory() { return imageFactory; }
	
	/**
	 * Closes all {@link Cursor}s operating on this {@link Image}.
	 */
	public void closeAllCursors()
	{
		for ( final Cursor<?> i : cursors )
			i.close();
	}
	
	/**
	 * Return all {@link Cursor}s currently instantiated for this {@link Image}.
	 * @return - {@link ArrayList} containing the {@link Cursor}s
	 */
	public ArrayList<Cursor<T>> getCursors() { return cursors; }	

	/**
	 * Return all active {@link Cursor}s currently instantiated for this {@link Image}.
	 * @return - {@link ArrayList} containing the {@link Cursor}s
	 */
	public ArrayList<Cursor<T>> getActiveCursors() 
	{ 
		final ArrayList<Cursor<T>> activeCursors = new ArrayList<Cursor<T>>();
		
		for (Cursor<T> i : cursors)
			if (i.isActive())
				activeCursors.add(i);
		
		return activeCursors; 
	}	
	/**
	 * Adds a {@link Cursor} to the {@link ArrayList} of instantiated {@link Cursor}s.
	 * @param c - new {@link Cursor}
	 */
	protected synchronized void addCursor( final Cursor<T> c ) { cursors.add( c );	}
	
	/**
	 * Closes the {@link Cursor} last instantiated.
	 */
	public synchronized void closeLastCursor() 
	{ 
		if ( cursors.size() > 0 )
		{
			cursors.get( cursors.size() - 1 ).close();
			cursors.remove( cursors.size() - 1 );
		}
	}

	/**
	 * Returns the number of {@link Cursor}s instantiated on this {@link Image}.
	 * @return - the number of {@link Cursor}s
	 */
	public int getNumCursors() { return cursors.size(); }
	
	/**
	 * Returns the number of active {@link Cursor}s instantiated on this {@link Image}.
	 * @return - the number of active {@link Cursor}s
	 */
	public int getNumActiveCursors() 
	{
		int active = 0;
		
		for (Cursor<?> i : cursors)
			if (i.isActive())
				active++;
		
		return active;
	}	
}
