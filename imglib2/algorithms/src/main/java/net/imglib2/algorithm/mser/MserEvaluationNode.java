package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import org.junit.internal.matchers.IsCollectionContaining;

import net.imglib2.Localizable;
import net.imglib2.type.numeric.IntegerType;

public class MserEvaluationNode< T extends IntegerType< T > >
{
	public static final boolean verbose = true;

	public final ArrayList< MserEvaluationNode< T > > ancestors;
	public final MserEvaluationNode< T > historyAncestor;
	public MserEvaluationNode< T > successor;
	
	/**
	 * Size (number of pixels) of the connected component.
	 */
	public final long size;
	
	/**
	 * Threshold value of the connected component.
	 */
	public final T value;
	
	/**
	 * MSER score : |Q_{i+\Delta}\Q_{i-Delta}| / |Q_i|. 
	 */
	public double score;
	public boolean isScoreValid;
	
	/**
	 * Whether there is a local minimum of the MSER score at this component.
	 */
	public boolean isLocalMinimum;
	public boolean isLocalMinimumValid;
	
	//for verbose output:
	public final ArrayList< Localizable > locations;
	public final int componentId;

	public MserEvaluationNode( final MserComponent< T > component )
	{
		ancestors = new ArrayList< MserEvaluationNode< T > >();
		MserEvaluationNode< T > n = null;
		if ( component.getEvaluationNode() != null )
		{
			n = createIntermediateNodes( component.getEvaluationNode(), component.getValue() );
			ancestors.add( n );
			n.setSuccessor( this );
		}

		MserEvaluationNode< T > historyWinner = n;
		long historySize = component.getLastEmitSize();
		for ( MserComponent< T > c : component.getAncestors() )
		{
			n = createIntermediateNodes( c.getEvaluationNode(), component.getValue() );
			ancestors.add( n );
			n.setSuccessor( this );
			if ( c.getSize() > historySize )
			{
				historyWinner = n;
				historySize = c.getSize();
			}
		}
		
		historyAncestor = historyWinner;
		size = component.getSize();
		value = component.getValue().copy();
		
		score = 0;
		isScoreValid = false;
		isLocalMinimum = false;
		isLocalMinimumValid = false;
		
		if ( verbose )
		{
			locations = getLocationsFromComponent( component );
			componentId = component.id;
		}
		else
		{
			locations = null;
			componentId = 0;
		}
		
		component.setEvaluationNode( this );
	}
	
	protected MserEvaluationNode( final MserEvaluationNode< T > ancestor, final T value )
	{
		ancestors = new ArrayList< MserEvaluationNode< T > >();
		ancestors.add( ancestor );
		ancestor.setSuccessor( this );

		historyAncestor = ancestor;
		size = ancestor.size;
		this.value = value;

		score = 0;
		isScoreValid = false;
		isLocalMinimum = false;
		isLocalMinimumValid = false;
		
		locations = ancestor.locations;
		componentId = ancestor.componentId;
	}

	protected MserEvaluationNode< T > createIntermediateNodes( final MserEvaluationNode< T > fromNode, T toValue )
	{
		long vMax = toValue.getIntegerLong() - 1;
		MserEvaluationNode< T > n = fromNode;
		long v = n.value.getIntegerLong();
		while ( v < vMax )
		{
			T newvalue = n.value.copy();
			newvalue.inc();
			n = new MserEvaluationNode< T >( n, newvalue );
			v = n.value.getIntegerLong();
		}
		return n;
	}
	
	public static enum MserEvalResult
	{
		SUCCESS,
		WAS_ALREADY_COMPUTED,
		INSUFFICIENT_HISTORY,
		INSUFFICIENT_FUTURE
	};

