package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.type.numeric.IntegerType;

public final class SimpleMserEvaluationNode< T extends IntegerType< T > >
{
	/**
	 * Threshold value of the connected component.
	 */
	public final long value;

	/**
	 * Size (number of pixels) of the connected component.
	 */
	public final long size;

	public final PixelList pixelList;

	public final ArrayList< SimpleMserEvaluationNode< T > > ancestors;
	public final SimpleMserEvaluationNode< T > historyAncestor;
	public SimpleMserEvaluationNode< T > successor;
	
	/**
	 * MSER score : |Q_{i+\Delta} - Q_i| / |Q_i|. 
	 */
	public double score;
	public boolean isScoreValid;
	
	public final int n;
	public double[] mean; // mean of region (x, y, z, ...)
	public double[] cov; // independent elements of covariance of region (xx, xy, xz, ..., yy, yz, ..., zz, ...)

	public SimpleMserEvaluationNode( final SimpleMserComponent< T > component, final long delta, final SimpleMserComponentHandler.SimpleMserProcessor< T > minimaProcessor )
	{
		value = component.getValue().getIntegerLong();
		pixelList = new PixelList( component.pixelList );
		size = pixelList.size();

		ancestors = new ArrayList< SimpleMserEvaluationNode< T > >();
		SimpleMserEvaluationNode< T > node = component.getEvaluationNode();
		long historySize = 0;
		if ( node != null )
		{
			historySize = node.size;
			node = createIntermediateNodes( component.getEvaluationNode(), value, delta, minimaProcessor );
			ancestors.add( node );
			node.setSuccessor( this );
		}

		SimpleMserEvaluationNode< T > historyWinner = node;
		for ( SimpleMserComponent< T > c : component.getAncestors() )
		{
			node = createIntermediateNodes( c.getEvaluationNode(), component.getValue().getIntegerLong(), delta, minimaProcessor );
			ancestors.add( node );
			node.setSuccessor( this );
			if ( c.getSize() > historySize )
			{
				historyWinner = node;
				historySize = c.getSize();
			}
		}
		
		historyAncestor = historyWinner;
		
		n = component.n;
		mean = new double[ n ];
		cov = new double[ ( n * (n+1) ) / 2 ];
		for ( int i = 0; i < n; ++i )
			mean[ i ] = component.sumPos[ i ] / size;
		int k = 0;
		for ( int i = 0; i < n; ++i )
			for ( int j = i; j < n; ++j )
			{
				cov[ k ] = component.sumSquPos[ k ] / size - mean[ i ] * mean[ j ];
				++k;
			}

		component.setEvaluationNode( this );
		isScoreValid = computeMserScore( delta );
		if ( isScoreValid )
			for ( SimpleMserEvaluationNode< T > a : ancestors )
				a.evaluateLocalMinimum( minimaProcessor );
	}

	private SimpleMserEvaluationNode( final SimpleMserEvaluationNode< T > ancestor, final long value, final long delta, final SimpleMserComponentHandler.SimpleMserProcessor< T > minimaProcessor )
	{
		ancestors = new ArrayList< SimpleMserEvaluationNode< T > >();
		ancestors.add( ancestor );
		ancestor.setSuccessor( this );

		historyAncestor = ancestor;
		size = ancestor.size;
		pixelList = ancestor.pixelList;
		this.value = value;
		n = ancestor.n;
		mean = ancestor.mean;
		cov = ancestor.cov;

		isScoreValid = computeMserScore( delta );
		if ( isScoreValid )
			ancestor.evaluateLocalMinimum( minimaProcessor );
	}

	private SimpleMserEvaluationNode< T > createIntermediateNodes( final SimpleMserEvaluationNode< T > fromNode, final long toValue, final long delta, final SimpleMserComponentHandler.SimpleMserProcessor< T > minimaProcessor )
	{
		SimpleMserEvaluationNode< T > node = fromNode;
		for ( long v = node.value + 1; v < toValue; ++v )
			node = new SimpleMserEvaluationNode< T >( node, v, delta, minimaProcessor );
		return node;
	}

	private void setSuccessor( SimpleMserEvaluationNode< T > node )
	{
		successor = node;
	}

	/**
	 * Evaluate the mser score at this connected component.
	 * This may fail if the connected component tree is not built far enough
	 * down from the current node.
	 */
	private boolean computeMserScore( final long delta )
	{
		// we are looking for a precursor node with value == (this.value - delta)
		final long valueMinus = value - delta;
		// go back in history until we find a node with (value == valueMinus)
		SimpleMserEvaluationNode< T > node = historyAncestor;
		while ( node != null  &&  node.value > valueMinus )
			node = node.historyAncestor;
		if ( node == null )
			// we cannot compute the mser score because the history is too short.
			return false;
		score = ( size - node.size ) / ( ( double ) size );
		return true;		
	}

	/**
	 * Check whether the mser score is a local minimum at this connected
	 * component. This may fail if the mser score for this component, or the
	 * previous one in the branch are not available. (Note, that this is only
	 * called, when the mser score for the next component in the branch is
	 * available.)
	 */
	private void evaluateLocalMinimum( final SimpleMserComponentHandler.SimpleMserProcessor< T > minimaProcessor )
	{
		if ( isScoreValid && historyAncestor.isScoreValid )
			if ( ( score <= historyAncestor.score ) && ( score < successor.score ) )
				minimaProcessor.foundNewMinimum( this );				
	}

	@Override
	public String toString()
	{
		String s = "SimpleMserEvaluationNode";
		s += ", size = " + size;
		s += ", history = [";
		SimpleMserEvaluationNode< T > node = historyAncestor;
		boolean first = true;
		while ( node != null )
		{
			if ( first )
				first = false;
			else
				s += ", ";	
			s += "(" + node.value + "; " + node.size;
			if ( node.isScoreValid )
				s += " s " + node.score + ")";
			else
				s += " s --)";
			node = node.historyAncestor;
		}
		s += "]";
		return s;
	}
}
