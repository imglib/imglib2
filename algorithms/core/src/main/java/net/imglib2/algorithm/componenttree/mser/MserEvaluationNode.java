/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.algorithm.componenttree.mser;

import java.util.ArrayList;
import java.util.Comparator;

import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.type.Type;

/**
 * Store {@link PixelList}, mean, covariance, instability score, parent, and
 * children of a extremal region. A tree of {@link MserEvaluationNode} is built
 * from emitted {@link MserPartialComponent}. As soon as the parent of a node is
 * available, it is checked whether the instability score is a local minimum. In
 * this case, it is passed to
 * {@link MserTree#foundNewMinimum(MserEvaluationNode)}, where a MSER is
 * created.
 * 
 * We construct the component tree for generic types, which means that we cannot
 * raise the threshold values in steps of 1 (value type might be continuous or
 * non-numeric). To create a tree whose every branch covers a continuous range
 * of values, two nodes are created for every {@link MserPartialComponent}.
 * These mark the range of values covered by that component. The first node is
 * called the <em>"direct"</em> or <em>"non-intermediate node"</em> and is
 * created, when the {@link MserPartialComponent} is emitted. It marks the lower
 * bound (inclusive) of the value range. When the parent of the
 * {@link MserPartialComponent} is emitted, the <em>"intermediate node"</em> is
 * created which covers the same pixels as the direct node but marks the upper
 * bound (exclusive) of the value range.
 *
 * @param <T>
 *            value type of the input image.
 *
 * @author Tobias Pietzsch
 */
final class MserEvaluationNode< T extends Type< T > >
{
	/**
	 * Threshold value of the connected component.
	 */
	final T value;

	/**
	 * Size (number of pixels) of the connected component.
	 */
	final long size;

	/**
	 * Pixels in the component.
	 */
	final PixelList pixelList;

	/**
	 * The child in the component tree from which we inherit the component size
	 * history.
	 */
	private final MserEvaluationNode< T > historyChild;

	/**
	 * Parent of this {@link MserEvaluationNode} in the component tree.
	 */
	private MserEvaluationNode< T > parent;

	/**
	 * MSER score : |R_i - R_{i-\Delta}| / |R_i|.
	 */
	double score;

	/**
	 * Whether the {@link #score} is valid. (Otherwise it has not or cannot be
	 * computed.)
	 */
	private final boolean isScoreValid;

	/**
	 * Number of dimensions in the image.
	 */
	final int n;

	/**
	 * Mean of pixel positions (x, y, z, ...).
	 */
	final double[] mean;

	/**
	 * Independent elements of the covariance of pixel positions (xx, xy, xz,
	 * ..., yy, yz, ..., zz, ...).
	 */
	final double[] cov;

	/**
	 * {@link Mser}s associated to this region or its children. To build up the
	 * MSER tree.
	 */
	final ArrayList< Mser< T > > mserThisOrChildren;

	MserEvaluationNode( final MserPartialComponent< T > component, final Comparator< T > comparator, final ComputeDelta< T > delta, final MserTree< T > tree )
	{
		value = component.getValue().copy();
		pixelList = new PixelList( component.pixelList );
		size = pixelList.size();

		final ArrayList< MserEvaluationNode< T > > children = new ArrayList< MserEvaluationNode< T > >();
		MserEvaluationNode< T > node = component.getEvaluationNode();
		long historySize = 0;
		if ( node != null )
		{
			historySize = node.size;
			// create intermediate MserEvaluationNode between last emitted and this node.
			node = new MserEvaluationNode< T >( node, value, comparator, delta );
			children.add( node );
			node.setParent( this );
		}

		MserEvaluationNode< T > historyWinner = node;
		for ( final MserPartialComponent< T > c : component.children )
		{
			// create intermediate MserEvaluationNode between child and this node.
			node = new MserEvaluationNode< T >( c.getEvaluationNode(), value, comparator, delta );
			children.add( node );
			node.setParent( this );
			if ( c.size() > historySize )
			{
				historyWinner = node;
				historySize = c.size();
			}
		}

		historyChild = historyWinner;

		n = component.n;
		mean = new double[ n ];
		cov = new double[ ( n * (n+1) ) / 2 ];
		for ( int i = 0; i < n; ++i )
			mean[ i ] = component.sumPos[ i ] / size;
		int k = 0;
		for ( int i = 0; i < n; ++i )
			for ( int j = i; j < n; ++j )
			{
				cov[ k ] = component.sumSquPos[ k ] / size - mean[ i ] * mean[ j ];
				++k;
			}

		component.setEvaluationNode( this );
		isScoreValid = computeMserScore( delta, comparator, false );
		if ( isScoreValid )
			for ( final MserEvaluationNode< T > a : children )
				a.evaluateLocalMinimum( tree, delta, comparator );

		if ( children.size() == 1 )
			mserThisOrChildren = children.get( 0 ).mserThisOrChildren;
		else
		{
			mserThisOrChildren = new ArrayList< Mser< T > >();
			for ( final MserEvaluationNode< T > a : children )
				mserThisOrChildren.addAll( a.mserThisOrChildren );
		}
	}

