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

package net.imglib2.converter;

import net.imglib2.Cursor;
import net.imglib2.converter.sampler.SamplerConverter;

/**
 * TODO
 *
 */
public class ConvertedCursor< A, B > implements Cursor< B >
{
	private final SamplerConverter< A, B > converter;

	private final Cursor< A > source;

	private final B converted;

	public ConvertedCursor( final SamplerConverter< A, B > converter, final Cursor< A > source )
	{
		this.converter = converter;
		this.source = source;
		this.converted = converter.convert( source );
	}

	@Override
	public void localize( int[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( long[] position )
	{
		source.localize( position );
	}

	@Override
	public int getIntPosition( int d )
	{
		return source.getIntPosition( d );
	}

	@Override
	public long getLongPosition( int d )
	{
		return source.getLongPosition( d );
	}

	@Override
	public void localize( float[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( double[] position )
	{
		source.localize( position );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return source.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return source.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public void jumpFwd( long steps )
	{
		source.jumpFwd( steps );
	}

	@Override
	public void fwd()
	{
		source.fwd();
	}

	@Override
	public void reset()
	{
		source.reset();
	}

	@Override
	public boolean hasNext()
	{
		return source.hasNext();
	}

	@Override
	public B next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
		source.remove();
	}

	@Override
	public B get()
	{
		return converted;
	}

	@Override
	public ConvertedCursor< A, B > copy()
	{
		return new ConvertedCursor< A, B >( converter, ( Cursor< A > ) source.copy() );
	}

	@Override
	public ConvertedCursor< A, B > copyCursor()
	{
		return copy();
	}
}
