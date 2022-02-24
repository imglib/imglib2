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

package net.imglib2.view;

import static org.junit.Assert.assertTrue;
import net.imglib2.transform.integer.Slicing;
import net.imglib2.transform.integer.SlicingTransform;

import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

/**
 * TODO
 * 
 */
public class SlicingTransformConcatenateTest
{
	public static boolean testConcatenation( final SlicingTransform t1, final Slicing t2 )
	{
		if ( t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println( "incompatible dimensions" );
			return false;
		}

		final SlicingTransform t1t2 = t1.concatenate( t2 );

		final Matrix mt1 = new Matrix( t1.getMatrix() );
		final Matrix mt2 = new Matrix( t2.getMatrix() );
		final Matrix mt1t2 = new Matrix( t1t2.getMatrix() );

		if ( mt1.times( mt2 ).minus( mt1t2 ).normF() > 0.1 )
		{
			System.out.println( "=======================" );
			System.out.println( "t1: " + t1.numSourceDimensions() + " -> " + t1.numTargetDimensions() + " (n -> m)" );
			System.out.println( "t2: " + t2.numSourceDimensions() + " -> " + t2.numTargetDimensions() + " (n -> m)" );
			System.out.println( "t1t2: " + t1t2.numSourceDimensions() + " -> " + t1t2.numTargetDimensions() + " (n -> m)" );

			System.out.print( "t1 = " );
			mt1.print( 1, 0 );
			System.out.print( "t2 = " );
			mt2.print( 1, 0 );
			System.out.print( "t1t2 = " );
			mt1t2.print( 1, 0 );
			System.out.print( "t1 x t2 = " );
			mt1.times( mt2 ).print( 1, 0 );

			System.out.println( "wrong result" );
			System.out.println( "=======================" );
			return false;
		}

		return true;
	}

	public static boolean testPreConcatenation( final Slicing t1, final SlicingTransform t2 )
	{
		if ( t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println( "incompatible dimensions" );
			return false;
		}

		final SlicingTransform t1t2 = t2.preConcatenate( t1 );

		final Matrix mt1 = new Matrix( t1.getMatrix() );
		final Matrix mt2 = new Matrix( t2.getMatrix() );
		final Matrix mt1t2 = new Matrix( t1t2.getMatrix() );

		if ( mt1.times( mt2 ).minus( mt1t2 ).normF() > 0.1 )
		{
			System.out.println( "=======================" );
			System.out.println( "t1: " + t1.numSourceDimensions() + " -> " + t1.numTargetDimensions() + " (n -> m)" );
			System.out.println( "t2: " + t2.numSourceDimensions() + " -> " + t2.numTargetDimensions() + " (n -> m)" );
			System.out.println( "t1t2: " + t1t2.numSourceDimensions() + " -> " + t1t2.numTargetDimensions() + " (n -> m)" );

			System.out.print( "t1 = " );
			mt1.print( 1, 0 );
			System.out.print( "t2 = " );
			mt2.print( 1, 0 );
			System.out.print( "t1t2 = " );
			mt1t2.print( 1, 0 );
			System.out.print( "t1 x t2 = " );
			mt1.times( mt2 ).print( 1, 0 );

			System.out.println( "wrong result" );
			System.out.println( "=======================" );
			return false;
		}

		return true;
	}

	SlicingTransform sl1;

	SlicingTransform sl2;

	@Before
	public void setUp()
	{
		sl1 = new SlicingTransform( 2, 3 );
		sl1.setComponentMapping( new int[] { 0, 1, -9 } );
		sl1.setComponentZero( new boolean[] { false, false, true } );
		sl1.setTranslation( new long[] { 0, 0, 100 } );

		sl2 = new SlicingTransform( 3, 4 );
		sl2.setComponentMapping( new int[] { -9, 0, 1, 2 } );
		sl2.setComponentZero( new boolean[] { true, false, false, false } );
		sl2.setTranslation( new long[] { 1287, 0, 0, 0 } );
	}

	@Test
	public void concatenateTr1Tr2()
	{
		assertTrue( testConcatenation( sl2, sl1 ) );
	}

	@Test
	public void preconcatenateTr1Tr2()
	{
		assertTrue( testPreConcatenation( sl2, sl1 ) );
	}
}