	private MserEvaluationNode( final MserEvaluationNode< T > child, final T value, final Comparator< T > comparator, final ComputeDelta< T > delta )
	{
		child.setParent( this );

		historyChild = child;
		size = child.size;
		pixelList = child.pixelList;
		this.value = value;
		n = child.n;
		mean = child.mean;
		cov = child.cov;

		isScoreValid = computeMserScore( delta, comparator, true );
//		All our children are non-intermediate, and
//		non-intermediate nodes are never minimal because their score is
//		never smaller than that of the parent intermediate node.
//		if ( isScoreValid )
//			child.evaluateLocalMinimum( minimaProcessor, delta );

		mserThisOrChildren = child.mserThisOrChildren;
	}

	private void setParent( final MserEvaluationNode< T > node )
	{
		parent = node;
	}

	/**
	 * Evaluate the mser score at this connected component. This may fail if the
	 * connected component tree is not built far enough down from the current
	 * node. The mser score is computed as |R_i - R_{i-\Delta}| / |R_i|, where
	 * R_i is this component and R_{i-delta} is the component delta steps down
	 * the component tree (threshold level is delta lower than this).
	 *
	 * @param delta
	 * @param isIntermediate
	 *            whether this is an intermediate node. This influences the
	 *            search for the R_{i-delta} in the following way. If a node
	 *            with value equal to i-delta is found, then this is a
	 *            non-intermediate node and there is an intermediate node with
	 *            the same value below it. If isIntermediate is true R_{i-delta}
	 *            is set to the intermediate node. (The other possibility is,
	 *            that we find a node with value smaller than i-delta, i.e.,
	 *            there is no node with that exact value. In this case,
	 *            isIntermediate has no influence.)
	 */
	private boolean computeMserScore( final ComputeDelta< T > delta, final Comparator< T > comparator, final boolean isIntermediate )
	{
		// we are looking for a precursor node with value == (this.value - delta)
		final T valueMinus = delta.valueMinusDelta( value );

		// go back in history until we find a node with (value <= valueMinus)
		MserEvaluationNode< T > node = historyChild;
		while ( node != null  &&  comparator.compare( node.value, valueMinus ) > 0 )
			node = node.historyChild;
		if ( node == null )
			// we cannot compute the mser score because the history is too short.
			return false;
		if ( isIntermediate && comparator.compare( node.value, valueMinus ) == 0 && node.historyChild != null )
			node = node.historyChild;
		score = ( size - node.size ) / ( ( double ) size );
		return true;
	}

	/**
	 * Check whether the mser score is a local minimum at this connected
	 * component. This may fail if the mser score for this component, or the
	 * previous one in the branch are not available. (Note, that this is only
	 * called, when the mser score for the next component in the branch is
	 * available.)
	 */
	private void evaluateLocalMinimum( final MserTree< T > tree, final ComputeDelta< T > delta, final Comparator< T > comparator )
	{
		if ( isScoreValid )
		{
			MserEvaluationNode< T > below = historyChild;
			while ( below.isScoreValid && below.size == size )
				below = below.historyChild;
			if ( below.isScoreValid )
			{
					below = below.historyChild;
				if ( ( score <= below.score ) && ( score < parent.score ) )
					tree.foundNewMinimum( this );
			}
			else
			{
				final T valueMinus = delta.valueMinusDelta( value );
				if ( comparator.compare( valueMinus, below.value ) > 0 )
					// we are just above the bottom of a branch and this components
					// value is high enough above the bottom value to make its score=0.
					// so let's pretend we found a minimum here...
					tree.foundNewMinimum( this );
			}
		}
	}

	@Override
	public String toString()
	{
		String s = "SimpleMserEvaluationNode";
		s += ", size = " + size;
		s += ", history = [";
		MserEvaluationNode< T > node = historyChild;
		boolean first = true;
		while ( node != null )
		{
			if ( first )
				first = false;
			else
				s += ", ";
			s += "(" + node.value + "; " + node.size;
			if ( node.isScoreValid )
				s += " s " + node.score + ")";
			else
				s += " s --)";
			node = node.historyChild;
		}
		s += "]";
		return s;
	}
}
