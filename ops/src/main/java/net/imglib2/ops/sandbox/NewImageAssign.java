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

package net.imglib2.ops.sandbox;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.roi.RectangleRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * TODO
 * 
 */
public class NewImageAssign< U extends ComplexType< U >, V extends ComplexType< V >>
{

	RandomAccessibleInterval< U > img;

	double[] origin;

	double[] span;

	NewFunc< U, V > func;

	public NewImageAssign( RandomAccessibleInterval< U > img, double[] origin, double[] span, NewFunc< U, V > func )
	{
		this.img = img;
		this.origin = origin;
		this.span = span;
		this.func = func;
	}

	public void assign()
	{
		RegionOfInterest r = null;

		RectangleRegionOfInterest roi = new RectangleRegionOfInterest( origin, span );
		double[] pos = new double[ 2 ];
		Img< DoubleType > img = null;
		NewFunc< DoubleType, DoubleType > newFunc = new NewAvgFunc< DoubleType, DoubleType >( new DoubleType() );
		DoubleType output = new DoubleType();
		IterableInterval< DoubleType > ii = roi.getIterableIntervalOverROI( img );
		Cursor< DoubleType > iiC = ii.cursor();
		while ( iiC.hasNext() )
		{
			iiC.fwd();
		}
		iiC.reset();
		roi.setOrigin( new double[] { 7, 123242 } );
		while ( iiC.hasNext() )
		{
			iiC.fwd();
			// TODO - ARG
			// newFunc.evaluate(iiC, output);
			iiC.localize( pos );
			System.out.println( Arrays.toString( pos ) );
		}
		roi.setOrigin( new double[] { 0, 0 } );
		iiC.reset();
		while ( iiC.hasNext() )
		{
			iiC.fwd();
			iiC.localize( pos );
			System.out.println( Arrays.toString( pos ) );
		}
	}
}
