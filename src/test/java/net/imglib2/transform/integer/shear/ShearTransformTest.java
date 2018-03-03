/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.transform.integer.shear;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.transform.integer.BoundingBox;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.view.TransformView;
import net.imglib2.view.Views;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Philipp Hanslovsky
 * @author Keith Schulze
 *
 */
public class ShearTransformTest
{

	final private long[] dim = new long[] { 5, 6, 7, 8 };
	final private int numDimensions = dim.length;
	final private ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( dim );
	final private Random rng = new Random();
	private int shearFactor = 3;


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		for ( FloatType i : img ) i.set( rng.nextFloat() );
		shearFactor = rng.nextInt(5);
	}


	@Test
	public void testIdentity()
	{
		for ( int refDim = 0; refDim < numDimensions; ++refDim )
		{
			for ( int shearDim = 0; shearDim < numDimensions; ++ shearDim )
			{

				if ( shearDim == refDim ) continue;

				ShearTransform tf = new ShearTransform( numDimensions, shearDim, refDim, shearFactor );
				AbstractShearTransform iv = tf.inverse();
				TransformView<FloatType> transformed = new TransformView< FloatType >( new TransformView< FloatType >( img, iv ), tf );

				ArrayCursor<FloatType> i = img.cursor();
				Cursor<FloatType> t      = Views.flatIterable( Views.interval( transformed, img ) ).cursor();

				while( t.hasNext() )
				{
					Assert.assertTrue( i.hasNext() );
					Assert.assertEquals( i.next().get(), t.next().get(), 0.0f );
				}
				Assert.assertFalse( i.hasNext() );
			}
		}
	}

	@Test
	public void simpleBoundingBoxTransformTest() {
		int shearDim = 0;
		int refDim = 2;

		ShearTransform tf = new ShearTransform( numDimensions, shearDim, refDim, shearFactor );

		BoundingBox boundingBox = new BoundingBox( img );
		BoundingBox bbTransformed = tf.transform( boundingBox );

		Assert.assertEquals( (dim[ shearDim ] - 1) + ( dim[ refDim ] - 1) * shearFactor, bbTransformed.corner2[ shearDim ] );
	}

	@Test
	public void testTransformCoord() {

		long[] source = new long[]{ 0, 1, 2, 3 };
		long[] target = new long[numDimensions];
		long[] back = new long[numDimensions];

		for (int refDim = 0; refDim < numDimensions; refDim++)
			for (int shearDim = 0; shearDim < numDimensions; shearDim++) {
				if (refDim != shearDim) {
					int coord1 = rng.nextInt((int) dim[shearDim]);
					int coord2 = rng.nextInt((int) dim[refDim]);

					source[shearDim] = coord1;
					source[refDim] = coord2;

					ShearTransform tf = new ShearTransform(numDimensions, shearDim, refDim, shearFactor);
					AbstractShearTransform iv = tf.inverse();

					tf.apply(source, target);
					iv.apply(target, back);

					Assert.assertEquals(source[shearDim] + source[refDim] * shearFactor, target[shearDim]);
					Assert.assertEquals(source[shearDim], back[shearDim]);
				}
			}
	}

	@Test
	public void testTransform() {
		for ( int refDim = 0; refDim < numDimensions; ++refDim )
		{
			for ( int shearDim = 0; shearDim < numDimensions; ++ shearDim )
			{
				if ( shearDim == refDim ) continue;

				ShearTransform tf = new ShearTransform( numDimensions, shearDim, refDim, shearFactor );
				AbstractShearTransform iv = tf.inverse();
				ExtendedRandomAccessibleInterval<FloatType, ArrayImg<FloatType, FloatArray>> extended =
					Views.extendValue( img, new FloatType( Float.NaN ) );
				TransformView<FloatType> transformed = new TransformView< FloatType >( extended, iv );
				BoundingBox boundingBox = new BoundingBox( img );
				BoundingBox bbTransformed = tf.transform( boundingBox );

				// BoundingBox is indexed from 0 not 1; need to account for that by subtracting 1 from dims;
				Assert.assertEquals( ( dim[ shearDim ] - 1 ) + ( dim[ refDim ] - 1 ) * shearFactor, bbTransformed.corner2[ shearDim ] );

				IntervalView<FloatType> viewTransformed = Views.shear( extended, img, shearDim, refDim, shearFactor );

				Interval interval = bbTransformed.getInterval();
				Cursor<FloatType> i = img.cursor();
				RandomAccess<FloatType> t = Views.interval( transformed, interval ).randomAccess();
				RandomAccess<FloatType> v = viewTransformed.randomAccess();

				Assert.assertEquals( interval.numDimensions(), viewTransformed.numDimensions() );
				for ( int d = 0; d < interval.numDimensions(); ++d )
				 	Assert.assertEquals( interval.dimension( d ), viewTransformed.dimension( d ) );

				while( i.hasNext() )
				{
					i.fwd();
					long shearedCoord = i.getLongPosition( shearDim ) + i.getLongPosition( refDim ) * shearFactor;
					t.setPosition( i );
					t.setPosition( shearedCoord , shearDim );
					v.setPosition( i );
					v.setPosition( shearedCoord, shearDim);
					Assert.assertEquals( i.get().get(), t.get().get(), 0.0f );
					Assert.assertEquals( t.get().get(), v.get().get(), 0.0f );
				}

			}
		}
	}


	@Test
	public void testInverseTransform()
	{
		for ( int refDim = 0; refDim < numDimensions; ++refDim )
		{
			for ( int shearDim = 0; shearDim < numDimensions; ++ shearDim )
			{
				if ( shearDim == refDim ) continue;

				ShearTransform tf = new ShearTransform( numDimensions, shearDim, refDim, shearFactor );
				AbstractShearTransform iv = tf.inverse();
				ExtendedRandomAccessibleInterval<FloatType, ArrayImg<FloatType, FloatArray>> extended = Views.extendValue( img, new FloatType( Float.NaN ) );
				TransformView<FloatType> transformed = new TransformView< FloatType >( extended, tf );
				BoundingBox boundingBox = new BoundingBox( img );
				BoundingBox bbTransformed = iv.transform( boundingBox );

				IntervalView<FloatType> viewTransformed = Views.unshear( extended, img, shearDim, refDim, shearFactor );

				// BoundingBox is indexed from 0 not 1; need to account for that by subtracting 1 from dims;
				Assert.assertEquals( ( dim[ shearDim ] - 1 ) - ( dim[ refDim ] - 1 ) * shearFactor, bbTransformed.corner2[ shearDim ] );


				Interval interval   = bbTransformed.getInterval();
				OutOfBounds<FloatType> i = extended.randomAccess();
				Cursor<FloatType> t      = Views.flatIterable( Views.interval( transformed, interval ) ).cursor();
				Cursor<FloatType> v      = Views.flatIterable( viewTransformed ).cursor();

				Assert.assertEquals( interval.numDimensions(), viewTransformed.numDimensions() );
				for ( int d = 0; d < interval.numDimensions(); ++d )
					Assert.assertEquals( interval.dimension( d ), viewTransformed.dimension( d ) );

				while( t.hasNext() )
				{
					t.fwd();
					v.fwd();
					i.setPosition( t );
					i.setPosition( i.getLongPosition( shearDim ) + i.getLongPosition( refDim ) * shearFactor, shearDim );
					Assert.assertEquals( i.get().get(), t.get().get(), 0.0f );
					Assert.assertEquals( t.get().get(), v.get().get(), 0.0f );
				}

			}
		}
	}
}
