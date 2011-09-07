package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.type.numeric.IntegerType;

public class MserEvaluationNode< T extends IntegerType< T > >
{
	public static final boolean verbose = true;

	public final ArrayList< MserEvaluationNode< T > > ancestors;
	public final MserEvaluationNode< T > historyAncestor;
	public MserEvaluationNode< T > successor;
	
	public final long size;
	public final T value;
	
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
	
	public boolean computeMserFunction( final T delta )
	{
		// get size at (this.value - delta)
		T valueMinus = value.copy();
		valueMinus.sub( delta );
		// go back in history until we find a node with (value <= valueMinus)
		MserEvaluationNode< T > n = historyAncestor;
		while ( n != null  &&  n.value.compareTo( valueMinus ) > 0 )
			n = n.historyAncestor;
		if ( n == null )
			return false;
		long sizeMinus = n.size;
		
		// get size at (this.value + delta)
		T valuePlus = value.copy();
		valuePlus.add( delta );
		n = historyAncestor;
		// go forward in history until we find a node with (value == valueMinus)
		while ( n != null  &&  n.value.compareTo( valueMinus ) > 0 )
			n = n.historyAncestor;
		if ( n == null )
			return false;
		long sizePlus = n.size;
		
		
		return false;
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
				s += "(" + n.value + "; " + n.size + ")";
				n = n.historyAncestor;
			}
			s += "]";
			return s;
		}
		else
			return super.toString();
	}
}
