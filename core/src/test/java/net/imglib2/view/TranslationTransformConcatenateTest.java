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
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.view;

import static org.junit.Assert.assertTrue;
import net.imglib2.transform.integer.Translation;
import net.imglib2.transform.integer.TranslationTransform;

import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

/**
 * TODO
 * 
 */
public class TranslationTransformConcatenateTest
{
	public static boolean testConcatenation( final TranslationTransform t1, final Translation t2 )
	{
		if ( t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println( "incompatible dimensions" );
			return false;
		}

		final TranslationTransform t1t2 = t1.concatenate( t2 );

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

	public static boolean testPreConcatenation( final Translation t1, final TranslationTransform t2 )
	{
		if ( t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println( "incompatible dimensions" );
			return false;
		}

		final TranslationTransform t1t2 = t2.preConcatenate( t1 );

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

	TranslationTransform tr1;

	TranslationTransform tr2;

	@Before
	public void setUp()
	{
		tr1 = new TranslationTransform( 3 );
		final long[] translation = new long[] { 3, 4, 5 };
		tr1.setTranslation( translation );

		tr2 = new TranslationTransform( new long[] { 7, 8, 9 } );
	}

	@Test
	public void concatenateTr1Tr2()
	{
		assertTrue( testConcatenation( tr1, tr2 ) );
	}

	@Test
	public void preconcatenateTr1Tr2()
	{
		assertTrue( testPreConcatenation( tr1, tr2 ) );
	}

	@Test
	public void concatenateTr2Tr1()
	{
		assertTrue( testConcatenation( tr2, tr1 ) );
	}

	@Test
	public void preconcatenateTr2Tr1()
	{
		assertTrue( testPreConcatenation( tr2, tr1 ) );
	}
}
