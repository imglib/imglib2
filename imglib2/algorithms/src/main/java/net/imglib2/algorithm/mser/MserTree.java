package net.imglib2.algorithm.mser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.type.Type;

public final class MserTree< T extends Type< T > > implements Component.Handler< MserComponentIntermediate< T > >, Iterable< Mser< T > >
{
	private final HashSet< Mser< T > > roots;

	private final ArrayList< Mser< T > > nodes;

	private final Comparator< T > comparator;

	private final ComputeDeltaValue< T > delta;

	private final long minSize;

	private final long maxSize;

	private final double maxVar;

	private final double minDiversity;
	
	public MserTree( final Comparator< T > comparator, final ComputeDeltaValue< T > delta, final long minSize, final long maxSize, final double maxVar, final double minDiversity )
	{
		roots = new HashSet< Mser< T > >();
		nodes = new ArrayList< Mser< T > >();
		this.comparator = comparator;
		this.delta = delta;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.maxVar = maxVar;
		this.minDiversity = minDiversity;
	}

	public void pruneDuplicates()
	{
		nodes.clear();
		for ( Mser< T > mser : roots )
			pruneChildren ( mser );
		nodes.addAll( roots );
	}

	private void pruneChildren( Mser< T > mser )
	{
		final ArrayList< Mser< T > > validChildren = new ArrayList< Mser< T > >();
		for ( int i = 0; i < mser.children.size(); ++i )
		{
			Mser< T > m = mser.children.get( i );
			double div = ( mser.size() - m.size() ) / (double) mser.size();
			if ( div > minDiversity )
			{
				validChildren.add( m );
				pruneChildren( m );
			}
			else
			{
				mser.children.addAll( m.children );
				for ( Mser< T > m2 : m.children )
					m2.parent = mser;
			}
		}
		mser.children.clear();
		mser.children.addAll( validChildren );
		nodes.addAll( validChildren );
	}

	@Override
	public void emit( MserComponentIntermediate< T > component )
	{
		new MserEvaluationNode< T >( component, comparator, delta, this );
		component.children.clear();
	}

	void foundNewMinimum( MserEvaluationNode< T > node )
	{
		if ( node.size >= minSize && node.size <= maxSize && node.score <= maxVar )
		{
			Mser< T > mser = new Mser< T >( node );
			for ( Mser< T > m : node.mserThisOrChildren )
				mser.children.add( m );
			node.mserThisOrChildren.clear();
			node.mserThisOrChildren.add( mser );
			
			for ( Mser< T > m : mser.children )
				roots.remove( m );
			roots.add( mser );
			nodes.add( mser );
		}
	}

	@Override
	public Iterator< Mser< T > > iterator()
	{
		return nodes.iterator();
	}

	public HashSet< Mser< T > > roots()
	{
		return roots;
	}
}
