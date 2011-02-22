/**
 * Copyright (c) 2010, Stephan Saalfeld
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
package mpicbg.imglib.container.shapelist;

import java.awt.Shape;
import java.util.ArrayList;

import mpicbg.imglib.Interval;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.AbstractImg;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.planar.PlanarContainer;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.sampler.shapelist.ShapeListPositionableRasterSampler;
import mpicbg.imglib.sampler.shapelist.ShapeListOutOfBoundsPositionableRasterSampler;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @version 0.1a
 */
//public class ShapeList< T extends Type< T > > extends ContainerImpl< T, DataAccess >
public class ShapeList< T extends Type< T > > extends AbstractImg< T >
{
	
	/* shapes need to be ordered for rendering with correct overlap */
	final protected ArrayList< ArrayList< Shape > > shapeLists;
	final protected ArrayList< ArrayList< T > > typeLists;
	final protected T background;
	
	public ShapeList( final long[] dim, final T background )
	{
		super( dim );
		
		int n = 1;
		for ( int d = 2; d < dim.length; ++d )
			n *= dim[ d ];
		
		shapeLists = new ArrayList< ArrayList< Shape > > ( n );
		typeLists = new ArrayList< ArrayList< T > > ( n );
		
		for ( int d = 0; d < n; ++d )
		{
			shapeLists.add( new ArrayList< Shape >() );
			typeLists.add( new ArrayList< T >() );
		}
		this.background = background;
	}
	
	public T getBackground() { return background; }
	
	public synchronized void addShape( final Shape shape, final T type, final long[] position )
	{
		int p = 0;
		if ( position != null )
		{
			int f = 1;
			for ( int d = 2; d < numDimensions(); ++d )
			{
				p += f * position[ d - 2 ];
				f *= dimension( d );
			}
		}
		shapeLists.get( p ).add( shape ); 
		typeLists.get( p ).add( type );
	}
	
	@Override
	public ShapeListContainerFactory<T> factory() { return new ShapeListContainerFactory<T>(); }

	/**
	 * Find the upper most Shape visible at the given position and return its
	 * {@link Type}.
	 * 
	 * Does not perform bounds checking.  For shape planes, this doesn't
	 * matter, but for all other dimensions, the result is undefined or an
	 * {@link IndexOutOfBoundsException}.
	 * 
	 * @param x
	 * @param y
	 * @param p pre-multiplied index of all dimensions >1
	 * @return
	 */
	protected T getShapeType( final long x, final long y, final int p )
	{
		// TODO: for p to be a long, shapeList should be a TreeSet<Long,TreeSet<Long,Shape>>
		final ArrayList< Shape > shapeList = shapeLists.get( p );
		for ( int i = shapeList.size() - 1; i >= 0; --i )
		{
			if ( shapeList.get( i ).contains( x, y ) )
				return typeLists.get( p ).get( i );
		}
		return background;
	}
	
	
	/**
	 * Find the upper most Shape visible at the given position and return its
	 * {@link Type}.
	 * 
	 * This random access and, therefore, not efficient.  Use only if the
	 * dimensions >1 cannot be pre-calculated.
	 * 
	 * Does not perform bounds checking.  For shape planes, this doesn't
	 * matter, but for all other dimensions, the result is undefined or an
	 * {@link IndexOutOfBoundsException}.
	 * 
	 * @param position
	 * @return
	 */
	public T getShapeType( final long[] position )
	{
		int p = 0;
		int f = 1;
		for ( int d = 2; d < position.length; ++d )
		{
			p += f * position[ d ];
			f *= dimension( d );
		}
		return getShapeType( position[ 0 ], position[ 1 ], p );
	}

	@Override
	public ImgRandomAccess<T> randomAccess() {
		return new ShapeListPositionableRasterSampler< T >( this );
	}

	@Override
	public ImgRandomAccess<T> randomAccess( final OutOfBoundsFactory< T, Img<T> > factory ) {
		return new ShapeListOutOfBoundsPositionableRasterSampler< T >( this, factory );
	}

	@Override
	public ImgCursor<T> cursor() {
		return new ShapeListPositionableRasterSampler< T >( this );
	}

	@Override
	public ImgCursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f ) {
		if ( f.numDimensions() != this.numDimensions() )
			return false;
		
		if ( getClass().isInstance( f ) || PlanarContainer.class.isInstance( f )
				|| Array.class.isInstance( f ) )
		{
			final Interval a = ( Interval )f;
			for ( int d = 0; d < n; ++d )
				if ( size[ d ] != a.dimension( d ) )
					return false;
			
			return true;
		}
		
		return false;
	}
}
