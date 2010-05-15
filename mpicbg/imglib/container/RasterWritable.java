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
 *
 */
package mpicbg.imglib.container;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.cell.CellContainer;
import mpicbg.imglib.container.shapelist.ShapeList;
import mpicbg.imglib.sampler.RasterSampler;
import mpicbg.imglib.sampler.Sampler;
import mpicbg.imglib.type.Type;

/**
 * <p>
 * This interface is an empty contract to mark either a {@link Sampler} or a
 * {@link Container} to provide writable {@link Type Types}, that is, iterating
 * over pixels allows you to change the content of pixels.  Just to clarify why
 * this is important: A {@link Container} that stores pixel intensities
 * directly in the one or other way like {@link Array} or {@link CellContainer}
 * should implement {@link RasterWritable}.  On the other hand, generative
 * {@link Container Containers} like the {@link ShapeList} should not unless
 * they provide {@link RasterSampler RasterSamplers} that somehow can change
 * the underlying generative instructions to reflect a write operation on a
 * pixel location.
 * </p>
 * <p>
 * You can declare a method or algorithm that requires writable
 * {@link Container Containers} or {@link RasterSampler RasterSamplers} using
 * <em>Generics</em> such as this simple {@link Container} invariant copy method:
 * </p>
 * <pre>
 *   public &lt;T extends Type&lt;T&gt;, C extends RasterWritable, A extends Image&lt;T,?&gt;, B extends Image&lt;T,C&gt; &gt;void copy( A source, B target ){
 *     final PositionableRasterSampler&lt;T&gt; t = b.createPositionableRasterSampler();
 *     for ( final RasterIterator i; i.hasNext(); ){
 *       i.fwd();
 *       t.moveTo( t );
 *       i.type().set( t.type() );
 *     }
 *   }
 * </pre>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public interface RasterWritable
{
	public < T extends Type< T >, A extends Container< T >, B extends Container< T > & RasterWritable >void doSomething( A source, B target );
}
