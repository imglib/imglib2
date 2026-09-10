/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
package net.imglib2.blocks;

/**
 * {@link Validity} with one value per pixel, backed by a {@code byte[]}.
 * <p>
 * Note that the backing array may be longer than {@link #length()} (it is
 * typically a reused buffer owned by a {@link VolatileArray}).
 */
public final class DenseValidity implements Validity
{
	private final byte[] valid;

	private final int length;

	/**
	 * @param valid
	 * 		backing array. Must have at least {@code length} elements.
	 * @param length
	 * 		number of pixels described
	 */
	public DenseValidity( final byte[] valid, final int length )
	{
		if ( valid.length < length )
			throw new IllegalArgumentException( "backing array too small" );
		this.valid = valid;
		this.length = length;
	}

	/**
	 * The backing array. (Maybe longer than {@link #length()}).
	 */
	byte[] array()
	{
		return valid;
	}

	@Override
	public int length()
	{
		return length;
	}

	@Override
	public boolean allValid()
	{
		for ( int i = 0; i < length; ++i )
			if ( valid[ i ] == 0 )
				return false;
		return true;
	}

	@Override
	public void expandTo( final byte[] dest )
	{
		if ( dest != valid )
			System.arraycopy( valid, 0, dest, 0, length );
	}
}
