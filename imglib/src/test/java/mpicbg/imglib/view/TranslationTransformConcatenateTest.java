package mpicbg.imglib.view;

import static org.junit.Assert.assertTrue;

import mpicbg.imglib.transform.integer.Translation;
import mpicbg.imglib.transform.integer.TranslationTransform;

import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

public class TranslationTransformConcatenateTest
{
	public static boolean testConcatenation( TranslationTransform t1, Translation t2 )
	{
		if (t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println("incompatible dimensions");
			return false;
		}

		TranslationTransform t1t2 = t1.concatenate( t2 );

		Matrix mt1 = t1.getMatrix(); 
		Matrix mt2 = t2.getMatrix(); 
		Matrix mt1t2 = t1t2.getMatrix();

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
	
	public static boolean testPreConcatenation( Translation t1, TranslationTransform t2 )
	{
		if (t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println("incompatible dimensions");
			return false;
		}

		TranslationTransform t1t2 = t2.preConcatenate( t1 );

		Matrix mt1 = t1.getMatrix(); 
		Matrix mt2 = t2.getMatrix(); 
		Matrix mt1t2 = t1t2.getMatrix();

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

	TranslationTransform tr1;
	TranslationTransform tr2;
	
	@Before
	public void setUp()
    { 
		tr1 = new TranslationTransform( 3 );
		long[] translation = new long[] {3, 4, 5};
		tr1.setTranslation( translation );

		tr2 = new TranslationTransform( new long[] {7, 8, 9} );
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
