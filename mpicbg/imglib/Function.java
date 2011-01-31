/**
 * Copyright (c) 2010, Stephan Saalfeld
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
package mpicbg.imglib;

import mpicbg.imglib.type.Type;

/**
 * <p><em>f:?&rarr;T</em></p>
 * 
 * <p>Function is the the basis of imglib, a function over an unspecified
 * source domain that can be sampled for values from a target domain T.
 * <em>Note</em> that {@link Sampler Samplers} returned by the {@link Function}
 * interface alone cannot be addressed in the unspecified source domain and
 * thus cannot do anything but access a default T.  That is, any implementation
 * of {@link Function} has to implement another interface that specifies the
 * source domain over which a T is defined.</p>
 * 
 * <p>Image processing usually refers to {@link Function Functions} over real
 * (f:R<sup>n</sup>&rarr;T) or discrete (f:Z<sup>n</sup>&rarr;T) space with the
 * latter being a subset of the former.  The source domain may be constrained
 * further as an interval over such space.  The standard interfaces for image
 * processing are thus:</p>
 * 
 * <dl>
 * <dt>{@link RealFunction}</dt>
 * <dd>random access at real coordinates</dd>
 * <dt>{@link RealIntervalFunction}</dt>
 * <dd>random access at real coordinates with an {@link OutOfBoundsStrategy}
 * that generates values beyond the boundaries of the source domain</dd>
 * <dt>{@link IterableFunction}</dt>
 * <dd>access to all T in the target domain and their coordinates in the
 * source domain via {@link Iterator}</dd>
 * <dt>{@link IntegerFunction}</dt>
 * <dd>random access at integer coordinates</dd>
 * <dt>{@link IntegerIntervalFunction}</dt>
 * <dd>random access at integer coordinates with an {@link OutOfBoundsStrategy}
 * that generates values beyond the boundaries of the source domain</dd>
 * <dl>
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface Function< T, F extends Function< T, F > >
{
	/**
	 * Create a {@link Sampler} that provides access to a {@link Type}.
	 * <em>Note</em> that, with the {@link Function} interface
	 * alone, the source domain is not further specified and, thus, a
	 * {@link SamplerFactory} cannot be specified.
	 * 
	 * @param <S> type of the Sampler is inferred from S in factory
	 * @param factory creates the sampler
	 * @return sampler
	 */
	public < S extends Sampler< T > > S sampler( final SamplerFactory< T, S, F > factory ); 

}
