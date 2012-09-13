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

import loci.common.DataTools;
import loci.formats.FormatTools;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.io.ImgIOUtils;

/**
 * This class is responsible for loading one plane of data from an image using
 * an IFormatReader. The loading is done in a virtual fashion with planes loaded
 * when a desired position has not been loaded. The data is loaded into a
 * PlanarImg provided at construction time.
 * 
 * @author Barry DeZonia
 */
public class VirtualPlaneLoader {

	// -- instance variables --

	private final VirtualImg<?> virtImage;
	private final PlanarImg<?, ? extends ArrayDataAccess<?>> planeImg;
	private final long[] planeDims;
	private final long[] planePosLoaded;
	private final boolean bytesOnly;

	// -- constructor --

	/**
	 * Create a VirtualPlaneLoader on a VirtualImage swapping planes into a
	 * PlanarImg as needed.
	 * 
	 * @param image - the VirtualImg to load planes from
	 * @param planeImg - the PlanarImg to load planes into
	 * @param bytesOnly - a flag which defines whether planes passed around as
	 *          byte[]'s only or as other primitive array types (int[]'s, etc.).
	 */
	public VirtualPlaneLoader(final VirtualImg<?> image,
		final PlanarImg<?, ? extends ArrayDataAccess<?>> planeImg,
		final boolean bytesOnly)
	{
		this.virtImage = image;
		this.planeImg = planeImg;
		this.planeDims = new long[image.numDimensions() - 2];
		for (int i = 0; i < planeDims.length; i++)
			this.planeDims[i] = image.dimension(i + 2);
		this.planePosLoaded = new long[planeDims.length];
		this.bytesOnly = bytesOnly;
		loadPlane(new long[image.numDimensions()]);
	}

	// -- public interface --

	/**
	 * Makes sure the plane that contains the data of the given position is loaded
	 * into the PlanarImg. Only reads data from VirtualImg if not already loaded.
	 */
	public boolean virtualSwap(final long[] pos) {
		if (!planeLoaded(pos)) {
			loadPlane(pos);
			return true;
		}
		return false;
	}

	/**
	 * Always loads the plane that contains the data of the given position.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadPlane(final long[] pos) {
		for (int i = 0; i < planePosLoaded.length; i++)
			planePosLoaded[i] = pos[i + 2];
		final int planeNum = planeIndex(planeDims, planePosLoaded);
		byte[] planeBytes = null;
		try {
			planeBytes = virtImage.getReader().openBytes(planeNum);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException("cannot load plane " + planeNum);
		}
		Object primitivePlane;
		if (bytesOnly) {
			primitivePlane = planeBytes;
		}
		else { // want type from encoded bytes
			primitivePlane = typeConvert(planeBytes);
		}
		final ArrayDataAccess<?> wrappedPlane =
			ImgIOUtils.makeArray(primitivePlane);
		((PlanarImg) planeImg).setPlane(0, wrappedPlane);
	}

	// -- private helpers --

	private boolean planeLoaded(final long[] pos) {
		for (int i = 2; i < pos.length; i++)
			if (pos[i] != planePosLoaded[i - 2]) return false;
		return true;
	}

	private static int planeIndex(final long[] planeDimensions,
		final long[] planePos)
	{
		// TODO: Migrate this to FormatTools.rasterToPosition(long[], long[]).
		int index = 0;
		for (int i = 0; i < planePos.length; i++) {
			int delta = 1;
			for (int j = 1; j <= i; j++)
				delta *= planeDimensions[j - 1];
			index += delta * planePos[i];
		}
		return index;
	}

	private Object typeConvert(final byte[] bytes) {
		final int pixelType = virtImage.getReader().getPixelType();
		final int bytesPerPix = FormatTools.getBytesPerPixel(pixelType);
		final boolean floating = FormatTools.isFloatingPoint(pixelType);

		return DataTools.makeDataArray(bytes, bytesPerPix, floating, virtImage
			.getReader().isLittleEndian());
	}

}
