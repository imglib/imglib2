/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import net.imglib2.type.numeric.ARGBType;
import net.imglib2.util.Binning;

/**
 * Abstract superclass for array-based color lookup tables.
 * 
 * @author Stephan Saalfeld
 * @author Curtis Rueden
 * @author Mark Hiner
 */
public abstract class AbstractArrayColorTable< T > implements ArrayColorTable< T >
{

	// -- Fields --

	/**
	 * Actual color table values.
	 */
	protected final T[] values;

	// -- Constructor --

	/**
	 * Initializes a color table with the given table values.
	 */
	public AbstractArrayColorTable( final T... values )
	{
		this.values = values;
	}

	// -- ArrayColorTable methods --

	@Override
	public T[] getValues()
	{
		return values.clone();
	}

	@Override
	public int argb( final int i )
	{
		final int r = values.length > 0 ? get( ColorTable.RED, i ) : 0;
		final int g = values.length > 1 ? get( ColorTable.GREEN, i ) : 0;
		final int b = values.length > 2 ? get( ColorTable.BLUE, i ) : 0;
		final int a = values.length > 3 ? get( ColorTable.ALPHA, i ) : 0xff;
		return ARGBType.rgba( r, g, b, a );
	}

	// -- ColorTable methods --

	@Override
	public int lookupARGB( final double min, final double max, final double value )
	{
		final int bins = getLength();
		final int bin = Binning.valueToBin( bins, min, max, value );
		return argb( bin );
	}

	@Override
	public int getComponentCount()
	{
		return values.length;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Value is unsigned 8 bits.
	 * </p>
	 */
	@Override
	public abstract int get( final int comp, final int bin );

	/**
	 * {@inheritDoc}
	 * <p>
	 * Value is unsigned 8 bits.
	 * </p>
	 */
	@Override
	public abstract int getResampled( final int comp, final int bins, final int bin );

}
