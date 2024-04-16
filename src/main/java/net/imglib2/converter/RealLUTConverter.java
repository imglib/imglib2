/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.converter;

import net.imglib2.display.AbstractLinearRange;
import net.imglib2.display.ColorTable;
import net.imglib2.display.ColorTable8;
import net.imglib2.display.projector.composite.CompositeXYProjector;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

/**
 * RealLUTConverter contains a {@link ColorTable8}, through which samples are
 * filtered. Input values are interpreted as indices into the color table.
 * 
 * @see CompositeXYProjector
 * @see RealARGBConverter for the code upon which this class was based.
 * 
 * @author Stephan Saalfeld
 * @author Grant Harris
 * @author Curtis Rueden
 */
public class RealLUTConverter< R extends RealType< R >> extends
		AbstractLinearRange implements Converter< R, ARGBType >
{

	private ColorTable lut = null;

	public RealLUTConverter()
	{
		super();
	}

	public RealLUTConverter( final double min, final double max,
			final ColorTable lut )
	{
		super( min, max );
		setLUT( lut );
	}

	public ColorTable getLUT()
	{
		return lut;
	}

	public void setLUT( final ColorTable lut )
	{
		this.lut = lut == null ? new ColorTable8() : lut;
	}

	@Override
	public void convert( final R input, final ARGBType output )
	{
		final double a = input.getRealDouble();
		final int argb = lut.lookupARGB( min, max, a );
		output.set( argb );
	}

}
