/**
 * Copyright (c) 2009--2010, Funke, Preibisch, Saalfeld & Schindelin
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
 */
package mpicbg.imglib.container.planar;

import ij.ImageStack;

import java.util.ArrayList;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.DirectAccessContainerImpl;
import mpicbg.imglib.container.basictypecontainer.PlanarAccess;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.cursor.planar.PlanarCursor;
import mpicbg.imglib.cursor.planar.PlanarLocalizableByDimCursor;
import mpicbg.imglib.cursor.planar.PlanarLocalizableByDimOutOfBoundsCursor;
import mpicbg.imglib.cursor.planar.PlanarLocalizableCursor;
import mpicbg.imglib.cursor.planar.PlanarLocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

/**
 * A {@link Container} that stores data in an aray of 2d-slices each as a
 * linear array of basic types.  For types that are supported by ImageJ (byte,
 * short, int, float), an actual Planar is created or used to store the
 * data.  Alternatively, an {@link PlanarContainer} can be created using
 * an already existing {@link Planar} instance. 
 * 
 * {@link PlanarContainer PlanarContainers} provides a legacy layer to
 * apply imglib-based algorithm implementations directly on the data stored in
 * an ImageJ {@link Planar}.  For all types that are suported by ImageJ, the
 * {@link PlanarContainer} provides access to the pixels of an
 * {@link Planar} instance that can be accessed ({@see #getPlanar()}.
 * 
 * @author Jan Funke, Stephan Preibisch, Stephan Saalfeld, Johannes Schindelin
 */
public class PlanarContainer<T extends Type<T>, A extends ArrayDataAccess<A>> extends DirectAccessContainerImpl<T,A> implements PlanarAccess<A>
{
	final protected PlanarContainerFactory factory;
	final protected int slices;

	final ArrayList< A > mirror;
	
	public PlanarContainer( final int[] dim, final int entitiesPerPixel ) 
	{
		this( new PlanarContainerFactory(), null, dim, entitiesPerPixel );
	}

	PlanarContainer( final PlanarContainerFactory factory, final A creator, final int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );				

		// compute number of slices
		int s = 1;
	
		for ( int d = 2; d < numDimensions; ++d )
			s *= dim[ d ];
		
		slices = s;
			
		this.factory = factory;
		
		mirror = new ArrayList< A >( slices );

		for ( int i = 0; i < slices; ++i )
			mirror.add( creator == null ? null : creator.createArray( getDimension( 0 ) * getDimension( 1 ) * entitiesPerPixel ) );
	}

	@Override
	public A update( final Cursor< ? > c )
	{
		return mirror.get( c.getStorageIndex() );
	}
		
	/**
	 * Note: this is NOT the number of z-slices!
	 * 
	 * @return depth * frames * channels which reflects the number of slices
	 * of the {@link ImageStack} used to store pixels in {@link Planar}.
	 */
	public int getSlices() { return slices; }

	/**
	 * For a given >=2d location, estimate the pixel index in the stack slice.
	 * 
	 * @param l
	 * @return
	 */
	public final int getIndex( final int[] l ) 
	{
		if ( numDimensions > 1 )
			return l[ 1 ] * dim[ 0 ] + l[ 0 ];
		else
			return l[ 0 ];
	}

	@Override
	public Cursor<T> createCursor( final Image<T> image ) 
	{
		return new PlanarCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableCursor<T> createLocalizableCursor( final Image<T> image ) 
	{
		return new PlanarLocalizableCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor( final Image<T> image ) 
	{
		return new PlanarLocalizablePlaneCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( final Image<T> image ) 
	{
		return new PlanarLocalizableByDimCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( final Image<T> image, OutOfBoundsStrategyFactory<T> outOfBoundsFactory ) 
	{
		return new PlanarLocalizableByDimOutOfBoundsCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer(), outOfBoundsFactory );
	}
	
	public PlanarContainerFactory getFactory() { return factory; }

	@Override
	public void close()
	{
		for ( final A array : mirror )
			array.close();
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

	@Override
	public A getPlane( final int no ) { return mirror.get( no ); }

	@Override
	public void setPlane( final int no, final A plane ) { mirror.set( no, plane ); }
}
