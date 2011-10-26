package net.imglib2.algorithm.mser;

import net.imglib2.type.numeric.IntegerType;

public class MserComponentHandler< T extends IntegerType< T > > implements Component.Handler< MserComponent< T > >, MserProcessor< T >
{
	final T delta;
	final MserProcessor< T > p;

	public MserComponentHandler( final T delta, final MserProcessor< T > p )
	{
		this.delta = delta;
		this.p = p;
	}

	@Override
	public void emit( MserComponent< T > component )
	{
		// debug out:
//		System.out.println( "MserComponentHandler.emit " + component );

		MserEvaluationNode< T > evalNode = new MserEvaluationNode< T >( component );
//		System.out.println( evalNode );
		
		evalNode.computeScoresForHistory( delta );
		evalNode.evaluateLocalMinimaForHistory( p );
		
		component.clearAncestors();
		component.updateLastEmitSize();
	}
	
	/* (non-Javadoc)
	 * @see net.imglib2.algorithm.mser.MserProcessor#foundMser(net.imglib2.algorithm.mser.MserEvaluationNode)
	 */
	@Override
	public void foundNewMinimum( MserEvaluationNode< T > node )
	{
		System.out.println( "found MSER " + node );
	}
}
