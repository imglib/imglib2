package net.imglib2.algorithm.mser;

import net.imglib2.type.numeric.IntegerType;

public interface MserProcessor< T extends IntegerType< T >>
{
	/**
	 * Called when a {@link MserEvaluationNode} is found to be a local minimum of the MSER score.
	 * @param node
	 */
	public abstract void foundNewMinimum( MserEvaluationNode< T > node );
}
