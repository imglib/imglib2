/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
/**
 *
 */
package net.imglib2.transform.integer.permutation;

import net.imglib2.transform.InvertibleTransform;

/**
 * Bijective integer transform mapping between integer coordinates in [0,n-1].
 * Currently, this transform handles only coordinates in the integer range
 * because it is implemented using primitive arrays with integer indices for
 * efficiency reasons.  Expect this permutation to be transferred to long
 * coordinates some time in more distant future.
 *
 * @author Philipp Hanslovsky
 * @author Stephan Saalfeld
 */
public abstract class AbstractPermutationTransform implements InvertibleTransform
{

	final protected int[] lut;

	final protected int[] inverseLut;

	/**
	 * @param lut
	 *            must be a bijective permutation over its index set, i.e. for a
	 *            lut of length n, the sorted content of the array must be
	 *            [0,...,n-1] which is the index set of the lut.
	 */
	public AbstractPermutationTransform( final int[] lut )
	{
		super();
		this.lut = lut.clone();

		this.inverseLut = new int[ lut.length ];
		for ( int i = 0; i < lut.length; ++i )
			this.inverseLut[ lut[ i ] ] = i;
	}

	public int apply( final int x )
	{
		return this.lut[ x ];
	}

	public long applyChecked( final int x )
	{
		if ( x < 0 )
			return -Long.MAX_VALUE;
		else if ( x >= this.lut.length )
			return Long.MAX_VALUE;
		else
			return this.apply( x );
	}

	public int applyInverse( final int y )
	{
		return this.inverseLut[ y ];
	}

	public long applyInverseChecked( final int y )
	{
		if ( y < 0 )
			return -Long.MAX_VALUE;
		else if ( y >= this.lut.length )
			return Long.MAX_VALUE;
		else
			return this.applyInverse( y );
	}

	public int[] getLutCopy()
	{
		return this.lut.clone();
	}

	public int[] getInverseLutCopy()
	{
		return this.inverseLut.clone();
	}

	final static public boolean checkBijectivity( final int[] lut )
	{
		final int[] inverseLut = new int[ lut.length ];
		try
		{
			for ( int i = 0; i < lut.length; ++i )
				inverseLut[ i ] = -1;
			for ( int i = 0; i < lut.length; ++i )
				inverseLut[ lut[ i ] ] = i;
			for ( int i = 0; i < lut.length; ++i )
				if ( inverseLut[ i ] == -1 )
					return false;
			return true;
		}
		catch ( final ArrayIndexOutOfBoundsException e )
		{
			return false;
		}
	}

}
