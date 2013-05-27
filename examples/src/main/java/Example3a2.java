/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */
import ij.ImageJ;

import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Perform a generic min & max search
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example3a2
{
	public Example3a2()
	{
		// it will work as well on a normal ArrayList
		ArrayList< FloatType > list = new ArrayList< FloatType >();

		// put values 0 to 10 into the ArrayList
		for ( int i = 0; i <= 10; ++i )
			list.add( new FloatType( i ) );

		// create two empty variables
		FloatType min = new FloatType();
		FloatType max = new FloatType();

		// compute min and max of the ArrayList
		computeMinMax( list, min, max );

		System.out.println( "minimum Value (arraylist): " + min );
		System.out.println( "maximum Value (arraylist): " + max );
	}

	/**
	 * Compute the min and max for any {@link Iterable}, like an {@link Img}.
	 *
	 * The only functionality we need for that is to iterate. Therefore we need no {@link Cursor}
	 * that can localize itself, neither do we need a {@link RandomAccess}. So we simply use the
	 * most simple interface in the hierarchy.
	 *
	 * @param input - the input that has to just be {@link Iterable}
	 * @param min - the type that will have min
	 * @param max - the type that will have max
	 */
	public < T extends Comparable< T > & Type< T > > void computeMinMax(
		final Iterable< T > input, final T min, final T max )
	{
		// create a cursor for the image (the order does not matter)
		final Iterator< T > iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set( type );
		max.set( type );

		// loop over the rest of the data and determine min and max value
		while ( iterator.hasNext() )
		{
			// we need this type more than once
			type = iterator.next();

			if ( type.compareTo( min ) < 0 )
				min.set( type );

			if ( type.compareTo( max ) > 0 )
				max.set( type );
		}
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example3a2();
	}
}
