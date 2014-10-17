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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package tests.labeling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.algorithm.labeling.Watershed;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Fraction;

import org.junit.Test;

/**
 * TODO
 * 
 * @author Lee Kamentsky
 */
public class WatershedTest
{
	private void testSeededCase2D( final int[][] image, final int[][] seeds, final int[][] expected, long[][] structuringElement, final int background )
	{
		final long[] imageDimensions = new long[] { image.length, image[ 0 ].length };
		final long[] seedDimensions = new long[] { seeds.length, seeds[ 0 ].length };
		final long[] outputDimensions = new long[] { expected.length, expected[ 0 ].length };
		final NativeImgLabeling< Integer, IntType > seedLabeling = new NativeImgLabeling< Integer, IntType >( new ArrayImgFactory< IntType >().create( seedDimensions, new IntType() ) );
		final NativeImgLabeling< Integer, IntType > outputLabeling = new NativeImgLabeling< Integer, IntType >( new ArrayImgFactory< IntType >().create( outputDimensions, new IntType() ) );
		final NativeImg< IntType, ? extends IntAccess > imageImage = new ArrayImgFactory< IntType >().createIntInstance( imageDimensions, new Fraction() );
		imageImage.setLinkedType( new IntType( imageImage ) );
		/*
		 * Fill the image.
		 */
		final Cursor< IntType > ic = imageImage.localizingCursor();
		final int[] position = new int[ imageImage.numDimensions() ];
		while ( ic.hasNext() )
		{
			final IntType t = ic.next();
			ic.localize( position );
			t.set( image[ position[ 0 ] ][ position[ 1 ] ] );
		}
		/*
		 * Fill the seeded image
		 */
		final Cursor< LabelingType< Integer >> sc = seedLabeling.localizingCursor();
		while ( sc.hasNext() )
		{
			final LabelingType< Integer > t = sc.next();
			sc.localize( position );
			final int seedLabel = seeds[ position[ 0 ] ][ position[ 1 ] ];
			if ( seedLabel == background )
				continue;
			t.setLabel( seedLabel );
		}
		if ( structuringElement == null )
		{
			structuringElement = AllConnectedComponents.getStructuringElement( 2 );
		}
		/*
		 * Run the seeded watershed algorithm
		 */
		final Watershed< IntType, Integer > watershed = new Watershed< IntType, Integer >();
		watershed.setSeeds( seedLabeling );
		watershed.setIntensityImage( imageImage );
		watershed.setStructuringElement( structuringElement );
		watershed.setOutputLabeling( outputLabeling );
		assertTrue( watershed.process() );
		/*
		 * Check against expected
		 */
		final Cursor< LabelingType< Integer >> oc = outputLabeling.localizingCursor();
		while ( oc.hasNext() )
		{
			final LabelingType< Integer > t = oc.next();
			oc.localize( position );
			final int expectedLabel = expected[ position[ 0 ] ][ position[ 1 ] ];
			final List< Integer > l = t.getLabeling();
			if ( expectedLabel == background )
			{
				assertTrue( l.isEmpty() );
			}
			else
			{
				assertEquals( l.size(), 1 );
				assertEquals( l.get( 0 ).intValue(), expectedLabel );
			}
		}
	}

	@Test
	public final void testEmpty()
	{
		testSeededCase2D( new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } }, new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } }, new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } }, null, 0 );
	}

	@Test
	public final void testOne()
	{
		testSeededCase2D( new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } }, new int[][] { { 0, 0, 0 }, { 0, 1, 0 }, { 0, 0, 0 } }, new int[][] { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } }, null, 0 );
	}

	@Test
	public final void testTwo()
	{
		testSeededCase2D( new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 1, 1, 1 }, { 0, 0, 0 } }, new int[][] { { 0, 1, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 2, 0 } }, new int[][] { { 1, 1, 1 }, { 1, 1, 1 }, { 2, 2, 2 }, { 2, 2, 2 } }, null, 0 );
	}

	@Test
	public final void testBig()
	{
		// Make an image that's composed of two rectangles that require
		// propagation.
		final int[][] image = new int[ 9 ][ 11 ];
		for ( int i = 0; i < image.length; i++ )
		{
			image[ i ][ image[ 0 ].length / 2 ] = 1;
		}
		// The seeds are placed asymetrically so that the closer to the middle
		// (= # 2) will propagate first to the ridge.
		final int[][] seeds = new int[ 9 ][ 11 ];
		seeds[ 4 ][ 0 ] = 1;
		seeds[ 4 ][ 6 ] = 2;
		final int[][] expected = new int[ 9 ][ 11 ];
		for ( int i = 0; i < image.length; i++ )
		{
			for ( int j = 0; j < image[ 0 ].length; j++ )
			{
				expected[ i ][ j ] = ( j < image[ 0 ].length / 2 ) ? 1 : 2;
			}
		}
		testSeededCase2D( image, seeds, expected, null, 0 );
	}
}
