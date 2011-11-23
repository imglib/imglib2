package net.imglib2.algorithm.mser;

import java.util.Comparator;

import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.type.Type;

public class MserComponentHandler< T extends Type< T > > implements Component.Handler< MserComponentIntermediate< T > >
{
	final Comparator< T > comparator;
	
	final MserTree< T > procNewMser;
	
	final ComputeDeltaValue< T > delta;
	
	public MserComponentHandler( final Comparator< T > comparator, final ComputeDeltaValue< T > delta, final MserTree< T > procNewMser )
	{
		this.comparator = comparator;
		this.delta = delta;
		this.procNewMser = procNewMser;
	}

	@Override
	public void emit( MserComponentIntermediate< T > component )
	{
		new MserEvaluationNode< T >( component, comparator, delta, procNewMser );
		component.clearAncestors();
	}
}
