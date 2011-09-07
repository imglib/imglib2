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

import net.imglib2.AbstractRandomAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.img.planar.PlanarRandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;


/**
 * This class manages read only random access to a virtual image. The random
 * access position is modified as needed. When user tries to get a value from
 * this accessor planes are loaded into memory by an IFormatReader as needed.
 * Planes are stored in a two dimensional PlanarImg that contains a single
 * plane. The user can modify the plane values but the changes are never saved
 * to disk.
 *  
 * @author Barry DeZonia
 *
 * @param <T>
 */
public class VirtualRandomAccess<T extends NativeType<T> & RealType<T>>
	extends AbstractRandomAccess<T>
{
	private VirtualImg<T> virtImage;
	private PlanarImg<T, ? extends ArrayDataAccess<?>> planeImg;
	private PlanarRandomAccess<T> accessor;
	private VirtualPlaneLoader planeLoader;

	/**
	 * Constructor
	 * 
	 * @param image - the VirtualImg to access randomly
	 */
	public VirtualRandomAccess(VirtualImg<T> image)
	{
		super(image.numDimensions());
		this.virtImage = image;
		long[] planeSize = new long[]{image.dimension(0), image.dimension(1)};
		this.planeImg =
			new PlanarImgFactory<T>().create(planeSize, image.getType().copy());
		this.planeLoader =
			new VirtualPlaneLoader(virtImage, planeImg, image.isByteOnly());
		planeLoader.loadPlane(position);
		// this initialization must follow loadPlane()
		this.accessor = planeImg.randomAccess();
	}

	@Override
	public void fwd(int d) {
		position[d]++;
	}

	@Override
	public void bck(int d) {
		position[d]--;
	}

	@Override
	public void move(long distance, int d) {
		position[d] += distance;
	}

	@Override
	public void setPosition(int[] pos) {
		for (int i = 0; i < position.length; i++)
			position[i] = pos[i];
	}

	@Override
	public void setPosition(long[] pos) {
		for (int i = 0; i < position.length; i++)
			position[i] = pos[i];
	}

	@Override
	public void setPosition(long pos, int d) {
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

}
