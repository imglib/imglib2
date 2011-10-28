package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.type.numeric.IntegerType;

public final class SimpleMserComponent< T extends IntegerType< T > > implements Component< T >
{
	private static int idGen = 0;
	
	final int id;
	
	PixelList pixelList;
	
	/**
	 * number of dimensions in the image.
	 */
	final int n;
	
	final double[] sumPos; // position sum (x, y, z, ...)
	final double[] sumSquPos; // sum of independent elements of outer product of position (xx, xy, xz, ..., yy, yz, ..., zz, ...)

	final T value;
	
	final private long[] tmp;

	/**
	 * A list of MserComponent merged into this one since it was last emitted.
	 * (For building up MSER evaluation structure.)
	 */
	ArrayList< SimpleMserComponent< T > > ancestors;
	
	/**
	 * The MserEvaluationNode assigned to this MserComponent when it was last emitted.
	 * (For building up MSER evaluation structure.)
	 */
	SimpleMserEvaluationNode< T > evaluationNode;

	public SimpleMserComponent( final T value, final SimpleMserComponentHandler< T > handler )
	{
		id = idGen++;
		pixelList = new PixelList( handler.linkedList.randomAccess(), handler.dimensions );
		n = handler.dimensions.length;
		sumPos = new double[ n ];
		sumSquPos = new double[ ( n * (n+1) ) / 2 ];
		this.value = value.copy();
		this.ancestors = new ArrayList< SimpleMserComponent< T > >();
		this.evaluationNode = null;
		tmp = new long[ n ];
	}
	
	@Override
	public void addPosition( final Localizable position )
	{
		pixelList.addPosition( position );
		position.localize( tmp );
		int k = 0;
		for ( int i = 0; i < n; ++i )
		{
			sumPos[ i ] += tmp[ i ];
			for ( int j = i; j < n; ++j )
				sumSquPos[ k++ ] += tmp[ i ] * tmp[ j ];
		}
	}

	@Override
	public T getValue()
	{
		return value;
	}

	@Override
	public void setValue( final T value )
	{
		this.value.set( value );
	}
	
	@Override
	public void merge( final Component< T > component )
	{
		final SimpleMserComponent< T > c = ( SimpleMserComponent< T > ) component;
		pixelList.merge( c.pixelList );
		for ( int i = 0; i < sumPos.length; ++i )
			sumPos[ i ] += c.sumPos[ i ];
		for ( int i = 0; i < sumSquPos.length; ++i )
			sumSquPos[ i ] += c.sumSquPos[ i ];
		ancestors.add( c );
	}

	@Override
	public String toString()
	{
		String s = "id=" + id;
		s += " [";
		boolean first = true;
		for ( SimpleMserComponent< T > c : ancestors )
		{
			if ( first )
			{
				first = false;
			}
			else
			{
				s += ",";
			}
			s += c.id;
		}
		s += "] (level = " + value.toString() + ", size = " + pixelList.size() + ")";
		return s + "}";
	}

	// TODO --> rename to size()
	public long getSize()
	{
		return pixelList.size();
	}
	
	public ArrayList< SimpleMserComponent< T > > getAncestors()
	{
		return ancestors;
	}
	
	public void clearAncestors()
	{
		ancestors.clear();
	}

	public SimpleMserEvaluationNode< T > getEvaluationNode()
	{
		return evaluationNode;
	}
	
	public void setEvaluationNode( SimpleMserEvaluationNode< T > node )
	{
		evaluationNode = node;
	}
}
