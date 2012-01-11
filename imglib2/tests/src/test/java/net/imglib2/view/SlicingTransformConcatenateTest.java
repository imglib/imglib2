package net.imglib2.view;

import static org.junit.Assert.assertTrue;
import net.imglib2.transform.integer.Slicing;
import net.imglib2.transform.integer.SlicingTransform;

import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

public class SlicingTransformConcatenateTest
{
	public static boolean testConcatenation( final SlicingTransform t1, final Slicing t2 )
	{
		if (t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println("incompatible dimensions");
			return false;
		}

		final SlicingTransform t1t2 = t1.concatenate( t2 );

		final Matrix mt1 = new Matrix( t1.getMatrix() );
		final Matrix mt2 = new Matrix( t2.getMatrix() );
		final Matrix mt1t2 = new Matrix( t1t2.getMatrix() );

		if ( mt1.times( mt2 ).minus( mt1t2 ).normF() > 0.1 ) {
			System.out.println("=======================");
			System.out.println("t1: " + t1.numSourceDimensions() + " -> " + t1.numTargetDimensions() + " (n -> m)" );
			System.out.println("t2: " + t2.numSourceDimensions() + " -> " + t2.numTargetDimensions() + " (n -> m)" );
			System.out.println("t1t2: " + t1t2.numSourceDimensions() + " -> " + t1t2.numTargetDimensions() + " (n -> m)" );

			System.out.print( "t1 = " );
			mt1.print( 1, 0 );
			System.out.print( "t2 = " );
			mt2.print( 1, 0 );
			System.out.print( "t1t2 = " );
			mt1t2.print( 1, 0 );
			System.out.print( "t1 x t2 = " );
			mt1.times( mt2 ).print( 1, 0 );

			System.out.println( "wrong result" );
			System.out.println("=======================");
			return false;
		}

		return true;
	}

	public static boolean testPreConcatenation( final Slicing t1, final SlicingTransform t2 )
	{
		if (t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println("incompatible dimensions");
			return false;
		}

		final SlicingTransform t1t2 = t2.preConcatenate( t1 );

		final Matrix mt1 = new Matrix( t1.getMatrix() );
		final Matrix mt2 = new Matrix( t2.getMatrix() );
		final Matrix mt1t2 = new Matrix( t1t2.getMatrix() );

		if ( mt1.times( mt2 ).minus( mt1t2 ).normF() > 0.1 ) {
			System.out.println("=======================");
			System.out.println("t1: " + t1.numSourceDimensions() + " -> " + t1.numTargetDimensions() + " (n -> m)" );
			System.out.println("t2: " + t2.numSourceDimensions() + " -> " + t2.numTargetDimensions() + " (n -> m)" );
			System.out.println("t1t2: " + t1t2.numSourceDimensions() + " -> " + t1t2.numTargetDimensions() + " (n -> m)" );

			System.out.print( "t1 = " );
			mt1.print( 1, 0 );
			System.out.print( "t2 = " );
			mt2.print( 1, 0 );
			System.out.print( "t1t2 = " );
			mt1t2.print( 1, 0 );
			System.out.print( "t1 x t2 = " );
			mt1.times( mt2 ).print( 1, 0 );

			System.out.println( "wrong result" );
			System.out.println("=======================");
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
		sl2.setComponentMapping( new int[] { -9, 0, 1, 2} );
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
