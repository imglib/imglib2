package net.imglib2.view;

import static org.junit.Assert.assertTrue;

import net.imglib2.transform.integer.Mixed;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.transform.integer.TranslationTransform;

import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

public class MixedTransformConcatenateTest
{
	public static boolean testConcatenation( MixedTransform t1, Mixed t2 )
	{
		if (t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println("incompatible dimensions");
			return false;
		}

		MixedTransform t1t2 = t1.concatenate( t2 );

		Matrix mt1 = new Matrix( t1.getMatrix() ); 
		Matrix mt2 = new Matrix( t2.getMatrix() ); 
		Matrix mt1t2 = new Matrix( t1t2.getMatrix() );

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
	
	public static boolean testPreConcatenation( Mixed t1, MixedTransform t2 )
	{
		if (t1.numSourceDimensions() != t2.numTargetDimensions() )
		{
			System.out.println("incompatible dimensions");
			return false;
		}

		MixedTransform t1t2 = t2.preConcatenate( t1 );

		Matrix mt1 = new Matrix( t1.getMatrix() ); 
		Matrix mt2 = new Matrix( t2.getMatrix() ); 
		Matrix mt1t2 = new Matrix( t1t2.getMatrix() );

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
	
	MixedTransform tr1;
	MixedTransform tr2;
	MixedTransform perm1;
	MixedTransform rot1;
	MixedTransform proj1;
	MixedTransform proj2;
	MixedTransform comp1;
	TranslationTransform translation1;
	
	@Before
	public void setUp()
    { 
		tr1 = new MixedTransform( 3, 3 );
		long[] translation = new long[] {3, 4, 5};
		tr1.setTranslation( translation );

		tr2 = new MixedTransform( 3, 3 );
		translation = new long[] {7, 8, 9};
		tr2.setTranslation( translation );

		perm1 = new MixedTransform( 3, 3 );
		boolean[] zero = new boolean[] {false, false, false};
		boolean[] inv = new boolean[] {false, false, false};
		int[] component = new int[] {0, 2, 1};
		perm1.setComponentZero( zero );
		perm1.setComponentMapping( component );
		perm1.setComponentInversion( inv );
		
		rot1 = new MixedTransform( 3, 3 );
		zero = new boolean[] {false, false, false};
		inv = new boolean[] {false, true, false};
		component = new int[] {1, 0, 2};
		rot1.setComponentZero( zero );
		rot1.setComponentMapping( component );
		rot1.setComponentInversion( inv );
		
		proj1 = new MixedTransform( 3, 2 );

		proj2 = new MixedTransform( 2, 3 );

		comp1 = rot1.concatenate( tr2 );
		
		translation1 = new TranslationTransform( new long[] {2011, 3, 24} );
    }

	@Test
	public void concatenateProj1Tr1()
	{
		assertTrue( testConcatenation( proj1, tr1 ) );
	}

	@Test
	public void preconcatenateProj1Tr1()
	{
		assertTrue( testPreConcatenation( proj1, tr1 ) );
	}

	@Test
	public void concatenateTr11Tr2()
	{
		assertTrue( testConcatenation( tr1, tr2 ) );
	}

	@Test
	public void preconcatenateTr1Tr2()
	{
		assertTrue( testPreConcatenation( tr1, tr2 ) );
	}
	
	@Test
	public void concatenateTr1Perm1()
	{
		assertTrue( testConcatenation( tr1, perm1 ) );
	}

	@Test
	public void preconcatenateTr1Perm1()
	{
		assertTrue( testPreConcatenation( tr1, perm1 ) );
	}
	
	@Test
	public void concatenateTr1Rot1()
	{
		assertTrue( testConcatenation( tr1, rot1 ) );
	}

	@Test
	public void preconcatenateTr1Rot1()
	{
		assertTrue( testPreConcatenation( tr1, rot1 ) );
	}
	
	@Test
	public void concatenateRot1Tr1()
	{
		assertTrue( testConcatenation( rot1, tr1 ) );
	}

	@Test
	public void preconcatenateRot1Tr1()
	{
		assertTrue( testPreConcatenation( rot1, tr1 ) );
	}
	
	@Test
	public void concatenateProj1Proj2()
	{
		assertTrue( testConcatenation( proj1, proj2 ) );
	}

	@Test
	public void preconcatenateProj1Proj2()
	{
		assertTrue( testPreConcatenation( proj1, proj2 ) );
	}

	@Test
	public void concatenateProj2Proj1()
	{
		assertTrue( testConcatenation( proj2, proj1 ) );
	}

	@Test
	public void preconcatenateProj2Proj1()
	{
		assertTrue( testPreConcatenation( proj2, proj1 ) );
	}	
	
	@Test
	public void concatenateComp1Tr1()
	{
		assertTrue( testConcatenation( comp1, tr1 ) );
	}

	@Test
	public void preconcatenateComp1Tr1()
	{
		assertTrue( testPreConcatenation( comp1, tr1 ) );
	}

	@Test
	public void concatenateTr1Comp1()
	{
		assertTrue( testConcatenation( tr1, comp1 ) );
	}
	
	@Test
	public void preconcatenateTr1Comp1()
	{
		assertTrue( testPreConcatenation( tr1, comp1 ) );
	}
	
	@Test
	public void concatenateComp1Rot1()
	{
		assertTrue( testConcatenation( comp1, rot1 ) );
	}

	@Test
	public void preconcatenateComp1Rot1()
	{
		assertTrue( testPreConcatenation( comp1, rot1 ) );
	}

	@Test
	public void concatenateRot1Comp1()
	{
		assertTrue( testConcatenation( rot1, comp1 ) );
	}
	
	@Test
	public void preconcatenateRot1Comp1()
	{
		assertTrue( testPreConcatenation( rot1, comp1 ) );
	}
	
	@Test
	public void concatenateProj1Comp1()
	{
		assertTrue( testConcatenation( proj1, comp1 ) );
	}
	
	@Test
	public void preconcatenateProj1Comp1()
	{
		assertTrue( testPreConcatenation( proj1, comp1 ) );
	}
	
	@Test
	public void concatenateComp1Proj2()
	{
		assertTrue( testConcatenation( comp1, proj2 ) );
	}

	@Test
	public void preconcatenateComp1Proj2()
	{
		assertTrue( testPreConcatenation( comp1, proj2 ) );
	}

	@Test
	public void concatenateComp1Translation1()
	{
		assertTrue( testConcatenation( comp1, translation1 ) );
	}

	@Test
	public void preconcatenateTranslation1Comp1()
	{
		assertTrue( testPreConcatenation( translation1, comp1 ) );
	}

	public static void main( String[] args )
	{
		MixedTransformConcatenateTest test = new MixedTransformConcatenateTest();
		test.setUp();
		
		Matrix m_tr1 = new Matrix( test.tr1.getMatrix() ); 
		Matrix m_tr2 = new Matrix( test.tr2.getMatrix() ); 
		Matrix m_perm1 = new Matrix( test.perm1.getMatrix() ); 
		Matrix m_rot1 = new Matrix( test.rot1.getMatrix() ); 
		Matrix m_proj1 = new Matrix( test.proj1.getMatrix() ); 
		Matrix m_proj2 = new Matrix( test.proj2.getMatrix() ); 
		Matrix m_comp1 = new Matrix( test.comp1.getMatrix() ); 

		System.out.print( "tr1 = " );
		m_tr1.print( 1, 0 );
		
		System.out.print( "tr2 = " );
		m_tr2.print( 1, 0 );
		
		System.out.print( "perm1 = " );
		m_perm1.print( 1, 0 );
		
		System.out.print( "rot1 = " );
		m_rot1.print( 1, 0 );
		
		System.out.print( "proj1 = " );
		m_proj1.print( 1, 0 );
		
		System.out.print( "proj2 = " );
		m_proj2.print( 1, 0 );
		
		System.out.print( "comp1 = " );
		m_comp1.print( 1, 0 );
	}
}
