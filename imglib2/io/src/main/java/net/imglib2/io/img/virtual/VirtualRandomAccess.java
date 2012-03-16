/*
Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

/**
 *
 * @author Barry DeZonia
 *
 */
package net.imglib2.io.img.virtual;

import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;


/**
 * This class manages read only spatial access to a virtual image. Data
 * returned from get() can be written to but any changes are never saved
 * to disk.
 *
 * @author Barry DeZonia
 *
 */
public class VirtualRandomAccess<T extends NativeType<T> & RealType<T>>
	extends Point implements RandomAccess<T>
{
	private final VirtualImg<T> virtImage;
	private final VirtualAccessor<T> accessor;

	/**
	 * Constructor
	 *
	 * @param image - the VirtualImg to access randomly
	 */
	public VirtualRandomAccess(final VirtualImg<T> image)
	{
		super(image.numDimensions());
		this.accessor = new VirtualAccessor<T>(image);
		this.virtImage = image;
	}

	@Override
	public void setPosition(final long pos, final int d) {
		position[d] = pos;
	}

	@Override
	public VirtualRandomAccess<T> copy() {
		return new VirtualRandomAccess<T>(virtImage);
	}

	@Override
	public VirtualRandomAccess<T> copyRandomAccess() {
		return new VirtualRandomAccess<T>(virtImage);
	}

	@Override
	public T get() {
		return accessor.get(position);
	}

	public Object getCurrentPlane() {
		return accessor.getCurrentPlane();
	}
}
