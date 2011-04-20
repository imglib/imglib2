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
 * @author Stephan Preibisch
 */
package net.imglib2.algorithm;

/**
 * This is a convenience implementation of an algorithm that implements {@link MultiThreaded}
 * and {@link Algorithm} so that less code has to be re-implemented.
 * 
 * IMPORTANT: It is not meant to be used for any other purpose than that, it should not be 
 * demanded by any other method or generic construct, use the interfaces instead.
 *   
 * @author Stephan Preibisch
 */
public abstract class MultiThreadedAlgorithm implements MultiThreaded, Algorithm
{
	protected int numThreads;
	protected String errorMessage = "";

	public MultiThreadedAlgorithm() { setNumThreads(); }
	
	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }

	@Override
	public String getErrorMessage() { return errorMessage; }
}
