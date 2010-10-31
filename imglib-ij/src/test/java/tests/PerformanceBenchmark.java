package tests;

import java.util.ArrayList;
import java.util.List;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ijaux.Constants;
import ijaux.PixLib;
import ijaux.hypergeom.BaseIndex;
import ijaux.hypergeom.PixelCube;
import ijaux.iter.array.ByteForwardIterator;
import ijaux.iter.seq.RasterForwardIterator;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.cell.CellContainerFactory;
import mpicbg.imglib.container.planar.PlanarContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.array.ArrayCursor;
import mpicbg.imglib.cursor.cell.CellCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusCursor;
import mpicbg.imglib.cursor.planar.PlanarCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.image.ImagePlusAdapter;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;

/**
 * Tests performance of uint8 image inversion with
 * raw byte array, imglib and pixlib libraries.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class PerformanceBenchmark {

	public static void main(String[] args) {
		PerformanceBenchmark bench = new PerformanceBenchmark();
		bench.testPerformance(3);
		System.exit(0);
	}

	// -- Helper methods --

	private void testPerformance(final int iterationCount) {
		// create objects and measure memory usage
		final int width = 4000, height = width;

		final List<Long> memUsage = new ArrayList<Long>();
		memUsage.add(getMemUsage());
		final byte[] data = createRawData(width, height);
		memUsage.add(getMemUsage());
		final ByteProcessor bp = createByteProcessor(data, width, height);
		memUsage.add(getMemUsage());
		final Image<UnsignedByteType> imgArray = createArrayImage(data, width, height);
		memUsage.add(getMemUsage());
		final Image<UnsignedByteType> imgCell = createCellImage(data, width, height);
		memUsage.add(getMemUsage());
		final Image<UnsignedByteType> imgPlanar = createPlanarImage(data, width, height);
		memUsage.add(getMemUsage());
		final Image<UnsignedByteType> imgImagePlus = createImagePlusImage(bp);
		memUsage.add(getMemUsage());
		final PixelCube<Byte, BaseIndex> pc = createPixelCube(bp);
		memUsage.add(getMemUsage());

		// report memory usage
		final long rawMem             = computeDifference(memUsage);
		final long ipMem              = computeDifference(memUsage);
		final long imgLibArrayMem     = computeDifference(memUsage);
		final long imgLibCellMem      = computeDifference(memUsage);
		final long imgLibPlanarMem    = computeDifference(memUsage);
		final long imgLibImagePlusMem = computeDifference(memUsage);
		final long pixLibMem          = computeDifference(memUsage);
		System.out.println("-- MEMORY OVERHEAD --");
		System.out.println("Raw: " + rawMem + " bytes");
		System.out.println("ImageJ: " + ipMem + " bytes");
		System.out.println("Imglib (Array): " + imgLibArrayMem + " bytes");
		System.out.println("Imglib (Cell): " + imgLibCellMem + " bytes");
		System.out.println("Imglib (Planar): " + imgLibPlanarMem + " bytes");
		System.out.println("Imglib (ImagePlus): " + imgLibImagePlusMem + " bytes");
		System.out.println("PixLib: " + pixLibMem + " bytes");
		System.out.println();

		// measure time performance
		System.out.println("-- TIME PERFORMANCE --");
		for (int i = 0; i < iterationCount; i++) 
		{
			System.gc();
			System.out.println("Iteration #" + (i + 1) + "/" + iterationCount + ":");
			final List<Long> times = new ArrayList<Long>();
			times.add(System.currentTimeMillis());
			invertRaw(data);
			times.add(System.currentTimeMillis());
			invertImageProcessor(bp);
			times.add(System.currentTimeMillis());
			invertArrayImage(imgArray);
			times.add(System.currentTimeMillis());
			invertCellImage(imgCell);
			times.add(System.currentTimeMillis());
			invertPlanarImage(imgPlanar);
			times.add(System.currentTimeMillis());
			invertImagePlusImage(imgImagePlus);
			times.add(System.currentTimeMillis());
			invertPixelCubeRefl(pc);
			times.add(System.currentTimeMillis());
			invertPixelCubeGenArray(pc);
			times.add(System.currentTimeMillis());
			invertPixelCubeByteArray(pc);
			times.add(System.currentTimeMillis());

			// report time performance
			final long rawTime             = computeDifference(times);
			final long ipTime              = computeDifference(times);
			final long imgLibArrayTime     = computeDifference(times);
			final long imgLibCellTime      = computeDifference(times);
			final long imgLibPlanarTime    = computeDifference(times);
			final long imgLibImagePlusTime = computeDifference(times);
			final long pixLibReflTime      = computeDifference(times);
			final long pixLibGenArrayTime  = computeDifference(times);
			final long pixLibByteArrayTime = computeDifference(times);
			reportTime("Raw", rawTime, rawTime, ipTime);
			reportTime("ImageJ", ipTime, rawTime, ipTime);
			reportTime("Imglib (Array)", imgLibArrayTime, rawTime, ipTime);
			reportTime("Imglib (Cell)", imgLibCellTime, rawTime, ipTime);
			reportTime("Imglib (Planar)", imgLibPlanarTime, rawTime, ipTime);
			reportTime("Imglib (ImagePlus)", imgLibImagePlusTime, rawTime, ipTime);
			reportTime("PixLib (Refl)", pixLibReflTime, rawTime, ipTime);
			reportTime("PixLib (Array: generic)", pixLibGenArrayTime, rawTime, ipTime);
			reportTime("PixLib (Array: byte)", pixLibByteArrayTime, rawTime, ipTime);
		}
	}

	private long getMemUsage() {
		Runtime r = Runtime.getRuntime();
		System.gc();
		System.gc();
		return r.totalMemory() - r.freeMemory();
	}
	
	private long computeDifference(List<Long> list) {
		long mem = list.remove(0);
		return list.get(0) - mem;
	}
	
	private void reportTime(String label, long time, long... otherTimes) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t");
		sb.append(label);
		sb.append(": ");
		sb.append(time);
		sb.append(" ms");
		for (long otherTime : otherTimes) {
			sb.append(", ");
			sb.append(time / (float) otherTime);
		}
		System.out.println(sb.toString());
	}

	// -- Creation methods --

	private byte[] createRawData(final int w, final int h) {
		final int size = w * h;
		final int max = w + h;
		byte[] data = new byte[size];
		int index = 0;
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				data[index++] = (byte) (255 * (x + y) / max);
			}
		}
		return data;
	}

	private ByteProcessor createByteProcessor(byte[] data, int width, int height) {
		return new ByteProcessor(width, height, data, null);
	}

	private PixelCube<Byte, BaseIndex> createPixelCube(ByteProcessor bp) {
		final PixLib<Byte, BaseIndex> plib = new PixLib<Byte, BaseIndex>();
		PixelCube<Byte, BaseIndex> pc = plib.cubeFrom(bp, Constants.BASE_INDEXING);
		return pc;
	}

	private Image<UnsignedByteType> createArrayImage(byte[] data, int width, int height) 
	{
		//return createImage(data, width, height, new ArrayContainerFactory());
		// NB: Avoid copying the data.
		final ByteAccess byteAccess = new ByteArray(data);
		final Array<UnsignedByteType, ByteAccess> array = new Array<UnsignedByteType, ByteAccess>(new ArrayContainerFactory(), byteAccess, new int[] {width, height}, 1);
		array.setLinkedType(new UnsignedByteType(array));
		return new Image<UnsignedByteType>(array, new UnsignedByteType());
		//return DevUtil.createImageFromArray(data, new int[] {width, height});
	}

	private Image<UnsignedByteType> createPlanarImage(byte[] data, int width, int height) {
		//return createImage(data, width, height, new PlanarContainerFactory());
		// NB: Avoid copying the data.
		PlanarContainer<UnsignedByteType, ByteArray> planarContainer = new PlanarContainer<UnsignedByteType, ByteArray>(new int[] {width, height}, 1);
		planarContainer.setPlane(0, new ByteArray(data));
		planarContainer.setLinkedType(new UnsignedByteType(planarContainer));
		return new Image<UnsignedByteType>(planarContainer, new UnsignedByteType());
	}

	private Image<UnsignedByteType> createCellImage(byte[] data, int width, int height) {
		return createImage(data, width, height, new CellContainerFactory());
	}

	private Image<UnsignedByteType> createImagePlusImage(ImageProcessor ip) {
		final ImagePlus imp = new ImagePlus("image", ip);
		return ImagePlusAdapter.wrapByte(imp);
	}

	private Image<UnsignedByteType> createImage(byte[] data, int width, int height, ContainerFactory cf) {
		final ImageFactory<UnsignedByteType> imageFactory =
			new ImageFactory<UnsignedByteType>(new UnsignedByteType(), cf);
		final int[] dim = {width, height};
		final Image<UnsignedByteType> img = imageFactory.createImage(dim);
		int index = 0;
		for (UnsignedByteType t : img) t.set(data[index++]);
		return img;
	}

	// -- Inversion methods --

	private void invertRaw(byte[] data) {
		for (int i=0; i<data.length; i++) {
			data[i] = (byte) (255 - data[i] & 0xFF);
		}
	}

	private void invertImageProcessor(ImageProcessor ip) {
		for (int i=0; i<ip.getPixelCount(); i++) {
			ip.set(i, 255 - ip.get(i));
		}
	}

	private void invertPixelCubeRefl(PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_FWD + Constants.IP_SINGLE);
		RasterForwardIterator<Byte> iterIn = (RasterForwardIterator<Byte>) pc.iterator();
		while (iterIn.hasNext()) {
			final byte b=(byte) (255 - iterIn.next() & 0xFF);
			iterIn.dec();
			iterIn.put(b);
		}
	}

	private void invertPixelCubeGenArray(PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_PRIM +Constants.IP_FWD + Constants.IP_SINGLE);
		ByteForwardIterator iter = (ByteForwardIterator) pc.iterator();
		for (final byte b : pc)
			iter.put((byte) (255 - b));
	}

	private void invertPixelCubeByteArray(PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_PRIM +Constants.IP_FWD + Constants.IP_SINGLE);
		ByteForwardIterator iter = (ByteForwardIterator) pc.iterator();
		while (iter.hasNext() ) {
			final byte b = (byte) ((255 - iter.nextByte()) & 0xFF);
			iter.dec();			
			iter.put(b);
		}
	}

	/** Generic version. */
	@SuppressWarnings("unused")
	private void invertImage(Image<UnsignedByteType> img) {
		final Cursor<UnsignedByteType> cursor = img.createCursor();
		for (final UnsignedByteType t : cursor)
			t.set( 255 - t.get() );
		cursor.close();
	}

	/** Explicit array version. */
	private void invertArrayImage(Image<UnsignedByteType> img) {
		final ArrayCursor<UnsignedByteType> cursor = (ArrayCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor)
			t.set(255 - t.get());
		cursor.close();
	}

	/** Explicit cell version. */
	private void invertCellImage(Image<UnsignedByteType> img) {
		final CellCursor<UnsignedByteType> cursor = (CellCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor)
			t.set(255 - t.get());
		cursor.close();
	}

	/** Explicit planar version. */
	private void invertPlanarImage(Image<UnsignedByteType> img) {
		final PlanarCursor<UnsignedByteType> cursor = (PlanarCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor)
			t.set(255 - t.get());
		cursor.close();
	}

	/** Explicit ImagePlus version. */
	private void invertImagePlusImage(Image<UnsignedByteType> img) {
		final ImagePlusCursor<UnsignedByteType> cursor = (ImagePlusCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor)
			t.set(255 - t.get());
		cursor.close();
	}

}
