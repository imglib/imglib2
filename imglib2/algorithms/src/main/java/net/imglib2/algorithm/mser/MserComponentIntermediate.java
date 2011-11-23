package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.type.Type;

final class MserComponentIntermediate< T extends Type< T > > implements Component< T >
{
	/**
	 * Threshold value of the connected component.
	 */
	private final T value;

	/**
	 * Pixels in the component.
	 */
	final PixelList pixelList;

	/**
	 * number of dimensions in the image.
	 */
	final int n;

	/**
	 * sum of pixel positions (x, y, z, ...).
	 */
	final double[] sumPos;

	/**
	 * sum of independent elements of outer product of position (xx, xy, xz, ..., yy, yz, ..., zz, ...).
	 */
	final double[] sumSquPos;

	private final long[] tmp;

	/**
	 * A list of MserComponent merged into this one since it was last emitted.
	 * (For building up MSER evaluation structure.)
	 */
	ArrayList< MserComponentIntermediate< T > > children;
	
	/**
	 * The MserEvaluationNode assigned to this MserComponent when it was last emitted.
	 * (For building up MSER evaluation structure.)
	 */
	MserEvaluationNode< T > evaluationNode;

	MserComponentIntermediate( final T value, final MserComponentGenerator< T > generator )
	{
		pixelList = new PixelList( generator.linkedList.randomAccess(), generator.dimensions );
		n = generator.dimensions.length;
		sumPos = new double[ n ];
		sumSquPos = new double[ ( n * (n+1) ) / 2 ];
		this.value = value.copy();
		this.children = new ArrayList< MserComponentIntermediate< T > >();
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
		children.add( c );
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : ";
		boolean first = true;
		for ( Localizable l : pixelList )
		{
			if ( first )
			{
				first = false;
			}
			else
			{
				s += ", ";
			}
			s += l.toString();
		}
		return s + "}";
	}

	long size()
	{
		return pixelList.size();
	}
	
	MserEvaluationNode< T > getEvaluationNode()
	{
		return evaluationNode;
	}
	
	void setEvaluationNode( MserEvaluationNode< T > node )
	{
		evaluationNode = node;
	}
}
