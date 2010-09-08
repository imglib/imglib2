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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.sampler;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.LinkableIterator;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.type.Type;

/**
 * <h2>The {@link RasterIterator} interface</h2>
 * 
 * <h3>Implementation</h3>
 * 
 * {@link RasterIterator RasterIterators} are used to iterate over a raster of
 * pixels.  They depend on the way how pixels are stored and thus need to be
 * implemented for each {@link Container} like {@link Array}, {@link CellContainer}, ... 
 * 
 * <h3>Data access</h3>
 * 
 * {@link RasterIterator RasterIterator} doe not know about the {@link Image}
 * {@link Type} as it is not important for iteration.  However, {@link Type} is
 * a generic parameter such that the correct instance of {@link Type} is
 * returned by {@link #type()}.
 * 
 * <h3>Traversal policy</h3>
 * 
 * There is no particular guaranty regarding the order of iteration other than
 * that all pixels are visited once before {@link #hasNext()} returns true.
 * The actual order of iteration depends on the {@link Container} and is
 * expected to be optimal in terms of access performance.
 * <p>
 * However, it is <em>guaranteed</em> that for two same
 * {@link Container Containers} with equal dimensions and properties, the 
 * traverse path will be the same.  For instance, if the following is true:
 * <p>
 * <pre>
  		Container&lt T &gt c1 = img1.getContainer(); 
  		Container&lt S &gt c2 = img2.getContainer();
 		if ( c1.compareStorageContainerCompatibility( c2 ) )  
 * </pre>
 * then, it is ensured that
 * <pre>
 * 		{
 * 			RasterIterator&lt T &gt cursor1 = img1.createBasicIterator();
 * 			RasterIterator&lt S &gt cursor2 = img2.createBasicIterator();
 * 			while ( cursor1.hasNext() )
			{
				cursor1.fwd();
				cursor2.fwd();
			}
		}
 * </pre>
 * will visit the <em>same pixel positions</em>.
 * <p>
 * If the two {@link Container} are not the same, then
 * {@link RasterLocalizable} and {@link PositionableRasterSampler} have to be
 * used:
 * <pre>
 * 		else {
 * 			LocalizableCursor&lt T &gt cursor1 = img1.createLocalizableCursor();
 * 			LocalizableByDimCursor&lt S &gt cursor2 = img2.createLocalizableByDimCursor();
 * 			while ( cursor1.hasNext() )
 * 			{
 * 				cursor1.fwd();
 * 				cursor2.moveTo( cursor1 );
 * 			}
 * 		}
 * </pre>
 * This snippet will also traverse images the same way, but this is less efficient than the previous solution 
 * with identical containers.
 *<p>
 * This interface is for convenience only, it combines a set of interfaces and
 * might be used for type definition in your implementation.  Instead of this
 * interface, you can use a generic type that includes only the interfaces you
 * need, e.g.
 * 
 * < T extends RasterSampler< ? >, Iterator< ? > > 
 * 
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 * @param <T> - the {@link Type} this {@link RasterIterator} works on
 */
public interface RasterIterator< T extends Type< T > > extends RasterSampler< T >, RasterLocalizable, LinkableIterator< T > 
{	
}
