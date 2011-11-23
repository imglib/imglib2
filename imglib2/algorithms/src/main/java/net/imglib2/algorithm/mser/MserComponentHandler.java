package net.imglib2.algorithm.mser;

import java.util.Comparator;

import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.type.Type;

public class MserComponentHandler< T extends Type< T > > implements Component.Handler< MserComponentIntermediate< T > >
{
	public interface SimpleMserProcessor< T extends Type< T > >
	{
		/**
		 * Called when a {@link MserEvaluationNode} is found to be a local minimum of the MSER score.
		 * @param node
		 */
		public abstract void foundNewMinimum( MserEvaluationNode< T > node );
	}

	final Comparator< T > comparator;
	
	final SimpleMserProcessor< T > procNewMser;
	
	final ComputeDeltaValue< T > delta;
	
	public MserComponentHandler( final Comparator< T > comparator, final ComputeDeltaValue< T > delta, final SimpleMserProcessor< T > procNewMser )
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
