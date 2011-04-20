/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Preibisch and Johannes Schindelin
 */
package net.imglib2.algorithm.kdtree.node;


public class SimpleNode implements Leaf<SimpleNode>
{
	final float p[];
	final int numDimensions;

	public SimpleNode(final SimpleNode node) {
		this.p = node.p.clone();
		this.numDimensions = p.length;
	}

	public SimpleNode(final float[] p) {
		this.p = p.clone();
		this.numDimensions = p.length;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public boolean equals(final SimpleNode o) {
		if (o.getNumDimensions() != numDimensions)
			return false;

		for (int d = 0; d < numDimensions; ++d)
			if (p[d] != o.p[d])
				return false;

		return true;
	}

	@Override
	public float distanceTo(final SimpleNode o) {
		double dist = 0;

		for (int d = 0; d < numDimensions; ++d) {
			final double v = o.get(d) - get(d);
			dist += v*v;
		}

		return (float)Math.sqrt(dist);
	}

	@Override
	public float get(final int k) {
		return p[k];
	}

	@Override
	public String toString() {
		String s = "(" + p[0];

		for (int d = 1; d < numDimensions; ++d)
			s += ", " + p[d];

		s += ")";
		return s;
	}

	@Override
	public SimpleNode[] createArray(final int n) {
		return new SimpleNode[n];
	}

	@Override
	public int getNumDimensions() {
		return numDimensions;
	}

	@Override
	public SimpleNode getEntry() { return this; }
}
