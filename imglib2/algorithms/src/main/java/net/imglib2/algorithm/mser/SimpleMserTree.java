package net.imglib2.algorithm.mser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.algorithm.mser.SimpleMserComponentHandler.SimpleMserProcessor;
import net.imglib2.type.numeric.IntegerType;

public class SimpleMserTree< T extends IntegerType< T > > implements SimpleMserProcessor< T >
{
	public class Mser implements Iterable< Localizable >
	{
		public final ArrayList< Mser > ancestors;

		public Mser successor;

		/**
		 * Threshold value of the connected component.
		 */
		private final long value;

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

		public Mser( SimpleMserEvaluationNode< T > node )
		{
			ancestors = new ArrayList< Mser >();
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
		public long value()
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
	
	final HashSet< Mser > roots;
	
	public SimpleMserTree()
	{
		roots = new HashSet< Mser >();
	}

	@Override
	public void foundNewMinimum( SimpleMserEvaluationNode< T > node )
	{
		Mser mser = new Mser( node );
		for ( Mser m : node.mserThisOrAncestors )
			mser.ancestors.add( m );
		node.mserThisOrAncestors.clear();
		node.mserThisOrAncestors.add( mser );
		
		for ( Mser m : mser.ancestors )
			roots.remove( m );
		roots.add( mser );
	}
}
