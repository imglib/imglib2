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

package net.imglib2.io.img.virtual;

import net.imglib2.AbstractCursor;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.img.planar.PlanarRandomAccess;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class VirtualCursor<T extends NativeType<T> & RealType<T>> extends AbstractCursor<T> {

	private VirtualImg<T> virtImage;
	private IntervalIterator iter;
	private PlanarImg<T, ? extends ArrayDataAccess<?>> planeImg;
	private PlanarRandomAccess<T> accessor;
	private long[] position;
	private VirtualPlaneLoader planeLoader;
	
	public VirtualCursor(VirtualImg<T> image) {
		super(image.numDimensions());
		this.virtImage = image;
		long[] fullDimensions = new long[image.numDimensions()];
		image.dimensions(fullDimensions);
		this.iter = new IntervalIterator(fullDimensions);
		long[] planeSize = new long[]{fullDimensions[0], fullDimensions[1]};
		this.planeImg = new PlanarImgFactory<T>().create(planeSize, image.getType().copy());
		this.position = new long[fullDimensions.length];
		this.planeLoader = new VirtualPlaneLoader(virtImage, planeImg, image.isByteOnly());
		planeLoader.loadPlane(position);
		// this initialization must follow loadPlane()
		this.accessor = planeImg.randomAccess();
	}
	
	@Override
	public T get() {
		iter.localize(position);
		// did we swap a plane?
		if (planeLoader.virtualSwap(position)) {
			// we did swap - make a new plane accessor
			accessor = planeImg.randomAccess();
			accessor.setPosition(position);
			// TODO - each plane swap hatches a new accessor. this might be too
			// costly. Figure out how to resync original accessor.
		}
		else { // we did not swap - adjust current accessor
			accessor.setPosition(position[0], 0);
			accessor.setPosition(position[1], 1);
		}
		return accessor.get();
	}

	@Override
	public void fwd() {
		iter.fwd();
	}

	@Override
	public void reset() {
		iter.reset();
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public void localize(long[] pos) {
		iter.localize(pos);
	}

	@Override
	public long getLongPosition(int d) {
		return iter.getLongPosition(d);
	}

	@Override
	public AbstractCursor<T> copy() {
		return new VirtualCursor<T>(virtImage);
	}

	@Override
	public AbstractCursor<T> copyCursor() {
		return new VirtualCursor<T>(virtImage);
	}
}
