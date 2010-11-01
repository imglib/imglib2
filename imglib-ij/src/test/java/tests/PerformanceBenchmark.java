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
 * Tests performance of uint8 image operations with
 * raw byte array, ImageJ, imglib and pixlib libraries.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class PerformanceBenchmark {

	final byte[] rawData;
	final ByteProcessor byteProc;
	final Image<UnsignedByteType> imgArray;
	final Image<UnsignedByteType> imgCell;
	final Image<UnsignedByteType> imgPlanar;
	final Image<UnsignedByteType> imgImagePlus;
	final PixelCube<Byte, BaseIndex> pixelCube;

	public static void main(final String[] args) {
		final PerformanceBenchmark bench = new PerformanceBenchmark();
		bench.testPerformance(10);
		System.exit(0);
	}

	/** Creates objects and measures memory usage. */
	public PerformanceBenchmark() {
		final int width = 4000, height = width;

		final List<Long> memUsage = new ArrayList<Long>();
		memUsage.add(getMemUsage());
		rawData = createRawData(width, height);
		memUsage.add(getMemUsage());
		byteProc = createByteProcessor(rawData, width, height);
		memUsage.add(getMemUsage());
		imgArray = createArrayImage(rawData, width, height);
		memUsage.add(getMemUsage());
		imgCell = createCellImage(rawData, width, height);
		memUsage.add(getMemUsage());
		imgPlanar = createPlanarImage(rawData, width, height);
		memUsage.add(getMemUsage());
		imgImagePlus = createImagePlusImage(byteProc);
		memUsage.add(getMemUsage());
		pixelCube = createPixelCube(byteProc);
		memUsage.add(getMemUsage());

		reportMemoryUsage(memUsage);
	}

	public void testPerformance(final int iterationCount) {
		testCheapPerformance(iterationCount);
		testExpensivePerformance(iterationCount);
	}

	// -- Helper methods --

	/** Measures performance of a cheap operation (image inversion). */
	private void testCheapPerformance(final int iterationCount) {
		System.out.println();
		System.out.println("-- TIME PERFORMANCE - CHEAP OPERATION --");
		for (int i = 0; i < iterationCount; i++) {
			System.gc();
			System.out.println("Iteration #" + (i + 1) + "/" + iterationCount + ":");
			final List<Long> times = new ArrayList<Long>();
			times.add(System.currentTimeMillis());
			invertRaw(rawData);
			times.add(System.currentTimeMillis());
			invertImageProcessor(byteProc);
			times.add(System.currentTimeMillis());
			invertArrayImage(imgArray);
			times.add(System.currentTimeMillis());
			invertCellImage(imgCell);
			times.add(System.currentTimeMillis());
			invertPlanarImage(imgPlanar);
			times.add(System.currentTimeMillis());
			invertImagePlusImage(imgImagePlus);
			times.add(System.currentTimeMillis());
			invertPixelCubeRefl(pixelCube);
			times.add(System.currentTimeMillis());
			invertPixelCubeGenArray(pixelCube);
			times.add(System.currentTimeMillis());
			invertPixelCubeByteArray(pixelCube);
			times.add(System.currentTimeMillis());

			reportTimePerformance(times);
		}
	}

	/** Measures performance of a computationally more expensive operation. */
	private void testExpensivePerformance(final int iterationCount) {
		System.out.println();
		System.out.println("-- TIME PERFORMANCE - EXPENSIVE OPERATION --");
		for (int i = 0; i < iterationCount; i++) {
			System.gc();
			System.out.println("Iteration #" + (i + 1) + "/" + iterationCount + ":");
			final List<Long> times = new ArrayList<Long>();
			times.add(System.currentTimeMillis());
			randomizeRaw(rawData);
			times.add(System.currentTimeMillis());
			randomizeImageProcessor(byteProc);
			times.add(System.currentTimeMillis());
			randomizeArrayImage(imgArray);
			times.add(System.currentTimeMillis());
			randomizeCellImage(imgCell);
			times.add(System.currentTimeMillis());
			randomizePlanarImage(imgPlanar);
			times.add(System.currentTimeMillis());
			randomizeImagePlusImage(imgImagePlus);
			times.add(System.currentTimeMillis());
			randomizePixelCubeRefl(pixelCube);
			times.add(System.currentTimeMillis());
			randomizePixelCubeGenArray(pixelCube);
			times.add(System.currentTimeMillis());
			randomizePixelCubeByteArray(pixelCube);
			times.add(System.currentTimeMillis());

			reportTimePerformance(times);
		}
	}

	private long getMemUsage() {
		Runtime r = Runtime.getRuntime();
		System.gc();
		System.gc();
		return r.totalMemory() - r.freeMemory();
	}

	private void reportMemoryUsage(final List<Long> memUsage) {
		final long rawMem             = computeDifference(memUsage);
		final long ipMem              = computeDifference(memUsage);
		final long imgLibArrayMem     = computeDifference(memUsage);
		final long imgLibCellMem      = computeDifference(memUsage);
		final long imgLibPlanarMem    = computeDifference(memUsage);
		final long imgLibImagePlusMem = computeDifference(memUsage);
		final long pixLibMem          = computeDifference(memUsage);
		System.out.println();
		System.out.println("-- MEMORY OVERHEAD --");
		System.out.println("Raw: " + rawMem + " bytes");
		System.out.println("ImageJ: " + ipMem + " bytes");
		System.out.println("Imglib (Array): " + imgLibArrayMem + " bytes");
		System.out.println("Imglib (Cell): " + imgLibCellMem + " bytes");
		System.out.println("Imglib (Planar): " + imgLibPlanarMem + " bytes");
		System.out.println("Imglib (ImagePlus): " + imgLibImagePlusMem + " bytes");
		System.out.println("PixLib: " + pixLibMem + " bytes");
	}

	private void reportTimePerformance(final List<Long> times) {
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

	private long computeDifference(final List<Long> list) {
		long mem = list.remove(0);
		return list.get(0) - mem;
	}

	private void reportTime(final String label, final long time, final long... otherTimes) {
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

	private ByteProcessor createByteProcessor(final byte[] data, final int width, final int height) {
		return new ByteProcessor(width, height, data, null);
	}

	private PixelCube<Byte, BaseIndex> createPixelCube(ByteProcessor bp) {
		final PixLib<Byte, BaseIndex> plib = new PixLib<Byte, BaseIndex>();
		PixelCube<Byte, BaseIndex> pc = plib.cubeFrom(bp, Constants.BASE_INDEXING);
		return pc;
	}

	private Image<UnsignedByteType> createArrayImage(final byte[] data, final int width, final int height) {
		//return createImage(data, width, height, new ArrayContainerFactory());
		// NB: Avoid copying the data.
		final ByteAccess byteAccess = new ByteArray(data);
		final Array<UnsignedByteType, ByteAccess> array = new Array<UnsignedByteType, ByteAccess>(new ArrayContainerFactory(), byteAccess, new int[] {width, height}, 1);
		array.setLinkedType(new UnsignedByteType(array));
		return new Image<UnsignedByteType>(array, new UnsignedByteType());
		//return DevUtil.createImageFromArray(data, new int[] {width, height});
	}

	private Image<UnsignedByteType> createPlanarImage(final byte[] data, final int width, final int height) {
		//return createImage(data, width, height, new PlanarContainerFactory());
		// NB: Avoid copying the data.
		PlanarContainer<UnsignedByteType, ByteArray> planarContainer = new PlanarContainer<UnsignedByteType, ByteArray>(new int[] {width, height}, 1);
		planarContainer.setPlane(0, new ByteArray(data));
		planarContainer.setLinkedType(new UnsignedByteType(planarContainer));
		return new Image<UnsignedByteType>(planarContainer, new UnsignedByteType());
	}

	private Image<UnsignedByteType> createCellImage(final byte[] data, final int width, final int height) {
		return createImage(data, width, height, new CellContainerFactory());
	}

	private Image<UnsignedByteType> createImagePlusImage(final ImageProcessor ip) {
		final ImagePlus imp = new ImagePlus("image", ip);
		return ImagePlusAdapter.wrapByte(imp);
	}

	private Image<UnsignedByteType> createImage(final byte[] data, final int width, final int height, final ContainerFactory cf) {
		final ImageFactory<UnsignedByteType> imageFactory =
			new ImageFactory<UnsignedByteType>(new UnsignedByteType(), cf);
		final int[] dim = {width, height};
		final Image<UnsignedByteType> img = imageFactory.createImage(dim);
		int index = 0;
		for (UnsignedByteType t : img) t.set(data[index++]);
		return img;
	}

	// -- Inversion methods --

	private void invertRaw(final byte[] data) {
		for (int i=0; i<data.length; i++) {
			final int value = data[i] & 0xff;
			final int result = 255 - value;
			data[i] = (byte) result;
		}
	}

	private void invertImageProcessor(final ImageProcessor ip) {
		for (int i=0; i<ip.getPixelCount(); i++) {
			final int value = ip.get(i);
			final int result = 255 - value;
			ip.set(i, result);
		}
	}

	/** Generic version. */
	@SuppressWarnings("unused")
	private void invertImage(final Image<UnsignedByteType> img) {
		final Cursor<UnsignedByteType> cursor = img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
		cursor.close();
	}

	/** Explicit array version. */
	private void invertArrayImage(final Image<UnsignedByteType> img) {
		final ArrayCursor<UnsignedByteType> cursor = (ArrayCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
		cursor.close();
	}

	/** Explicit cell version. */
	private void invertCellImage(final Image<UnsignedByteType> img) {
		final CellCursor<UnsignedByteType> cursor = (CellCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
		cursor.close();
	}

	/** Explicit planar version. */
	private void invertPlanarImage(final Image<UnsignedByteType> img) {
		final PlanarCursor<UnsignedByteType> cursor = (PlanarCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
		cursor.close();
	}

	/** Explicit ImagePlus version. */
	private void invertImagePlusImage(final Image<UnsignedByteType> img) {
		final ImagePlusCursor<UnsignedByteType> cursor = (ImagePlusCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
		cursor.close();
	}

	private void invertPixelCubeRefl(final PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_FWD + Constants.IP_SINGLE);
		RasterForwardIterator<Byte> iterIn = (RasterForwardIterator<Byte>) pc.iterator();
		while (iterIn.hasNext()) {
			final int value = iterIn.next() & 0xff;
			final int result = 255 - value;
			iterIn.dec();
			iterIn.put((byte) result);
		}
	}

	private void invertPixelCubeGenArray(final PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_PRIM +Constants.IP_FWD + Constants.IP_SINGLE);
		ByteForwardIterator iter = (ByteForwardIterator) pc.iterator();
		for (final byte b : pc) {
			final int value = b & 0xff;
			final int result = 255 - value;
			iter.put((byte) result);
		}
	}

	private void invertPixelCubeByteArray(final PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_PRIM +Constants.IP_FWD + Constants.IP_SINGLE);
		ByteForwardIterator iter = (ByteForwardIterator) pc.iterator();
		while (iter.hasNext() ) {
			final int value = iter.nextByte() & 0xff;
			final int result = 255 - value;
			iter.dec();
			iter.put((byte) result);
		}
	}

	// -- Randomization methods --

	private void randomizeRaw(final byte[] data) {
		for (int i=0; i<data.length; i++) {
			final int value = data[i] & 0xff;
			final double result = expensiveOperation(value);
			data[i] = (byte) result;
		}
	}

	private void randomizeImageProcessor(final ImageProcessor ip) {
		for (int i=0; i<ip.getPixelCount(); i++) {
			final int value = ip.get(i);
			final double result = expensiveOperation(value);
			ip.set(i, (int) result);
		}
	}

	/** Generic version. */
	@SuppressWarnings("unused")
	private void randomizeImage(final Image<UnsignedByteType> img) {
		final Cursor<UnsignedByteType> cursor = img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
		cursor.close();
	}

	/** Explicit array version. */
	private void randomizeArrayImage(final Image<UnsignedByteType> img) {
		final ArrayCursor<UnsignedByteType> cursor = (ArrayCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
		cursor.close();
	}

	/** Explicit cell version. */
	private void randomizeCellImage(final Image<UnsignedByteType> img) {
		final CellCursor<UnsignedByteType> cursor = (CellCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
		cursor.close();
	}

	/** Explicit planar version. */
	private void randomizePlanarImage(final Image<UnsignedByteType> img) {
		final PlanarCursor<UnsignedByteType> cursor = (PlanarCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
		cursor.close();
	}

	/** Explicit ImagePlus version. */
	private void randomizeImagePlusImage(final Image<UnsignedByteType> img) {
		final ImagePlusCursor<UnsignedByteType> cursor = (ImagePlusCursor<UnsignedByteType>) img.createCursor();
		for (final UnsignedByteType t : cursor) {
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
		cursor.close();
	}

	private void randomizePixelCubeRefl(final PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_FWD + Constants.IP_SINGLE);
		RasterForwardIterator<Byte> iterIn = (RasterForwardIterator<Byte>) pc.iterator();
		while (iterIn.hasNext()) {
			final int value = iterIn.next() & 0xff;
			final double result = expensiveOperation(value);
			iterIn.dec();
			iterIn.put((byte) result);
		}
	}

	private void randomizePixelCubeGenArray(final PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_PRIM +Constants.IP_FWD + Constants.IP_SINGLE);
		ByteForwardIterator iter = (ByteForwardIterator) pc.iterator();
		for (final byte b : pc) {
			final int value = b & 0xff;
			final double result = expensiveOperation(value);
			iter.put((byte) result);
		}
	}

	private void randomizePixelCubeByteArray(final PixelCube<Byte, BaseIndex> pc) {
		pc.setIterationPattern(Constants.IP_PRIM +Constants.IP_FWD + Constants.IP_SINGLE);
		ByteForwardIterator iter = (ByteForwardIterator) pc.iterator();
		while (iter.hasNext() ) {
			final int value = iter.nextByte() & 0xff;
			final double result = expensiveOperation(value);
			iter.dec();
			iter.put((int) result);
		}
	}

	private double expensiveOperation(final int value) {
		return 255 * Math.random() * Math.sin(value / 255.0);
	}

}
