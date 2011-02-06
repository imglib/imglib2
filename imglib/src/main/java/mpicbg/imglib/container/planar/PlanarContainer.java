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

import java.util.ArrayList;

import mpicbg.imglib.Interval;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.AbstractNativeContainer;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgFactory;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.basictypecontainer.PlanarAccess;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.NativeType;

/**
 * A {@link Container} that stores data in an array of 2d-slices each as a
 * linear array of basic types.  For types that are supported by ImageJ (byte,
 * short, int, float), an actual Planar is created or used to store the
 * data.  Alternatively, an {@link PlanarContainer} can be created using
 * an already existing {@link Planar} instance.
 *
 * {@link PlanarContainer PlanarContainers} provides a legacy layer to
 * apply imglib-based algorithm implementations directly on the data stored in
 * an ImageJ {@link Planar}.  For all types that are supported by ImageJ, the
 * {@link PlanarContainer} provides access to the pixels of an
 * {@link Planar} instance that can be accessed ({@see #getPlanar()}.
 *
 * @author Jan Funke, Stephan Preibisch, Stephan Saalfeld, Johannes Schindelin
 */
public class PlanarContainer< T extends NativeType< T >, A extends ArrayDataAccess<A> > extends AbstractNativeContainer< T, A > implements PlanarAccess< A >
{
	final protected int slices;
	final int[] dim;
	
	final protected ArrayList< A > mirror;

	public PlanarContainer( final long[] dim, final int entitiesPerPixel )
	{
		this( null, dim, entitiesPerPixel );
	}

	PlanarContainer( final A creator, final long[] dim, final int entitiesPerPixel )
	{
		super( dim, entitiesPerPixel );

		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = (int)dim[ d ];
		
		// compute number of slices
		int s = 1;

		for ( int d = 2; d < n; ++d )
			s *= dim[ d ];

		slices = s;

		mirror = new ArrayList< A >( slices );

		for ( int i = 0; i < slices; ++i )
			mirror.add( creator == null ? null : creator.createArray( this.dim[ 0 ] * this.dim[ 1 ] * entitiesPerPixel ) );
	}

	@Override
	public A update( final Object c )
	{
		return mirror.get( ((PlanarLocation)c).getCurrentPlane() );
	}

	/**
   * @return total number of image planes
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
		if ( n > 1 )
			return l[ 1 ] * dim[ 0 ] + l[ 0 ];
		return l[ 0 ];
	}

	@Override
	public PlanarCursor<T> cursor()
	{
		if ( n == 2 )
			return new PlanarCursor2D< T >( this );
		else
			return new PlanarCursor< T >( this );
	}

	@Override
	public PlanarLocalizingCursor<T> localizingCursor()
	{
		if ( n == 2 )
			return new PlanarLocalizingCursor2D< T >( this );
		else
			return new PlanarLocalizingCursor<T>( this );
	}

	@Override
	public ImgRandomAccess<T> randomAccess()
	{
		return new PlanarRandomAccess<T>( this );
	}

	@Override
	public ImgRandomAccess<T> randomAccess( OutOfBoundsFactory<T,Img<T>> outOfBoundsFactory )
	{
		return new PlanarOutOfBoundsRandomAccess< T >( this, outOfBoundsFactory );
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		if ( f.numDimensions() != this.numDimensions() )
			return false;
		
		if ( getClass().isInstance( f ) )
		{
			final Interval a = ( Interval )f;
			for ( int d = 0; d < n; ++d )
				if ( size[ d ] != a.dimension( d ) )
					return false;
		}
		
		return true;
	}

	@Override
	public A getPlane( final int no ) { return mirror.get( no ); }

	@Override
	public void setPlane( final int no, final A plane ) { mirror.set( no, plane ); }

	@Override
	public ImgFactory<T> factory() { return new PlanarContainerFactory<T>(); }
}
