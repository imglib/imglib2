package net.imglib2.algorithm.mser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.imglib2.algorithm.mser.MserComponentHandler.SimpleMserProcessor;
import net.imglib2.type.Type;

public class MserTree< T extends Type< T > > implements SimpleMserProcessor< T >, Iterable< Mser< T > >
{
	private final HashSet< Mser< T > > roots;

	private final ArrayList< Mser< T > > nodes;

	private final long minSize;

	private final long maxSize;

	private final double maxVar;

	private final double minDiversity;
	
	public MserTree( final long minSize, final long maxSize, final double maxVar, final double minDiversity )
	{
		roots = new HashSet< Mser< T > >();
		nodes = new ArrayList< Mser< T > >();
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
		final ArrayList< Mser< T > > validAncestors = new ArrayList< Mser< T > >();
		for ( int i = 0; i < mser.ancestors.size(); ++i )
		{
			Mser< T > m = mser.ancestors.get( i );
			double div = ( mser.size() - m.size() ) / (double) mser.size();
			if ( div > minDiversity )
			{
				validAncestors.add( m );
				pruneChildren( m );
			}
			else
			{
				mser.ancestors.addAll( m.ancestors );
				for ( Mser< T > m2 : m.ancestors )
					m2.successor = mser;
			}
		}
		mser.ancestors.clear();
		mser.ancestors.addAll( validAncestors );
		nodes.addAll( validAncestors );
	}

	@Override
	public void foundNewMinimum( MserEvaluationNode< T > node )
	{
		if ( node.size >= minSize && node.size <= maxSize && node.score <= maxVar )
		{
			Mser< T > mser = new Mser< T >( node );
			for ( Mser< T > m : node.mserThisOrAncestors )
				mser.ancestors.add( m );
			node.mserThisOrAncestors.clear();
			node.mserThisOrAncestors.add( mser );
			
			for ( Mser< T > m : mser.ancestors )
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
