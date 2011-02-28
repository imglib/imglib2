/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.interpolation.nearestneighbor;

import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.position.transform.Round;

/**
 * 
 * @param <T>
 *
 * @author Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld
 */
public class NearestNeighborInterpolator< T > extends Round< RandomAccess< T > > implements Interpolator< T, RandomAccessible< T > >
{
	final protected RandomAccessible< T > randomAccessible;

	protected NearestNeighborInterpolator( final RandomAccessible< T > randomAccessible )
	{
		super( randomAccessible.randomAccess() );
		this.randomAccessible = randomAccessible;
	}

	/**
	 * Returns the {@link RandomAccessible} the interpolator is working on
	 * 
	 * @return - the {@link RandomAccessible}
	 */
	@Override
	public RandomAccessible< T > getFunction()
	{
		return randomAccessible;
	}
	
	@Override
	public T get()
	{
		return target.get();
	}
	
	@Override
	@Deprecated
	final public T getType()
	{
		return get();
	}
}
