/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.legacy.kdtree.node;

/**
 * TODO
 *
 * @author Johannes Schindelin
 * @author Stephan Preibisch
 */
public class NonLeaf<T extends Leaf<T>> implements Node<T>
{
	/* the axis of 'coordinate' is the depth modulo the dimension */
	final public float coordinate;
	final public Node<T> left, right;
	final int dimension;

	public NonLeaf(final float coordinate, final int dimension, final Node<T> left, final Node<T> right) {
		this.coordinate = coordinate;
		this.left = left;
		this.right = right;
		this.dimension = dimension;
	}

	public boolean isLeaf() {
		return false;
	}

	public String toString(final Node<T> node) {
		if (node == null)
			return "null";
		if (Leaf.class.isInstance(node)) {
			String result = "(" + ((Leaf<?>)node).get(0);

			for (int i = 1; i < dimension; i++)
				result += ", " + ((Leaf<?>)node).get(i);

			return result + ")";
		}

		if (NonLeaf.class.isInstance(node)) {
			NonLeaf<T> nonLeaf = (NonLeaf<T>)node;
			return "[" + toString( nonLeaf.left ) + " |{" + nonLeaf.coordinate + "} " + toString(nonLeaf.right) + "]";
		}

		return node.toString();
	}

	public String toString() {
		return toString(this);
	}
}