	/**
	 * Evaluate the mser score at this connected component.
	 * This may fail if the connected component tree is not built far enough up and
	 * down from the current node.
	 * 
	 * @return whether the score could be successfully computed.
	 */
	public MserEvalResult computeMserScore( final T delta )
	{
		if ( isScoreValid )
			// score has already been computed
			return MserEvalResult.WAS_ALREADY_COMPUTED;

		// we are looking for a precursor node with value == (this.value - delta)
		T valueMinus = value.copy();
		valueMinus.sub( delta );
		// go back in history until we find a node with (value == valueMinus)
		MserEvaluationNode< T > n = historyAncestor;
		while ( n != null  &&  n.value.compareTo( valueMinus ) > 0 )
			n = n.historyAncestor;
		if ( n == null )
			// we cannot compute the mser score because the history is too short.
			return MserEvalResult.INSUFFICIENT_HISTORY;
		long sizeMinus = n.size;
		
		// we are looking for a successor node with value == (this.value + delta)
		T valuePlus = value.copy();
		valuePlus.add( delta );
		n = successor;
		// go forward in history until we find a node with (value == valuePlus)
		while ( n != null  &&  n.value.compareTo( valuePlus ) < 0 )
			n = n.successor;
		if ( n == null )
			// we cannot compute the mser score right now because the component tree has not been build far enough
			return MserEvalResult.INSUFFICIENT_FUTURE;
		long sizePlus = n.size;
		
		score = ( sizePlus - sizeMinus ) / ( ( double ) size );
		isScoreValid = true;
		
		return MserEvalResult.SUCCESS;
	}
	
	/**
	 * Check whether the mser score is a local minimum at this connected component.
	 * This may fail if the mser score for this component, the previous one, or the
	 * next one in the branch are not available.
	 * @return whether the property could be evaluated.
	 */
	public MserEvalResult evaluateLocalMinimum( final MserProcessor< T > minimaProcessor )
	{
		if ( isLocalMinimumValid )
			return MserEvalResult.WAS_ALREADY_COMPUTED;

		if ( successor == null || ( ! successor.isScoreValid ) )
			return MserEvalResult.INSUFFICIENT_FUTURE;

		if ( ! ( isScoreValid && historyAncestor.isScoreValid ) )
			return MserEvalResult.INSUFFICIENT_HISTORY;

		isLocalMinimum = ( score <= historyAncestor.score ) && ( score < successor.score );
		if ( isLocalMinimum )
			minimaProcessor.foundNewMinimum( this );
		isLocalMinimumValid = true;
		return MserEvalResult.SUCCESS;
	}
	
	public void computeScoresForHistory( final T delta )
	{

		switch ( computeMserScore( delta ) )
		{
		case WAS_ALREADY_COMPUTED:
			// if the score for this node is valid
			// then it was also successfully computed for all ancestor nodes.
			// we are done
		case INSUFFICIENT_HISTORY:
			// if there is not enough history to evaluate the score
			// then there never will be.
			// we are done
			return;
		case INSUFFICIENT_FUTURE:
			// we cannot evaluate this node yet.
			// recursively evaluate ancestors
		case SUCCESS:
			// recursively evaluate ancestors
			for ( MserEvaluationNode< T > n : ancestors )
				n.computeScoresForHistory( delta );
		}
	}
	
	public void evaluateLocalMinimaForHistory( final MserProcessor< T > minimaProcessor )
	{

		switch ( evaluateLocalMinimum( minimaProcessor ) )
		{
		case WAS_ALREADY_COMPUTED:
			// if the minimum check was completed for this node
			// then it was also completed for all ancestor nodes.
			// we are done
		case INSUFFICIENT_HISTORY:
			// if there is not enough history to check for local minimum
			// then there never will be.
			// we are done
			return;
		case INSUFFICIENT_FUTURE:
			// we cannot evaluate this node yet.
			// recursively evaluate ancestors
		case SUCCESS:
			// recursively evaluate ancestors
			for ( MserEvaluationNode< T > n : ancestors )
				n.evaluateLocalMinimaForHistory( minimaProcessor );
		}
	}
	
	public void setSuccessor( MserEvaluationNode< T > n )
	{
		successor = n;
	}

	@SuppressWarnings( "unchecked" )
	private ArrayList< Localizable > getLocationsFromComponent( final MserComponent< T > component )
	{
		return ( ArrayList< Localizable > ) component.locations.clone();
	}
	
	@Override
	public String toString()
	{
		if ( verbose )
		{
			String s = "MserEvaluationNode constructed from component " + componentId;
			s += ", size=" + size;
			s += ", history = [";
			MserEvaluationNode< T > n = historyAncestor;
			boolean first = true;
			while ( n != null )
			{
				if ( first )
					first = false;
				else
					s += ", ";	
				s += "(" + n.value + "; " + n.size;
				if ( n.isScoreValid )
					s += " s " + n.score + ")";
				else
					s += " s --)";
				n = n.historyAncestor;
			}
			s += "]";
			return s;
		}
		else
			return super.toString();
	}
}
