/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2.img.transform;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.transform.integer.TranslationTransform;
import net.imglib2.type.Type;
import net.imglib2.view.TransformView;

/**
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Lee Kamentsky
 */
public class ImgTranslationAdapter< T extends Type< T >, I extends Img< T >> extends Point implements Img< T >
{
	protected final I img;

	/**
	 * Initialize the translation adapter with zero translation and the image to
	 * adapt
	 * 
	 * @param img
	 *            - the image to be accessed at the translated coordinates.
	 */
	public ImgTranslationAdapter( final I img )
	{
		super( img.numDimensions() );
		this.img = img;
	}

	/**
	 * Initialize the adapter with the image to be adapted and the initial
	 * translation
	 * 
	 * @param img
	 *            - image to be adapted
	 * @param offset
	 *            - offset image coordinate 0,0... so it appears to be at
	 *            offset[0], offset[1] ...
	 */
	public ImgTranslationAdapter( final I img, final long[] offset )
	{
		super( offset );
		this.img = img;
	}

	/**
	 * Initialize the adapter with the image to be adapted and a localizable
	 * giving the translated image origin
	 * 
	 * @param img
	 *            - image to be adapted
	 * @param localizable
	 *            - img's 0,0 will appear to be at this localizable's
	 *            coordinates.
	 */
	public ImgTranslationAdapter( final I img, final Localizable localizable )
	{
		super( localizable );
		this.img = img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RandomAccessible#randomAccess()
	 */
	@Override
	public RandomAccess< T > randomAccess()
	{
		return new TransformView< T >( img, getTranslationTransform() ).randomAccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RandomAccessible#randomAccess(net.imglib2.Interval)
	 */
	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return new TransformView< T >( img, getTranslationTransform() ).randomAccess( interval );
	}

	/**
	 * Construct a translation transform that transforms space coordinates to
	 * the offset image coordinateas.
	 * 
	 * @return a TranslationTransform that can be applied in a TransformView to
	 *         generate a RandomAccesible.
	 */
	protected TranslationTransform getTranslationTransform()
	{
		final long[] translation = new long[ numDimensions() ];
		for ( int i = 0; i < numDimensions(); i++ )
		{
			translation[ i ] = -position[ i ];
		}
		return new TranslationTransform( translation );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.Interval#min(int)
	 */
	@Override
	public long min( final int d )
	{
		return img.min( d ) + position[ d ];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.Interval#min(long[])
	 */
	@Override
	public void min( final long[] min )
	{
		img.min( min );
		for ( int i = 0; i < numDimensions(); i++ )
		{
			min[ i ] += position[ i ];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.Interval#min(net.imglib2.Positionable)
	 */
	@Override
	public void min( final Positionable min )
	{
		img.min( min );
		min.move( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.Interval#max(int)
	 */
	@Override
	public long max( final int d )
	{
		return img.max( d ) + position[ d ];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.Interval#max(long[])
	 */
	@Override
	public void max( final long[] max )
	{
		img.max( max );
		for ( int i = 0; i < numDimensions(); i++ )
		{
			max[ i ] += position[ i ];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.Interval#max(net.imglib2.Positionable)
	 */
	@Override
	public void max( final Positionable max )
	{
		img.max( max );
		max.move( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.Interval#dimensions(long[])
	 */
	@Override
	public void dimensions( final long[] dimensions )
	{
		img.dimensions( dimensions );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.Interval#dimension(int)
	 */
	@Override
	public long dimension( final int d )
	{
		return img.dimension( d );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RealInterval#realMin(int)
	 */
	@Override
	public double realMin( final int d )
	{
		return img.realMin( d ) + position[ d ];
	}

	@Override
	public void realMin( final double[] min )
	{
		img.realMin( min );
		for ( int i = 0; i < numDimensions(); i++ )
		{
			min[ i ] += position[ i ];
		}
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		img.realMin( min );
		min.move( this );
	}

	@Override
	public double realMax( final int d )
	{
		return img.realMax( d ) + position[ d ];
	}

	@Override
	public void realMax( final double[] max )
	{
		img.realMax( max );
		for ( int i = 0; i < numDimensions(); i++ )
		{
			max[ i ] += position[ i ];
		}
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		img.realMax( max );
		max.move( this );
	}

	@Override
	public Cursor< T > cursor()
	{
		return cursorImpl( img.cursor() );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		// TODO Auto-generated method stub
		return cursorImpl( img.localizingCursor() );
	}

	private Cursor< T > cursorImpl( final Cursor< T > c )
	{
		final long[] offset = this.position.clone();
		return new Cursor< T >()
		{

			@Override
			public void localize( final float[] pos )
			{
				c.localize( pos );
				for ( int i = 0; i < numDimensions(); i++ )
				{
					pos[ i ] += offset[ i ];
				}
			}

			@Override
			public void localize( final double[] pos )
			{
				c.localize( pos );
				for ( int i = 0; i < numDimensions(); i++ )
				{
					pos[ i ] += offset[ i ];
				}
			}

			@Override
			public float getFloatPosition( final int d )
			{
				return c.getFloatPosition( d ) + offset[ d ];
			}

			@Override
			public double getDoublePosition( final int d )
			{
				return c.getDoublePosition( d ) + offset[ d ];
			}

			@Override
			public int numDimensions()
			{
				return ImgTranslationAdapter.this.numDimensions();
			}

			@Override
			public T get()
			{
				return c.get();
			}

			@Override
			public Sampler< T > copy()
			{
				return copyCursor();
			}

			@Override
			public void jumpFwd( final long steps )
			{
				c.jumpFwd( steps );
			}

			@Override
			public void fwd()
			{
				c.fwd();
			}

			@Override
			public void reset()
			{
				c.reset();
			}

			@Override
			public boolean hasNext()
			{
				return c.hasNext();
			}

			@Override
			public T next()
			{
				return c.next();
			}

			@Override
			public void remove()
			{
				c.remove();
			}

			@Override
			public void localize( final int[] pos )
			{
				c.localize( pos );
				for ( int i = 0; i < numDimensions(); i++ )
				{
					pos[ i ] += offset[ i ];
				}
			}

			@Override
			public void localize( final long[] pos )
			{
				c.localize( pos );
				for ( int i = 0; i < numDimensions(); i++ )
				{
					pos[ i ] += offset[ i ];
				}
			}

			@Override
			public int getIntPosition( final int d )
			{
				return c.getIntPosition( d ) + ( int ) offset[ d ];
			}

			@Override
			public long getLongPosition( final int d )
			{
				return c.getIntPosition( d ) + offset[ d ];
			}

			@Override
			public Cursor< T > copyCursor()
			{
				return cursorImpl( c.copyCursor() );
			}
		};
	}

	@Override
	public long size()
	{
		return img.size();
	}

	@Override
	public T firstElement()
	{
		return img.firstElement();
	}

	@Override
	public Object iterationOrder()
	{
		return img.iterationOrder();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursorImpl( img.cursor() );
	}

	@Override
	public ImgFactory< T > factory()
	{
		return img.factory();
	}

	@Override
	public ImgTranslationAdapter< T, I > copy()
	{
		return new ImgTranslationAdapter< T, I >( img, this );
	}
}
