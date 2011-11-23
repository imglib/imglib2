package net.imglib2.algorithm.mser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.algorithm.mser.MserComponentHandler.SimpleMserProcessor;
import net.imglib2.type.Type;

public class MserTree< T extends Type< T > > implements SimpleMserProcessor< T >
{
	public static class Mser< T extends Type< T > > implements Iterable< Localizable >
	{
		public final ArrayList< Mser< T > > ancestors;

		public Mser< T > successor;

		/**
		 * Threshold value of the connected component.
		 */
		private final T value;

		/**
		 * MSER score : |Q_{i+\Delta} - Q_i| / |Q_i|.
		 */
		private final double score;

		/**
		 * Pixels in the component.
		 */
		private final PixelList pixelList;

		/**
		 * Mean of the pixel positions in the region.
		 */
		private final double[] mean;

		/**
		 * Covariance of the pixel positions in the region.
		 */
		private final double[] cov;

		public Mser( MserEvaluationNode< T > node )
		{
			ancestors = new ArrayList< Mser< T > >();
			successor = null;

			value = node.value;
			score = node.score;
			pixelList = node.pixelList;
			mean = node.mean;
			cov = node.cov;
		}

		/**
		 * @return the image threshold that created the extremal region.
		 */
		public T value()
		{
			return value;
		}

		/**
		 * @return number of pixels the extremal region.
		 */
		public long size()
		{
			return pixelList.size();
		}

		/**
		 * The MSER score is computed as |Q_{i+delta} - Q_i| / |Q_i|.
		 * 
		 * @return the MSER score.
		 */
		public double score()
		{
			return score;
		}

		/**
		 * Mean of the pixel positions in the region. This is a position vector
		 * (x, y, z, ...)
		 * 
		 * @return mean vector.
		 */
		public double[] mean()
		{
			return mean;
		}

		/**
		 * Covariance of the pixel positions in the region. This is a vector of
		 * the independent elements of the covariance matrix (xx, xy, xz, ...,
		 * yy, yz, ..., zz, ...)
		 * 
		 * @return vector of covariance elements.
		 */
		public double[] cov()
		{
			return cov;
		}

		@Override
		public Iterator< Localizable > iterator()
		{
			return pixelList.iterator();
		}
	}
	
	private final HashSet< Mser< T > > roots;

	private final double minDiversity;
	
	public MserTree( final double minDiversity )
	{
		roots = new HashSet< Mser< T > >();
		this.minDiversity = minDiversity;
	}

	public void pruneDuplicates()
	{
		for ( Mser< T > mser : roots )
			pruneChildren ( mser );
	}

	public HashSet< Mser< T > > roots()
	{
		return roots;
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
	}

	@Override
	public void foundNewMinimum( MserEvaluationNode< T > node )
	{
		Mser< T > mser = new Mser< T >( node );
		for ( Mser< T > m : node.mserThisOrAncestors )
			mser.ancestors.add( m );
		node.mserThisOrAncestors.clear();
		node.mserThisOrAncestors.add( mser );
		
		for ( Mser< T > m : mser.ancestors )
			roots.remove( m );
		roots.add( mser );
	}
}
