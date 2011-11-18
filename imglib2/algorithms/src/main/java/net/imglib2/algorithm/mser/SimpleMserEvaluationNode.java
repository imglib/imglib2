package net.imglib2.algorithm.mser;

import java.util.ArrayList;
import java.util.Comparator;

import net.imglib2.type.numeric.RealType;

public final class SimpleMserEvaluationNode< T extends RealType< T > >
{
	/**
	 * Threshold value of the connected component.
	 */
	public final T value;

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

	/**
	 * MSERs associated to this region or its children. To build up the MSER
	 * tree.
	 */
	final ArrayList< SimpleMserTree< T >.Mser > mserThisOrAncestors;

	public SimpleMserEvaluationNode( final SimpleMserComponent< T > component, final Comparator< T > comparator, final ComputeDeltaValue< T > delta, final SimpleMserComponentHandler.SimpleMserProcessor< T > minimaProcessor )
	{
		value = component.getValue().copy();
		pixelList = new PixelList( component.pixelList );
		size = pixelList.size();

		ancestors = new ArrayList< SimpleMserEvaluationNode< T > >();
		SimpleMserEvaluationNode< T > node = component.getEvaluationNode();
		long historySize = 0;
		if ( node != null )
		{
			historySize = node.size;
			node = new SimpleMserEvaluationNode< T >( node, value, comparator, delta, minimaProcessor );
			ancestors.add( node );
			node.setSuccessor( this );
		}

		SimpleMserEvaluationNode< T > historyWinner = node;
		for ( SimpleMserComponent< T > c : component.getAncestors() )
		{
			node = new SimpleMserEvaluationNode< T >( c.getEvaluationNode(), value, comparator, delta, minimaProcessor );
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
		isScoreValid = computeMserScore( delta, comparator, false );
		if ( isScoreValid )
			for ( SimpleMserEvaluationNode< T > a : ancestors )
				a.evaluateLocalMinimum( minimaProcessor, delta, comparator );

		if ( ancestors.size() == 1 )
			mserThisOrAncestors = ancestors.get( 0 ).mserThisOrAncestors;
		else
		{
			mserThisOrAncestors = new ArrayList< SimpleMserTree< T >.Mser >();
			for ( SimpleMserEvaluationNode< T > a : ancestors )
				mserThisOrAncestors.addAll( a.mserThisOrAncestors );
		}
	}

	private SimpleMserEvaluationNode( final SimpleMserEvaluationNode< T > ancestor, final T value, final Comparator< T > comparator, final ComputeDeltaValue< T > delta, final SimpleMserComponentHandler.SimpleMserProcessor< T > minimaProcessor )
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

		isScoreValid = computeMserScore( delta, comparator, true );
//		All our ancestors are non-intermediate, and
//		non-intermediate nodes are never minimal because their score is
//		never smaller than that of the successor intermediate node.
//		if ( isScoreValid )
//			ancestor.evaluateLocalMinimum( minimaProcessor, delta );

		mserThisOrAncestors = ancestor.mserThisOrAncestors;
	}

	private void setSuccessor( SimpleMserEvaluationNode< T > node )
	{
		successor = node;
	}

	/**
	 * Evaluate the mser score at this connected component. This may fail if the
	 * connected component tree is not built far enough down from the current
	 * node. The mser score is computed as |Q_{i+delta} - Q_i| / |Q_i|, where
	 * Q_i is this component and Q_{i+delta} is the component delta steps down
	 * the component tree (threshold level is delta lower than this).
	 * 
	 * @param delta
	 * @param isIntermediate
	 *            whether this is an intermediate node. This influences the
	 *            search for the Q_{i+delta} in the following way. If a node
	 *            with value equal to i+delta is found, then this is a
	 *            non-intermediate node and there is an intermediate node with
	 *            the same value below it. If isIntermediate is true Q_{i+delta}
	 *            is set to the intermediate node. (The other possibility is,
	 *            that we find a node with value smaller than i+delta, i.e.,
	 *            there is no node with that exact value. In this case,
	 *            isIntermediate has no influence.)
	 */
	private boolean computeMserScore( final ComputeDeltaValue< T > delta, final Comparator< T > comparator, final boolean isIntermediate )
	{
		// we are looking for a precursor node with value == (this.value - delta)
		final T valueMinus = delta.valueMinusDelta( value );

		// go back in history until we find a node with (value <= valueMinus)
		SimpleMserEvaluationNode< T > node = historyAncestor;
		while ( node != null  &&  comparator.compare( node.value, valueMinus ) > 0 )
			node = node.historyAncestor;
		if ( node == null )
			// we cannot compute the mser score because the history is too short.
			return false;
		if ( isIntermediate && comparator.compare( node.value, valueMinus ) == 0 && node.historyAncestor != null )
			node = node.historyAncestor;
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
	private void evaluateLocalMinimum( final SimpleMserComponentHandler.SimpleMserProcessor< T > minimaProcessor, final ComputeDeltaValue< T > delta, final Comparator< T > comparator )
	{
		if ( isScoreValid )
		{
			SimpleMserEvaluationNode< T > below = historyAncestor;
			while ( below.isScoreValid && below.size == size )
				below = below.historyAncestor;
			if ( below.isScoreValid )
			{
					below = below.historyAncestor;
				if ( ( score <= below.score ) && ( score < successor.score ) )
					minimaProcessor.foundNewMinimum( this );
			}
			else
			{
				final T valueMinus = delta.valueMinusDelta( value );
				if ( comparator.compare( valueMinus, below.value ) > 0 )
					// we are just above the bottom of a branch and this components
					// value is high enough above the bottom value to make its score=0.
					// so let's pretend we found a minimum here...
					minimaProcessor.foundNewMinimum( this );
			}
		}
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
