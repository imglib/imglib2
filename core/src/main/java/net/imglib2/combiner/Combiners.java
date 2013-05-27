/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
package net.imglib2.combiner;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.combiner.read.CombinedIterableInterval;
import net.imglib2.combiner.read.CombinedRandomAccessible;
import net.imglib2.combiner.read.CombinedRandomAccessibleInterval;
import net.imglib2.type.Type;

/**
 * Convenience factory methods for sample conversion.
 * 
 * @author Christian Dietz
 */
public class Combiners
{
	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a Combined sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * Combined {@link RandomAccessibleInterval} has no effect.
	 * 
	 * @param sourceA
	 * @param sourceB
	 * @param combiner
	 * @param b
	 * @return a Combined {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	final static public < A, B, C extends Type< C > > RandomAccessible< C > combine( final RandomAccessible< A > sourceA, final RandomAccessible< B > sourceB, final Combiner< A, B, C > combiner, final C c )
	{
		return new CombinedRandomAccessible< A, B, C >( sourceA, sourceB, combiner, c );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a Combined sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * Combined {@link RandomAccessibleInterval} has no effect.
	 * 
	 * @param sourceA
	 * @param sourceB
	 * @param combiner
	 * @param b
	 * @return a Combined {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	final static public < A, B, C extends Type< C > > RandomAccessibleInterval< C > combine( final RandomAccessibleInterval< A > sourceA, final RandomAccessibleInterval< B > sourceB, final Combiner< A, B, C > combiner, final C c )
	{
		return new CombinedRandomAccessibleInterval< A, B, C >( sourceA, sourceB, combiner, c );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a Combined sample. Conversion is done on-the-fly
	 * when reading values. Writing to the Combined {@link IterableInterval} has
	 * no effect.
	 * 
	 * @param sourceA
	 * @param sourceB
	 * @param combiner
	 * @param b
	 * @return a Combined {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	final static public < A, B, C extends Type< C > > IterableInterval< C > combine( final IterableInterval< A > sourceA, final IterableInterval< B > sourceB, final Combiner< A, B, C > combiner, final C b )
	{
		return new CombinedIterableInterval< A, B, C >( sourceA, sourceB, combiner, b );
	}
}
