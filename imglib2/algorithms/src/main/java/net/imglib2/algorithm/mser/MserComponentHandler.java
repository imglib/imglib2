package net.imglib2.algorithm.mser;

import net.imglib2.type.numeric.IntegerType;

public class MserComponentHandler< T extends IntegerType< T > > implements ComponentHandler< MserComponent< T > >
{
	final T delta;

	public MserComponentHandler( final T delta )
	{
		this.delta = delta;
	}

	@Override
	public void emit( MserComponent< T > component )
	{
		// debug out:
		System.out.println( "MserComponentHandler.emit " + component );

		MserEvaluationNode< T > evalNode = new MserEvaluationNode< T >( component );
		System.out.println( evalNode );
		
		//evalNode.evaluateHistory( delta );

		component.clearAncestors();
		component.updateLastEmitSize();
	}
	
}
