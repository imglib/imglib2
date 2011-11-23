package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.type.numeric.RealType;

public final class MserComponentIntermediate< T extends RealType< T > > implements Component< T >
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
	ArrayList< MserComponentIntermediate< T > > ancestors;
	
	/**
	 * The MserEvaluationNode assigned to this MserComponent when it was last emitted.
	 * (For building up MSER evaluation structure.)
	 */
	MserEvaluationNode< T > evaluationNode;

	public MserComponentIntermediate( final T value, final MserComponentHandler< T > handler )
	{
		id = idGen++;
		pixelList = new PixelList( handler.linkedList.randomAccess(), handler.dimensions );
		n = handler.dimensions.length;
		sumPos = new double[ n ];
		sumSquPos = new double[ ( n * (n+1) ) / 2 ];
		this.value = value.copy();
		this.ancestors = new ArrayList< MserComponentIntermediate< T > >();
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
		final MserComponentIntermediate< T > c = ( MserComponentIntermediate< T > ) component;
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
		for ( MserComponentIntermediate< T > c : ancestors )
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

	public long size()
	{
		return pixelList.size();
	}
	
	public ArrayList< MserComponentIntermediate< T > > getAncestors()
	{
		return ancestors;
	}
	
	public void clearAncestors()
	{
		ancestors.clear();
	}

	public MserEvaluationNode< T > getEvaluationNode()
	{
		return evaluationNode;
	}
	
	public void setEvaluationNode( MserEvaluationNode< T > node )
	{
		evaluationNode = node;
	}
}
