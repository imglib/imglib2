package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.type.numeric.IntegerType;

public class MserComponent< T extends IntegerType< T > > extends PixelListComponent< T >
{
	long size;
	
	/**
	 * The size of this MserComponent when it was last emitted.
	 * (For building up MSER evaluation structure.)
	 */
	long lastEmitSize;
	
	/**
	 * A list of MserComponent merged into this one since it was last emitted.
	 * (For building up MSER evaluation structure.)
	 */
	ArrayList< MserComponent< T > > ancestors;
	
	/**
	 * The MserEvaluationNode assigned to this MserComponent when it was last emitted.
	 * (For building up MSER evaluation structure.)
	 */
	MserEvaluationNode< T > evaluationNode;
	
	public MserComponent( final T value )
	{
		super( value );
		size = 0;
		lastEmitSize = 0;
		ancestors = new ArrayList< MserComponent< T > >();
		evaluationNode = null;
	}
	
	@Override
	public void addPosition( final Localizable position )
	{
		super.addPosition( position );
		++size;
	}
	
	@Override
	public void merge( final Component< T > component )
	{
		super.merge( component );
		final MserComponent< T > c = ( MserComponent< T > ) component;
		// size += c.size; TODO: uncomment (for now this is handled by addPosition, which is called by super.merge())
		ancestors.add( c );
	}

	@Override
	public String toString()
	{
		String s = "id=" + id;
		s += " [";
		boolean first = true;
		for ( MserComponent< T > c : ancestors )
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
		s += "] (level = " + value.toString() + ", size = " + size + ")";
//		first = true;
//		for ( Localizable l : locations )
//		{
//			if ( first )
//			{
//				first = false;
//			}
//			else
//			{
//				s += ", ";
//			}
//			s += l.toString();
//		}
		return s + "}";
	}

	public long getSize()
	{
		return size;
	}
	
	public ArrayList< MserComponent< T > > getAncestors()
	{
		return ancestors;
	}
	
	public void clearAncestors()
	{
		ancestors.clear();
	}

	public long getLastEmitSize()
	{
		return lastEmitSize;
	}

	public void updateLastEmitSize()
	{
		lastEmitSize = size;
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
