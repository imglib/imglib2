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

import loci.formats.FormatTools;
import loci.formats.IFormatReader;

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class VirtualImg<T extends NativeType<T>> extends AbstractImg<T> {

	private long[] dims;
	private IFormatReader reader;
	private T type;
	
	// TODO
	// The reader gets shared among all copy()'s and randomAccess()'s and
	// cursor()'s, etc. so there might be threading issues. Unless we enforce
	// that only one user of the reader can be defined. Unless reader is
	// already thread safe.
	
	public VirtualImg(long[] dims, IFormatReader reader)
	{
		super(dims);
		this.dims = dims.clone();
		this.reader = reader;
		this.type = pixelType(reader.getPixelType());
		checkPlaneShape();
	}

	// TODO
	/*
	public VirtualImg(String fileName) {
		super(ProblemWhatDimsDoIPassHereCannotResetLater);
		try {
			reader = ImgOpener.createReader(fileName, false);
			type = ImgOpener.makeType(reader.getPixelType());
			dims = ImgOpener.getDimLengths(reader);
		}
		catch (final FormatException e) {
			throw new ImgIOException(e);
		}
		catch (final IOException e) {
			throw new ImgIOException(e);
		}
	}
	*/
	
	@Override
	public RandomAccess<T> randomAccess() {
		return new VirtualRandomAccess<T>(this);
	}

	@Override
	public Cursor<T> cursor() {
		return new VirtualCursor<T>(this);
	}

	@Override
	public Cursor<T> localizingCursor() {
		// TODO - not supporting actual localizing cursor
		return new VirtualCursor<T>(this);
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		// TODO maybe support. For now, for simplicity, don't support
		return false;
	}

	@Override
	public ImgFactory<T> factory() {
		return new VirtualImgFactory<T>();
	}

	@Override
	public Img<T> copy() {
		return new VirtualImg<T>(dims, reader);
	}

	public T getType() {
		return type;
	}
	
	public IFormatReader getReader() {
		return reader;
	}
	
	// -- private helpers --
	
	private void checkPlaneShape() {
		if (dims.length < 2)
			throw new IllegalArgumentException("VirtualImg must be of dimension two or higher");

		checkDimension(0);
		checkDimension(1);
	}
	
	private void checkDimension(int d) {
		String dimsOrder = reader.getDimensionOrder();
		
		char dimName = Character.toUpperCase(dimsOrder.charAt(d));

		boolean ok = false;
		
		switch (dimName) {
			case 'X': ok = (dims[d] == reader.getSizeX()); break;
			case 'Y': ok = (dims[d] == reader.getSizeY()); break;
			case 'C': ok = (dims[d] == reader.getSizeC()); break;
			case 'Z': ok = (dims[d] == reader.getSizeZ()); break;
			case 'T': ok = (dims[d] == reader.getSizeT()); break;
			default:
				throw new IllegalArgumentException(
					"To match IFormatReader a VirtualImg currently only supports X, Y, Z, C, & T axes");
		}
			
		if (!ok)
			throw new IllegalArgumentException(
				"VirtualImg : size of dimension "+d+" does not match IFormatReader");
	}

	// TODO - adapted from ImgOpener::makeType(). Could not fix compiler errors to reuse it.
	//   Undo this borrowing.
	
	@SuppressWarnings("unchecked")
	private T pixelType(int pixelType) {
		final NativeType<?> imgLibType;
		switch (pixelType) {
			case FormatTools.UINT8:
				imgLibType = new UnsignedByteType();
				break;
			case FormatTools.INT8:
				imgLibType = new ByteType();
				break;
			case FormatTools.UINT16:
				imgLibType = new UnsignedShortType();
				break;
			case FormatTools.INT16:
				imgLibType = new ShortType();
				break;
			case FormatTools.UINT32:
				imgLibType = new UnsignedIntType();
				break;
			case FormatTools.INT32:
				imgLibType = new IntType();
				break;
			case FormatTools.FLOAT:
				imgLibType = new FloatType();
				break;
			case FormatTools.DOUBLE:
				imgLibType = new DoubleType();
				break;
			default:
				imgLibType = null;
		}
		return (T) imgLibType;
	}
}
