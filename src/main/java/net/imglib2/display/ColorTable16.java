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
package net.imglib2.display;

/**
 * 16-bit color lookup table.
 * 
 * @author Stephan Saalfeld
 * @author Curtis Rueden
 */
public class ColorTable16 extends AbstractArrayColorTable< short[] >
{

	/**
	 * Initializes a 16-bit color table with a linear grayscale ramp.
	 */
	public ColorTable16()
	{
		super( gray() );
	}

	/**
	 * Initializes a 16-bit color table with the given table values.
	 */
	public ColorTable16( final short[]... values )
	{
		super( values );
	}

	@Override
	public int getLength()
	{
		return values[ 0 ].length;
	}

	@Override
	public int getBits()
	{
		return 16;
	}

	@Override
	public int get( final int comp, final int bin )
	{
		// convert 0xffff to 0xff
		return getNative( comp, bin ) >> 8;
	}

	@Override
	public int getNative( final int c, final int i )
	{
		// returns short value as unsigned, expressed as int
		return values[ c ][ i ] & 0xffff;
	}

	@Override
	public int getResampled( final int comp, final int bins, final int bin )
	{
		final int newBin = ( int ) ( ( long ) getLength() * bin / bins );
		return get( comp, newBin );
	}

	// -- Helper methods --
	/**
	 * Creates a linear grayscale ramp with 3 components and 65536 values.
	 */
	private static short[][] gray()
	{
		final short[][] gray = new short[ 3 ][ 65536 ];
		for ( int j = 0; j < gray.length; j++ )
		{
			for ( int i = 0; i < gray[ j ].length; i++ )
			{
				gray[ j ][ i ] = ( short ) i;
			}
		}
		return gray;
	}
}
