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
package mpicbg.imglib;

import mpicbg.imglib.sampler.RasterIterator;
import mpicbg.imglib.type.Type;

/**
 * A Raster whose data elements (pixels) can be iterated.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface IterableRaster< T extends Type< T > > extends Iterable< T >
{
	/**
	 * Create a {@link RasterIterator} that will iterate all data elements in
	 * memory-optimal order.
	 * 
	 * @return RasterIterator<T> - the typed {@link RasterIterator}
	 */
	public RasterIterator< T > createRasterIterator();
	
	/**
	 * Create a {@link RasterIterator} that will iterate all data elements in
	 * memory-optimal order and tracks its location at each moving operation.
	 * 
	 * This {@link RasterIterator} is the preferred choice for implementations
	 * that require the iterator's location at each iteration step (e.g. for
	 * rendering a transformed image)
	 *  
	 * @return RasterIterator<T> - the typed {@link RasterIterator}
	 */
	public RasterIterator< T > createLocalizingRasterIterator();
}
