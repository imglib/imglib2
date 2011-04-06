//
// ImageOpener.java
//

/*
Imglib I/O logic using Bio-Formats.

Copyright (c) 2009, Stephan Preibisch & Stephan Saalfeld.
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

package mpicbg.imglib.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.DataTools;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.common.services.ServiceException;
import loci.formats.ChannelFiller;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLServiceImpl;
import mpicbg.imglib.exception.IncompatibleTypeException;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.basictypeaccess.PlanarAccess;
import mpicbg.imglib.img.basictypeaccess.array.ArrayDataAccess;
import mpicbg.imglib.img.basictypeaccess.array.ByteArray;
import mpicbg.imglib.img.basictypeaccess.array.CharArray;
import mpicbg.imglib.img.basictypeaccess.array.DoubleArray;
import mpicbg.imglib.img.basictypeaccess.array.FloatArray;
import mpicbg.imglib.img.basictypeaccess.array.IntArray;
import mpicbg.imglib.img.basictypeaccess.array.LongArray;
import mpicbg.imglib.img.basictypeaccess.array.ShortArray;
import mpicbg.imglib.img.planar.PlanarImgFactory;
import mpicbg.imglib.sampler.special.OrthoSliceCursor;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.ByteType;
import mpicbg.imglib.type.numeric.integer.IntType;
import mpicbg.imglib.type.numeric.integer.ShortType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedIntType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;
import mpicbg.imglib.type.numeric.real.DoubleType;
import mpicbg.imglib.type.numeric.real.FloatType;

/**
 * Reads in an imglib Image using Bio-Formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImgOpener implements StatusReporter {
	
	// -- Constants --

	public static final String X = "X";
	public static final String Y = "Y";
	public static final String Z = "Z";
	public static final String TIME = "Time";

	// -- Fields --

	private List<StatusListener> listeners = new ArrayList<StatusListener>();

	// -- ImageOpener methods --

	/**
	 * Reads in an imglib {@link Image} from the given source
	 * (e.g., file on disk).
	 */
	public <T extends RealType<T> & NativeType<T>> Img<T> openImage(String id)
		throws FormatException, IOException, IncompatibleTypeException
	{
		return openImage(id, new PlanarImgFactory<T>());
	}

	/**
	 * Reads in an imglib {@link Image} from the given source
	 * (e.g., file on disk), using the given {@link ContainerFactory}
	 * to construct the {@link Image}.
	 * 
	 * @throws IncompatibleTypeException if the Type of the Image is incompatible with
	 * the {@link ImgFactory}
	 */
	public <T extends RealType<T>> Img<T> openImage(String id,
		ImgFactory<T> imgFactory) throws FormatException, IOException, IncompatibleTypeException
	{
		final IFormatReader r = initializeReader(id);
		final T type = makeType(r.getPixelType());
		final ImgFactory<T> imageFactory = imgFactory.imgFactory( type ); 
		return openImage(r, imageFactory, type );
	}

	/**
	 * Reads in an imglib {@link Image} from the given source
	 * (e.g., file on disk), using the given {@link ImageFactory}
	 * to construct the {@link Image}.
	 */
	public <T extends RealType<T>> Img<T> openImage(String id,
		ImgFactory<T> imageFactory, final T type ) throws FormatException, IOException
	{
		final IFormatReader r = initializeReader(id);
		return openImage(r, imageFactory, type);
	}

	/**
	 * Reads in an imglib {@link Image} from the given initialized
	 * {@link IFormatReader}, using the given {@link ImageFactory}.
	 */
	public <T extends RealType<T>> Img<T> openImage(IFormatReader r,
		ImgFactory<T> imageFactory, final T type) throws FormatException, IOException
	{
		final String[] dimTypes = getDimTypes(r);
		final long[] dimLengths = getDimLengths(r);

		// TEMP - make suffix out of dimension types, until imglib supports them
		final String id = r.getCurrentFile();
		final File idFile = new File(id);
		String name = idFile.exists() ? idFile.getName() : id;
		name = encodeName(name, dimTypes);

		// create image object
		final Img<T> img = imageFactory.create( dimLengths, type );

		// set calibration of the image
		// TODO: set calibration
		// img.setCalibration( getCalibration(r,dimLengths) );

		// TODO - create better container types; either:
		// 1) an array container type using one byte array per plane
		// 2) as #1, but with an IFormatReader reference reading planes on demand
		// 3) as PlanarContainer, but with an IFormatReader reference
		//    reading planes on demand

		// PlanarContainer is useful for efficient access to pixels in ImageJ
		// (e.g., getPixels)
		// #1 is useful for efficient Bio-Formats import, and useful for tools
		//   needing byte arrays (e.g., BufferedImage Java3D texturing by reference)
		// #2 is useful for efficient memory use for tools wanting matching
		//   primitive arrays (e.g., virtual stacks in ImageJ)
		// #3 is useful for efficient memory use

		// get container
		final PlanarAccess<?> planarAccess = getPlanarAccess(img);
		final T inputType = makeType(r.getPixelType());
		final T outputType = type;
		final boolean compatibleTypes =
			outputType.getClass().isAssignableFrom(inputType.getClass());

		final long startTime = System.currentTimeMillis();

		// populate planes
		final int planeCount = r.getImageCount();
		if (planarAccess == null || !compatibleTypes) {
			// use cursor to populate planes

			// NB: This solution is general and works regardless of container,
			// but at the expense of performance both now and later.

			byte[] plane = null;
			for (int no = 0; no < planeCount; no++) {
				notifyListeners(new StatusEvent(no, planeCount,
					"Reading plane " + (no + 1) + "/" + planeCount));
				if (plane == null) plane = r.openBytes(no);
				else r.openBytes(no, plane);
				populatePlane(r, no, plane, img);
			}
		}
		else {
			// populate the values directly using PlanarAccess interface;
			// e.g., to a PlanarContainer

			byte[] plane = null;
			for (int no=0; no<planeCount; no++) {
				notifyListeners(new StatusEvent(no, planeCount,
					"Reading plane " + (no + 1) + "/" + planeCount));
				if (plane == null) plane = r.openBytes(no);
				else r.openBytes(no, plane);
				populatePlane(r, no, plane, planarAccess);
			}
		}
		r.close();

		final long endTime = System.currentTimeMillis();
		final float time = (endTime - startTime) / 1000f;
		notifyListeners(new StatusEvent(planeCount, planeCount,
			id + ": read " + planeCount + " planes in " + time + "s"));

		return img;
	}

	// TODO: eliminate getPlanarAccess in favor of utility method elsewhere.

	/** Obtains planar access instance backing the given image, if any. */
	@SuppressWarnings("unchecked")
	public static PlanarAccess<ArrayDataAccess<?>> getPlanarAccess( final Img<?> im )
	{
		if ( im instanceof PlanarAccess<?>) 
			return (PlanarAccess<ArrayDataAccess<?>>) im;
		else
			return null;
	}

	/** Converts Bio-Formats pixel type to imglib Type object. */
	@SuppressWarnings("unchecked")
	public static <T extends RealType<T>> T makeType(int pixelType) {
		final RealType<?> type;
		switch (pixelType) {
			case FormatTools.UINT8:
				type = new UnsignedByteType();
				break;
			case FormatTools.INT8:
				type = new ByteType();
				break;
			case FormatTools.UINT16:
				type = new UnsignedShortType();
				break;
			case FormatTools.INT16:
				type = new ShortType();
				break;
			case FormatTools.UINT32:
				type = new UnsignedIntType();
				break;
			case FormatTools.INT32:
				type = new IntType();
				break;
			case FormatTools.FLOAT:
				type = new FloatType();
				break;
			case FormatTools.DOUBLE:
				type = new DoubleType();
				break;
			default:
				type = null;
		}
		return (T) type;
	}

	/** Wraps raw primitive array in imglib Array object. */
	public static ArrayDataAccess<?> makeArray(Object array) {
		final ArrayDataAccess<?> access;
		if (array instanceof byte[]) {
			access = new ByteArray((byte[]) array);
		}
		else if (array instanceof char[]) {
			access = new CharArray((char[]) array);
		}
		else if (array instanceof double[]) {
			access = new DoubleArray((double[]) array);
		}
		else if (array instanceof int[]) {
			access = new IntArray((int[]) array);
		}
		else if (array instanceof float[]) {
			access = new FloatArray((float[]) array);
		}
		else if (array instanceof short[]) {
			access = new ShortArray((short[]) array);
		}
		else if (array instanceof long[]) {
			access = new LongArray((long[]) array);
		}
		else access = null;
		return access;
	}

	/** Converts the given image name back to a list of dimensional axis types. */
	public static String decodeName(String name) {
		final int lBracket = name.lastIndexOf(" [");
		return name.substring(0, lBracket);
	}

	/** Converts the given image name back to a list of dimensional axis types. */
	public static String[] decodeTypes(String name) {
		final int lBracket = name.lastIndexOf(" [");
		if (lBracket < 0) return new String[0];
		final int rBracket = name.lastIndexOf("]");
		if (rBracket < lBracket) return new String[0];
		return name.substring(lBracket + 2, rBracket).split(" ");
	}

	// -- StatusReporter methods --

	/** Adds a listener to those informed when progress occurs. */
	public void addStatusListener(StatusListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}

	/** Removes a listener from those informed when progress occurs. */
	public void removeStatusListener(StatusListener l) {
		synchronized (listeners) {
			listeners.remove(l);
		}
	}

	/** Notifies registered listeners of progress. */
	public void notifyListeners(StatusEvent e) {
		synchronized (listeners) {
			for (StatusListener l : listeners) l.statusUpdated(e);
		}
	}

	// -- Helper methods --

	/** Constructs and initializes a Bio-Formats reader for the given file. */
	private IFormatReader initializeReader(String id)
		throws FormatException, IOException
	{
		notifyListeners(new StatusEvent("Initializing " + id));

		IFormatReader r = null;
		r = new ImageReader();
		r = new ChannelFiller(r);
		r = new ChannelSeparator(r);

		//TODO: Replace code below
		/*
		try
		{
			ServiceFactory factory = new ServiceFactory();
			OMEXMLService service = factory.getInstance( OMEXMLService.class );
			IMetadata meta = service.createOMEXMLMetadata();
			r.setMetadataStore( meta );
		}
		catch (ServiceException e) 
		{
		}
		catch (DependencyException e )
		{			
		}
		*/
		
		try 
		{
			r.setMetadataStore( new OMEXMLServiceImpl().createOMEXMLMetadata() );
		} 
		catch (ServiceException e) 
		{
		}
		
		r.setId(id);

		return r;
	}

	/** Compiles an N-dimensional list of axis types from the given reader. */
	private String[] getDimTypes(IFormatReader r) {
		final int sizeX = r.getSizeX();
		final int sizeY = r.getSizeY();
		final int sizeZ = r.getSizeZ();
		final int sizeT = r.getSizeT();
		final String[] cDimTypes = r.getChannelDimTypes();
		final int[] cDimLengths = r.getChannelDimLengths();
		final String dimOrder = r.getDimensionOrder();
		final List<String> dimTypes = new ArrayList<String>();

		// add core dimensions
		for (char dim : dimOrder.toCharArray()) {
			switch (dim) {
				case 'X':
					if (sizeX > 1) dimTypes.add(X);
					break;
				case 'Y':
					if (sizeY > 1) dimTypes.add(Y);
					break;
				case 'Z':
					if (sizeZ > 1) dimTypes.add(Z);
					break;
				case 'T':
					if (sizeT > 1) dimTypes.add(TIME);
					break;
				case 'C':
					for (int c=0; c<cDimTypes.length; c++) {
						int len = cDimLengths[c];
						if (len > 1) dimTypes.add(cDimTypes[c]);
					}
					break;
			}
		}

		return dimTypes.toArray(new String[0]);
	}
	
	/** Retrieves calibration for X,Y,Z,T **/
	private float[] getCalibration( final IFormatReader r, final long[] dimensions )
	{		
		float[] calibration = new float[ dimensions.length ];		
		for ( int i = 0; i < calibration.length; ++i )
			calibration[ i ] = 1;
	
		try
		{
			final String dimOrder = r.getDimensionOrder().toUpperCase();
			final MetadataRetrieve retrieve = (MetadataRetrieve)r.getMetadataStore();
			
			// stage coordinates (per plane and series)
			// retrieve.getPlanePositionX( series, plane );
			// retrieve.getPlanePositionY( series, plane );
			// retrieve.getPlanePositionZ( series, plane );
			
			Double cal;
			
			final int posX = dimOrder.indexOf( 'X' );
			cal = retrieve.getPixelsPhysicalSizeX( 0 );
			if ( posX >= 0 && posX < calibration.length && cal != null && cal.floatValue() != 0 )
				calibration[ posX ] = cal.floatValue(); 
	
			final int posY = dimOrder.indexOf( 'Y' );
			cal = retrieve.getPixelsPhysicalSizeY( 0 );
			if ( posY >= 0 && posY < calibration.length && cal != null && cal.floatValue() != 0 )
				calibration[ posY ] = cal.floatValue();
	
			final int posZ = dimOrder.indexOf( 'Z' );
			cal = retrieve.getPixelsPhysicalSizeZ( 0 );
			if ( posZ >= 0 && posZ < calibration.length && cal != null && cal.floatValue() != 0 )
				calibration[ posZ ] = cal.floatValue();
			
			final int posT = dimOrder.indexOf( 'T' );
			retrieve.getPixelsTimeIncrement( 0 );
			cal = retrieve.getPixelsTimeIncrement( 0 );
			if ( posT >= 0 && posT < calibration.length && cal != null && cal.floatValue() != 0 )
				calibration[ posT ] = cal.floatValue();
		}
		catch ( Exception e ) 
		{
			// somehow an error occured reading the calibration
		}
		
		return calibration;
	}

	/** Compiles an N-dimensional list of axis lengths from the given reader. */
	private long[] getDimLengths(IFormatReader r) {
		final long sizeX = r.getSizeX();
		final long sizeY = r.getSizeY();
		final long sizeZ = r.getSizeZ();
		final long sizeT = r.getSizeT();
		//final String[] cDimTypes = r.getChannelDimTypes();
		final int[] cDimLengths = r.getChannelDimLengths();
		final String dimOrder = r.getDimensionOrder();

		final List<Long> dimLengthsList = new ArrayList<Long>();

		// add core dimensions
		for (int i=0; i<dimOrder.length(); i++) {
			final char dim = dimOrder.charAt(i);
			switch (dim) {
				case 'X':
					if (sizeX > 1) dimLengthsList.add(sizeX);
					break;
				case 'Y':
					if (sizeY > 1) dimLengthsList.add(sizeY);
					break;
				case 'Z':
					if (sizeZ > 1) dimLengthsList.add(sizeZ);
					break;
				case 'T':
					if (sizeT > 1) dimLengthsList.add(sizeT);
					break;
				case 'C':
					for (int c=0; c<cDimLengths.length; c++) {
						long len = cDimLengths[c];
						if (len > 1) dimLengthsList.add(len);
					}
					break;
			}
		}

		// convert result to primitive array
		final long[] dimLengths = new long[dimLengthsList.size()];
		for (int i=0; i<dimLengths.length; i++){
			dimLengths[i] = dimLengthsList.get(i);
		}
		return dimLengths;
	}

	/** Copies the current dimensional position into the given array. */
	private void getPosition(IFormatReader r, int no, long[] pos) {
		final int sizeX = r.getSizeX();
		final int sizeY = r.getSizeY();
		final int sizeZ = r.getSizeZ();
		final int sizeT = r.getSizeT();
		//final String[] cDimTypes = r.getChannelDimTypes();
		final int[] cDimLengths = r.getChannelDimLengths();
		final String dimOrder = r.getDimensionOrder();

		final int[] zct = r.getZCTCoords(no);

		int index = 0;
		for (int i=0; i<dimOrder.length(); i++) {
			final char dim = dimOrder.charAt(i);
			switch (dim) {
				case 'X':
					if (sizeX > 1) index++; // NB: Leave X axis position alone.
					break;
				case 'Y':
					if (sizeY > 1) index++; // NB: Leave Y axis position alone.
					break;
				case 'Z':
					if (sizeZ > 1) pos[index++] = zct[0];
					break;
				case 'T':
					if (sizeT > 1) pos[index++] = zct[2];
					break;
				case 'C':
					final int[] cPos = FormatTools.rasterToPosition(cDimLengths, zct[1]);
					for (int c=0; c<cDimLengths.length; c++) {
						if (cDimLengths[c] > 1) pos[index++] = cPos[c];
					}
					break;
			}
		}
	}

	/**
	 * Creates a name for the image based on the input source
	 * and dimensional axis types.
	 */
	private String encodeName(String id, String[] dimTypes) {
		final StringBuilder sb = new StringBuilder(id);
		boolean first = true;
		for (String dimType : dimTypes) {
			if (first) {
				sb.append(" [");
				first = false;
			}
			else sb.append(" ");
			sb.append(dimType);
		}
		if (!first) sb.append("]");
		return sb.toString();
	}

	@SuppressWarnings({"unchecked"})
	private void populatePlane(IFormatReader r,
		int no, byte[] plane, PlanarAccess planarAccess)
	{
		final int pixelType = r.getPixelType();
		final int bpp = FormatTools.getBytesPerPixel(pixelType);
		final boolean fp = FormatTools.isFloatingPoint(pixelType);
		final boolean little = r.isLittleEndian();
		Object planeArray = DataTools.makeDataArray(plane, bpp, fp, little);
		if (planeArray == plane) {
			// array was returned by reference; make a copy
			final byte[] planeCopy = new byte[plane.length];
			System.arraycopy(plane, 0, planeCopy, 0, plane.length);
			planeArray = planeCopy;
		}
		planarAccess.setPlane(no, makeArray(planeArray));
	}

	
	private <T extends RealType<T>> void populatePlane(IFormatReader r,
			int no, byte[] plane, Img<T> img )
		{
			final int sizeX = r.getSizeX();
			final int pixelType = r.getPixelType();
			final boolean little = r.isLittleEndian();

			final long[] dimLengths = getDimLengths(r);
			final long[] pos = new long[dimLengths.length];

			final int planeX = 0;
			final int planeY = 1;

			getPosition(r, no, pos);

			final OrthoSliceCursor<T> cursor = new OrthoSliceCursor<T>( img, planeX, planeY, pos );
			
			while ( cursor.hasNext() )
			{
				cursor.fwd();
				final int index = cursor.getIntPosition( planeX ) + cursor.getIntPosition( planeY ) * sizeX;
				final double value = decodeWord(plane, index, pixelType, little);
				cursor.get().setReal(value);
			}				
		}
	
	/*
	private <T extends RealType<T>> void populatePlane(IFormatReader r,
		int no, byte[] plane, LocalizableByDimCursor<T> cursor)
	{
		final int sizeX = r.getSizeX();
		final int sizeY = r.getSizeY();
		final int pixelType = r.getPixelType();
		final boolean little = r.isLittleEndian();

		final int[] dimLengths = getDimLengths(r);
		final int[] pos = new int[dimLengths.length];

		for (int y=0; y<sizeY; y++) {
			for (int x=0; x<sizeX; x++) {
				final int index = sizeY * x + y;
				final double value = decodeWord(plane, index, pixelType, little);
				// TODO - need IFormatReader method to get N-dimensional position
				getPosition(r, no, pos);
				pos[0] = x;
				pos[1] = y;
				cursor.setPosition(pos);
				cursor.getType().setReal(value);
			}
		}
	}
	*/
	
	private static double decodeWord(byte[] plane, int index,
		int pixelType, boolean little)
	{
		final double value;
		switch (pixelType) {
			case FormatTools.UINT8:
				value = plane[index] & 0xff;
				break;
			case FormatTools.INT8:
				value = plane[index];
				break;
			case FormatTools.UINT16:
				value = DataTools.bytesToShort(plane, 2 * index, 2, little) & 0xffff;
				break;
			case FormatTools.INT16:
				value = DataTools.bytesToShort(plane, 2 * index, 2, little);
				break;
			case FormatTools.UINT32:
				value = DataTools.bytesToInt(plane, 4 * index, 4, little) & 0xffffffff;
				break;
			case FormatTools.INT32:
				value = DataTools.bytesToInt(plane, 4 * index, 4, little);
				break;
			case FormatTools.FLOAT:
				value = DataTools.bytesToFloat(plane, 4 * index, 4, little);
				break;
			case FormatTools.DOUBLE:
				value = DataTools.bytesToDouble(plane, 4 * index, 4, little);
				break;
			default:
				value = Double.NaN;
		}
		return value;
	}

}
