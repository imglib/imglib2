/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.io.img.virtual;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.img.planar.PlanarRandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * This class manages read only random access to a virtual image. The random
 * access position is provided by the user of the get(position) call. When user
 * tries to get a value from this accessor planes are loaded into memory as
 * needed. Planes are stored in a two dimensional PlanarImg that contains a
 * single plane. The user can modify the data values returned from get() but the
 * changes are never saved to disk.
 * 
 * @author Barry DeZonia
 */
public class VirtualAccessor<T extends NativeType<T> & RealType<T>> {

	private final PlanarImg<T, ? extends ArrayDataAccess<?>> planeImg;
	private final PlanarRandomAccess<T> accessor;
	private final VirtualPlaneLoader planeLoader;

	public VirtualAccessor(final VirtualImg<T> virtImage) {
		final long[] planeSize =
			new long[] { virtImage.dimension(0), virtImage.dimension(1) };
		this.planeImg =
			new PlanarImgFactory<T>().create(planeSize, virtImage.getType().copy());
		this.planeLoader =
			new VirtualPlaneLoader(virtImage, planeImg, virtImage.isByteOnly());
		// this initialization must follow loadPlane()
		this.accessor = planeImg.randomAccess();
	}

	public T get(final long[] position) {

		if (planeLoader.virtualSwap(position)) {
			accessor.get().updateContainer(accessor);
		}

		accessor.setPosition(position[0], 0);
		accessor.setPosition(position[1], 1);

		return accessor.get();
	}

	public Object getCurrentPlane() {
		return planeImg.getPlane(0);
	}

}
