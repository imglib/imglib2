/**
 * Copyright (c) 2010, Larry Lindsey
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
 *
 * @author Larry Lindsey
 */
package mpicbg.imglib.algorithm.histogram;

import mpicbg.imglib.type.Type;

/**
 * HistogramKeys are used by {@link Histogram} to key {@link HistogramBin}s 
 * into a {@link HashTable}.
 * @author Larry Lindsey
 *
 * @param <T> the type of {@link Type} that this HistogramKey pertains to.
 */
public class HistogramKey<T extends Type<T>>
{
	/**
	 * The hash code returned by hashCode().
	 */
	private final int code;
	
	/**
	 * The HistogramBinFactory that generated this HistogramKey.
	 */
	private final HistogramBinFactory<T> keyFactory;
	
	/**
	 * A representative {@link Type}.
	 */
	private final T type;
	
	/**
	 * Create a HistogramKey with hash code hc, representative {@Type} t, and
	 * HistogramBinFactory factory.
	 * @param hc the hash code to be returned by this HistogramKey's hashCode().
	 * @see Object#hashCode()
	 * @param t the representative {@link Type}.  Preferably, this should be
	 * the center Type of the HistogramBin to be keyed, but it only need be one
	 * that would be binned into that bin.
	 * @param factory the HistogramBinFactory corresponding to this key.
	 * Typically, this should be the HistogramBinFactory that generated this
	 * key.
	 */
	public HistogramKey(int hc, T t, HistogramBinFactory<T> factory)
	{
		code = hc;
		keyFactory = factory;
		type = t;
	}
	
	@Override
	public int hashCode()
	{
		return code;
	}

	
	/**
	 * Determines whether this HistogramKey is equal to the given Object.
	 * This HistogramKey is equal to {@code o} iff {@code o} is also a
	 * HistogramKey, and {@link HistogramBinFactory#equivalent(Type, Type)}
	 * returns true when called with this HistogramKey's representative type
	 * and the one corresponding to {@code o}.
	 * @param o the Object against which to check for equality.
	 * @return true if this HistogramKey is equal to o. 
	 */	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o)
	{
		if (o instanceof HistogramKey)
		{
			if (this.getClass().isInstance(o))
			{
				HistogramKey<T> key = (HistogramKey<T>) o;
				return keyFactory.equivalent(type, key.type);
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public T getType()
	{
		return type;
	}
}
