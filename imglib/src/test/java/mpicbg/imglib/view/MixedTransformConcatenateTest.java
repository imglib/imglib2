package mpicbg.imglib.view;

import mpicbg.imglib.view.MixedTransform;
import Jama.Matrix;

public class MixedTransformConcatenateTest
{
	public static void testConcatenation( MixedTransform t1, MixedTransform t2 )
	{
		if (t1.sourceDim() != t2.targetDim() )
		{
			System.out.println("incompatible dimensions");
			return;
		}

		MixedTransform t1t2 = new MixedTransform( t2.sourceDim(), t1.targetDim() );

		MixedTransform.concatenate( t1, t2, t1t2 );
		
		Matrix mt1 = t1.getMatrix(); 
		Matrix mt2 = t2.getMatrix(); 
		Matrix mt1t2 = t1t2.getMatrix();

		if ( mt1.times( mt2 ).minus( mt1t2 ).normF() > 0.1 ) {
			System.out.println("t1: " + t1.sourceDim() + " -> " + t1.targetDim() + " (n -> m)" );
			System.out.println("t2: " + t2.sourceDim() + " -> " + t2.targetDim() + " (n -> m)" );
			System.out.println("t1t2: " + t1t2.sourceDim() + " -> " + t1t2.targetDim() + " (n -> m)" );			

			System.out.print( "t1 = " );
			mt1.print( 1, 0 );
			System.out.print( "t2 = " );
			mt2.print( 1, 0 );
			System.out.print( "t1t2 = " );
			mt1t2.print( 1, 0 );
			System.out.print( "t1 x t2 = " );
			mt1.times( mt2 ).print( 1, 0 );

			System.out.println( "wrong result" );
		} else {
			System.out.println( "correct" );
		}
	}
	
	public static void main( String[] args )
	{
		MixedTransform tr1 = new MixedTransform( 3, 3 );
		long[] translation = new long[] {3, 4, 5};
		tr1.setTranslation( translation );
		Matrix m_tr1 = tr1.getMatrix(); 

		MixedTransform tr2 = new MixedTransform( 3, 3 );
		translation = new long[] {7, 8, 9};
		tr2.setTranslation( translation );
		Matrix m_tr2 = tr2.getMatrix(); 

		MixedTransform perm1 = new MixedTransform( 3, 3 );
		boolean[] zero = new boolean[] {false, false, false};
		boolean[] inv = new boolean[] {false, false, false};
		int[] component = new int[] {0, 1, 2};
		perm1.setPermutation( zero, component, inv );
		Matrix m_perm1 = perm1.getMatrix(); 

		MixedTransform rot1 = new MixedTransform( 3, 3 );
		zero = new boolean[] {false, false, false};
		inv = new boolean[] {false, true, false};
		component = new int[] {1, 0, 2};
		rot1.setPermutation( zero, component, inv );
		Matrix m_rot1 = rot1.getMatrix(); 

		MixedTransform proj1 = new MixedTransform( 3, 2 );
		Matrix m_proj1 = proj1.getMatrix(); 

		MixedTransform proj2 = new MixedTransform( 2, 3 );
		Matrix m_proj2 = proj2.getMatrix(); 

		MixedTransform comp1 = new MixedTransform( 3, 3 );
		MixedTransform.concatenate( rot1, tr2, comp1 );
		Matrix m_comp1 = comp1.getMatrix(); 


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
		
		System.out.println("=======================");
		testConcatenation( proj1, tr1 );
		System.out.println("=======================");
		testConcatenation( tr1, tr2 );
		System.out.println("=======================");
		testConcatenation( tr1, perm1 );
		System.out.println("=======================");
		testConcatenation( tr1, rot1 );
		System.out.println("=======================");
		testConcatenation( rot1, tr1 );
		System.out.println("=======================");
		testConcatenation( proj2, proj1 );
		System.out.println("=======================");
		testConcatenation( proj1, proj2 );
		System.out.println("=======================");
		testConcatenation( comp1, tr1 );
		System.out.println("=======================");
		testConcatenation( tr1, comp1 );
		System.out.println("=======================");
		testConcatenation( comp1, rot1 );
		System.out.println("=======================");
		testConcatenation( rot1, comp1 );
		System.out.println("=======================");
		testConcatenation( proj1, comp1 );
		System.out.println("=======================");
		testConcatenation( comp1, proj2 );
	}
}
