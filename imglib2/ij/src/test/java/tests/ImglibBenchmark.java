package tests;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.cell.CellCursor;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.imageplus.ByteImagePlus;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.img.list.ListCursor;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.img.planar.PlanarCursor;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;

/**
 * Modified version of {@link PerformanceBenchmark} to generate
 * plots for imglib2-poster.
 *
 * @author Tobias Pietzsch
 */
public class ImglibBenchmark {

	private static final boolean SAVE_RESULTS_TO_DISK = true;

	private static final String METHOD_RAW = "Raw";
	private static final String METHOD_IMAGEJ = "ImageJ";
	private static final String METHOD_IMGLIB_ARRAY = "Imglib (Array)";
	private static final String METHOD_IMGLIB_CELL = "Imglib (Cell)";
	private static final String METHOD_IMGLIB_PLANAR = "Imglib (Planar)";
	private static final String METHOD_IMGLIB_IMAGEPLUS = "Imglib (ImagePlus)";
	private static final String METHOD_IMGLIB_LIST = "Imglib (List)";

//	private final int imageSize;
	private final int numDimensions;
	private final long[] dimensions;
	private final byte[] rawData;
	private final ByteProcessor byteProc;
	private final ArrayImg<UnsignedByteType, ByteArray> imgArray;
	private final CellImg<UnsignedByteType, ByteArray, ?> imgCell;
	private final PlanarImg<UnsignedByteType, ByteArray> imgPlanar;
	private final ByteImagePlus<UnsignedByteType> imgImagePlus;
	private final ListImg<UnsignedByteType> imgList;

	/**
	 * List of timing results.
	 *
	 * Each element of the list represents an iteration.
	 * Each entry maps the method name to the time measured.
	 */
	private final List<Map<String, Long>> results =
		new ArrayList<Map<String, Long>>();

	public static void main(final String[] args) throws IOException {
		final boolean testListImg = false;
		final int iterations = 10;
		final long[] dimensions;
		if (args.length > 0)
		{
			dimensions = new long[ args.length ];
			for ( int d = 0; d < args.length; ++d )
				dimensions[ d ] = Long.parseLong( args[0] );
		}
		else
		{
			//dimensions = new long[] {100000000};
			//dimensions = new long[] {8192,8192};
			dimensions = new long[] {50000,50000};
			//dimensions = new long[] {384,384,384};
			//dimensions = new long[] {28, 28, 28, 28, 28, 28 };
		}
		final ImglibBenchmark bench = new ImglibBenchmark(dimensions, testListImg);
		bench.testPerformance(iterations);
		System.exit(0);
	}

	/** Creates objects and measures memory usage. */
	public ImglibBenchmark(final long[] dimensions, final boolean testListImg) {
		this.numDimensions = dimensions.length;
		this.dimensions = dimensions;
		System.out.println();
		System.out.print( "===== " );
		for ( int d = 0; d < numDimensions; ++d )
			System.out.print( dimensions[ d ] + ( d == numDimensions - 1 ? "" : " x " ) );
		System.out.println( " =====" );

		final List<Long> memUsage = new ArrayList<Long>();
		memUsage.add(getMemUsage());
		rawData = createRawData();
		memUsage.add(getMemUsage());
		byteProc = createByteProcessor(rawData);
		memUsage.add(getMemUsage());
		imgArray = createArrayImage(rawData);
		memUsage.add(getMemUsage());
		imgCell = createCellImage();
		memUsage.add(getMemUsage());
		imgPlanar = createPlanarImage(rawData);
		memUsage.add(getMemUsage());
		imgImagePlus = createImagePlusImage(byteProc);
		memUsage.add(getMemUsage());
		if ( testListImg )
			imgList = createListImage();
		else
			imgList = null;
		memUsage.add(getMemUsage());

		reportMemoryUsage(memUsage);
	}

	public void testPerformance(final int iterationCount) throws IOException {
		// initialize results map
		results.clear();
		for (int i = 0; i < iterationCount; i++) {
			final Map<String, Long> entry = new HashMap<String, Long>();
			results.add(entry);
		}
		testCheapPerformance(iterationCount);
		if (SAVE_RESULTS_TO_DISK) saveResults("cheap");
		testExpensivePerformance(iterationCount);
		if (SAVE_RESULTS_TO_DISK) saveResults("expensive");
	}

	/**
	 * Saves benchmark results to the given CSV file on disk.
	 *
	 * The motivation is to produce two charts:
	 *
	 * 1) performance by iteration number (on each size of image)
	 *    one line graph per method
	 *    X axis = iteration number
	 *    Y axis = time needed
	 *
	 * 2) average performance by image size (for first iteration, and 10th)
	 *    one line graph per method
	 *    X axis = size of image
	 *    Y axis = time needed
	 *
	 * The CSV file produced enables graph #1 very easily.
	 * For graph #2, results from several files must be combined.
	 */
	public void saveResults(final String prefix) throws IOException {
		final StringBuilder sb = new StringBuilder();

		// write header
		final Map<String, Long> firstEntry = results.get(0);
		final String[] methods = firstEntry.keySet().toArray(new String[0]);
		Arrays.sort(methods);
		sb.append("Iteration");
		for (final String method : methods) {
			sb.append("\t");
			sb.append(method);
		}
		sb.append("\n");

		// write data
		for (int iter = 0; iter < results.size(); iter++) {
			final Map<String, Long> entry = results.get(iter);
			sb.append(iter + 1);
			for (final String method : methods) {
				sb.append("\t");
				sb.append(entry.get(method));
			}
			sb.append("\n");
		}

		// write to disk
		String path = "results-" + prefix + "-";
		for ( int d = 0; d < numDimensions; ++d )
			path += dimensions[ d ] + ( d == numDimensions - 1 ? ".csv" : "x");
		final PrintWriter out = new PrintWriter(new FileWriter(path));
		out.print(sb.toString());
		out.close();
	}

	// -- Helper methods --

	/** Measures performance of a cheap operation (image inversion). */
	private void testCheapPerformance(final int iterationCount) {
		System.out.println();
		System.out.println("-- TIME PERFORMANCE - CHEAP OPERATION --");
		final long[] min = new long[ 7 ];
		final long[] max = new long[ 7 ];
		final long[] avg = new long[ 7 ];
		final long[][] median = new long[ 7 ][ iterationCount ];

		for (int i = 0; i < iterationCount; i++) {
			System.gc();
			System.out.println("Iteration #" + (i + 1) + "/" + iterationCount + ":");
			final List<Long> times = new ArrayList<Long>();

			times.add(System.currentTimeMillis());
			if ( rawData != null ) invertRaw(rawData);
			times.add(System.currentTimeMillis());
			if ( byteProc != null ) invertImageProcessor(byteProc);
			times.add(System.currentTimeMillis());
			if ( imgArray != null ) invertArrayImage(imgArray);
			times.add(System.currentTimeMillis());
			if ( imgCell != null ) invertCellImage(imgCell);
			times.add(System.currentTimeMillis());
			if ( imgPlanar != null ) invertPlanarImage(imgPlanar);
			times.add(System.currentTimeMillis());
			if ( imgImagePlus != null ) invertImagePlusImage(imgImagePlus);
			times.add(System.currentTimeMillis());
			if ( imgList != null ) invertListImage(imgList);
			times.add(System.currentTimeMillis());

			logTimePerformance(i, times, min, max, avg, median );
		}

		reportMinAvgMax( min, max, avg, median, iterationCount );
	}

	/** Measures performance of a computationally more expensive operation. */
	private void testExpensivePerformance(final int iterationCount) {
		System.out.println();
		System.out.println("-- TIME PERFORMANCE - EXPENSIVE OPERATION --");
		final long[] min = new long[ 7 ];
		final long[] max = new long[ 7 ];
		final long[] avg = new long[ 7 ];
		final long[][] median = new long[ 7 ][ iterationCount ];

		for (int i = 0; i < iterationCount; i++) {
			System.gc();
			System.out.println("Iteration #" + (i + 1) + "/" + iterationCount + ":");
			final List<Long> times = new ArrayList<Long>();
			times.add(System.currentTimeMillis());
			if ( rawData != null ) randomizeRaw(rawData);
			times.add(System.currentTimeMillis());
			if ( byteProc != null ) randomizeImageProcessor(byteProc);
			times.add(System.currentTimeMillis());
			if ( imgArray != null ) randomizeArrayImage(imgArray);
			times.add(System.currentTimeMillis());
			if ( imgCell != null ) randomizeCellImage(imgCell);
			times.add(System.currentTimeMillis());
			if ( imgPlanar != null ) randomizePlanarImage(imgPlanar);
			times.add(System.currentTimeMillis());
			if ( imgImagePlus != null ) randomizeImagePlusImage(imgImagePlus);
			times.add(System.currentTimeMillis());
			if ( imgList != null ) randomizeListImage(imgList);
			times.add(System.currentTimeMillis());

			logTimePerformance(i, times, min, max, avg, median );
		}

		reportMinAvgMax( min, max, avg, median, iterationCount );
	}

	private void reportMinAvgMax(final long[] min, final long[] max, final long[] avg, final long[][] median, final int iterationCount) {
		for ( int i = 0; i < avg.length; ++i )
			avg[ i ] /= iterationCount;
		
		

		System.out.println( "-- SUMMARY --" );
		System.out.println(METHOD_RAW + " min: " + min[ 0 ] + " avg: " +  avg[ 0 ] + " max: " +  max[ 0 ] + " median: " + Util.computeMedian( median[ 0 ] ) );
		System.out.println(METHOD_IMAGEJ + " min: " + min[ 1 ] + " avg: " +  avg[ 1 ] + " max: " +  max[ 1 ] + " median: " + Util.computeMedian( median[ 1 ] ) );
		System.out.println(METHOD_IMGLIB_ARRAY + " min: " + min[ 2 ] + " avg: " +  avg[ 2 ] + " max: " +  max[ 2 ] + " median: " + Util.computeMedian( median[ 2 ] ) );
		System.out.println(METHOD_IMGLIB_CELL + " min: " + min[ 3 ] + " avg: " +  avg[ 3 ] + " max: " +  max[ 3 ] + " median: " + Util.computeMedian( median[ 3 ] ) );
		System.out.println(METHOD_IMGLIB_PLANAR + " min: " + min[ 4 ] + " avg: " +  avg[ 4 ] + " max: " +  max[ 4 ] + " median: " + Util.computeMedian( median[ 4 ] ) );
		System.out.println(METHOD_IMGLIB_IMAGEPLUS + " min: " + min[ 5 ] + " avg: " +  avg[ 5 ] + " max: " +  max[ 5 ] + " median: " + Util.computeMedian( median[ 5 ] ) );
		System.out.println(METHOD_IMGLIB_LIST + " min: " + min[ 6 ] + " avg: " +  avg[ 6 ] + " max: " +  max[ 6 ] + " median: " + Util.computeMedian( median[ 6 ] ) );
	}
	private long getMemUsage() {
		final Runtime r = Runtime.getRuntime();
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
		final long imgLibListMem      = computeDifference(memUsage);
		System.out.println();
		System.out.println("-- MEMORY OVERHEAD --");
		System.out.println(METHOD_RAW + ": " + rawMem + " bytes");
		System.out.println(METHOD_IMAGEJ + ": " + ipMem + " bytes");
		System.out.println(METHOD_IMGLIB_ARRAY + ": " + imgLibArrayMem + " bytes");
		System.out.println(METHOD_IMGLIB_CELL + ": " + imgLibCellMem + " bytes");
		System.out.println(METHOD_IMGLIB_PLANAR + ": " + imgLibPlanarMem + " bytes");
		System.out.println(METHOD_IMGLIB_IMAGEPLUS + ": " + imgLibImagePlusMem + " bytes");
		System.out.println(METHOD_IMGLIB_LIST + ": " + imgLibListMem + " bytes");
	}

	private void logTimePerformance(final int iter, final List<Long> times, final long[] min, final long[] max, final long[] avg, final long[][] median) {
		long rawTime             = computeDifference(times);
		long ipTime              = computeDifference(times);
		long imgLibArrayTime     = computeDifference(times);
		long imgLibCellTime      = computeDifference(times);
		long imgLibPlanarTime    = computeDifference(times);
		long imgLibImagePlusTime = computeDifference(times);
		long imgLibListTime      = computeDifference(times);

		if ( rawData == null )
			rawTime = -1;
		if ( byteProc == null )
			ipTime = -1;
		if ( imgArray == null )
			imgLibArrayTime = -1;
		if ( imgCell == null )
			imgLibCellTime = -1;
		if ( imgPlanar == null )
			imgLibPlanarTime = -1;
		if ( imgImagePlus == null )
			imgLibImagePlusTime = -1;
		if ( imgList == null )
			imgLibListTime = -1;

		final Map<String, Long> entry = results.get(iter);
		entry.put(METHOD_RAW, rawTime);
		entry.put(METHOD_IMAGEJ, ipTime);
		entry.put(METHOD_IMGLIB_ARRAY, imgLibArrayTime);
		entry.put(METHOD_IMGLIB_CELL, imgLibCellTime);
		entry.put(METHOD_IMGLIB_PLANAR, imgLibPlanarTime);
		entry.put(METHOD_IMGLIB_IMAGEPLUS, imgLibImagePlusTime);
		entry.put(METHOD_IMGLIB_LIST, imgLibListTime);

		reportTime(METHOD_RAW, rawTime, rawTime, ipTime);
		reportTime(METHOD_IMAGEJ, ipTime, rawTime, ipTime);
		reportTime(METHOD_IMGLIB_ARRAY, imgLibArrayTime, rawTime, ipTime);
		reportTime(METHOD_IMGLIB_CELL, imgLibCellTime, rawTime, ipTime);
		reportTime(METHOD_IMGLIB_PLANAR, imgLibPlanarTime, rawTime, ipTime);
		reportTime(METHOD_IMGLIB_IMAGEPLUS, imgLibImagePlusTime, rawTime, ipTime);
		reportTime(METHOD_IMGLIB_LIST, imgLibListTime, rawTime, ipTime);

		if ( iter == 0 )
		{
			min[ 0 ] = max[ 0 ] = avg[ 0 ] = median[ 0 ][ 0 ] = rawTime;
			min[ 1 ] = max[ 1 ] = avg[ 1 ] = median[ 1 ][ 0 ] = ipTime;
			min[ 2 ] = max[ 2 ] = avg[ 2 ] = median[ 2 ][ 0 ] = imgLibArrayTime;
			min[ 3 ] = max[ 3 ] = avg[ 3 ] = median[ 3 ][ 0 ] = imgLibCellTime;
			min[ 4 ] = max[ 4 ] = avg[ 4 ] = median[ 4 ][ 0 ] = imgLibPlanarTime;
			min[ 5 ] = max[ 5 ] = avg[ 5 ] = median[ 5 ][ 0 ] = imgLibImagePlusTime;
			min[ 6 ] = max[ 6 ] = avg[ 6 ] = median[ 6 ][ 0 ] = imgLibListTime;
		}
		else
		{
			min[ 0 ] = Math.min( min[ 0 ], rawTime );
			max[ 0 ] = Math.max( max[ 0 ], rawTime );
			median[ 0 ][ iter ] = rawTime;
			avg[ 0 ] += rawTime;

			min[ 1 ] = Math.min( min[ 1 ], ipTime );
			max[ 1 ] = Math.max( max[ 1 ], ipTime );
			median[ 1 ][ iter ] = ipTime;
			avg[ 1 ] += ipTime;

			min[ 2 ] = Math.min( min[ 2 ], imgLibArrayTime );
			max[ 2 ] = Math.max( max[ 2 ], imgLibArrayTime );
			median[ 2 ][ iter ] = imgLibArrayTime;
			avg[ 2 ] += imgLibArrayTime;

			min[ 3 ] = Math.min( min[ 3 ], imgLibCellTime );
			max[ 3 ] = Math.max( max[ 3 ], imgLibCellTime );
			median[ 3 ][ iter ] = imgLibCellTime;
			avg[ 3 ] += imgLibCellTime;

			min[ 4 ] = Math.min( min[ 4 ], imgLibPlanarTime );
			max[ 4 ] = Math.max( max[ 4 ], imgLibPlanarTime );
			median[ 4 ][ iter ] = imgLibPlanarTime;
			avg[ 4 ] += imgLibPlanarTime;

			min[ 5 ] = Math.min( min[ 5 ], imgLibImagePlusTime );
			max[ 5 ] = Math.max( max[ 5 ], imgLibImagePlusTime );
			median[ 5 ][ iter ] = imgLibImagePlusTime;
			avg[ 5 ] += imgLibImagePlusTime;

			min[ 6 ] = Math.min( min[ 6 ], imgLibListTime );
			max[ 6 ] = Math.max( max[ 6 ], imgLibListTime );
			median[ 6 ][ iter ] = imgLibListTime;
			avg[ 6 ] += imgLibListTime;
		}
	}

	private long computeDifference(final List<Long> list) {
		final long mem = list.remove(0);
		return list.get(0) - mem;
	}

	private void reportTime(final String label, final long time, final long... otherTimes) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\t");
		sb.append(label);
		sb.append(": ");
		if ( time == -1 )
			sb.append( "--, --, --" );
		else
		{
			sb.append(time);
			sb.append(" ms");
			for (final long otherTime : otherTimes) {
				sb.append(", ");
				if ( otherTime == -1 )
					sb.append( "--" );
				else
					sb.append(time / (float) otherTime);
			}
		}
		System.out.println(sb.toString());
	}

	// -- Creation methods --

	private byte[] createRawData() {
		long size = 1;
		for ( int d = 0; d < numDimensions; ++d )
			size *= dimensions[ d ];

		if ( size > Integer.MAX_VALUE )
			return null;

		final byte[] data = new byte[ ( int ) size];
		for ( int i = 0; i < size; ++i )
			data[i] = (byte) ( i % 256 );

		return data;
	}

	private ByteProcessor createByteProcessor(final byte[] data) {
		if ( data == null || numDimensions != 2 )
			return null;
		return new ByteProcessor( ( int ) dimensions[ 0 ], ( int ) dimensions[ 1 ], data, null );
	}

	private ArrayImg<UnsignedByteType, ByteArray> createArrayImage(final byte[] data) {
		//return createImage(data, width, height, new ArrayContainerFactory());
		// NB: Avoid copying the data.
		if ( data == null )
			return null;
		final ByteArray byteAccess = new ByteArray(data);
		final ArrayImg<UnsignedByteType, ByteArray> array = new ArrayImg<UnsignedByteType, ByteArray>( byteAccess, dimensions, 1 );
		array.setLinkedType(new UnsignedByteType(array));
		return array;
		//return DevUtil.createImageFromArray(data, new int[] {width, height});
	}

	private PlanarImg<UnsignedByteType, ByteArray> createPlanarImage(final byte[] data) {
		//return createImage(data, width, height, new PlanarContainerFactory());
		if ( numDimensions == 2 && data != null )
		{
			// NB: Avoid copying the data.
			final PlanarImg<UnsignedByteType, ByteArray> planarContainer = new PlanarImg<UnsignedByteType, ByteArray>(dimensions, 1);
			planarContainer.setPlane(0, new ByteArray(data));
			planarContainer.setLinkedType(new UnsignedByteType(planarContainer));
			return planarContainer;
		}
		if ( dimensions.length == 1 )
			return null;
		if ( dimensions[ 0 ] * dimensions[ 1 ] > Integer.MAX_VALUE )
			return null;
		
		@SuppressWarnings( "unchecked" )
		final
		PlanarImg<UnsignedByteType, ByteArray> planarContainer = ( PlanarImg<UnsignedByteType, ByteArray> ) createImage( dimensions, new PlanarImgFactory< UnsignedByteType >() );
		return planarContainer;
	}

	private CellImg<UnsignedByteType, ByteArray, ?> createCellImage() {
		final UnsignedByteType type = new UnsignedByteType();
		final int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / type.getEntitiesPerPixel(), 1.0 / numDimensions );

		// test whether there were rounding errors and cellSize is actually too big
		long t = 1;
		for ( int d = 0; d < numDimensions; ++d )
			t *= cellSize;
		t *= type.getEntitiesPerPixel();
		if ( t > Integer.MAX_VALUE )
			throw new RuntimeException( "there were rounding errors and cellSize is actually too big" );

		@SuppressWarnings( "unchecked" )
		final
		CellImg<UnsignedByteType, ByteArray, ?> cellContainer = ( CellImg<UnsignedByteType, ByteArray, ?> ) createImage( dimensions, new CellImgFactory< UnsignedByteType >( cellSize ) );
		return cellContainer;
	}

	private ByteImagePlus<UnsignedByteType> createImagePlusImage(final ImageProcessor ip) {
		if ( dimensions.length == 1 )
			return null;
		if ( ip != null )
		{
			final ImagePlus imp = new ImagePlus("image", ip);
			return ImagePlusAdapter.wrapByte(imp);
		}
		if ( dimensions[ 0 ] * dimensions[ 1 ] > Integer.MAX_VALUE )
			return null;
		if ( numDimensions > 5 )
			return null;
		final ByteImagePlus<UnsignedByteType> imagePlusContainer = ( ByteImagePlus<UnsignedByteType> ) createImage( dimensions, new ImagePlusImgFactory< UnsignedByteType >() );
		return imagePlusContainer;
	}

	private ListImg<UnsignedByteType> createListImage() {
		if ( dimensions[ 0 ] * dimensions[ 1 ] > Integer.MAX_VALUE )
			return null;
		@SuppressWarnings( "unchecked" )
		final
		ListImg<UnsignedByteType> listContainer = ( ListImg<UnsignedByteType> ) createImage( dimensions, new ListImgFactory< UnsignedByteType >() );
		return listContainer;
	}

	private Img< UnsignedByteType > createImage(final long[] dims, final ImgFactory< UnsignedByteType > cf )
	{
		final Img< UnsignedByteType > img = cf.create( dims, new UnsignedByteType() );
		long i = 0;
		for ( final UnsignedByteType t : img )
			t.set( ( int ) ( i++ % 256 ) );
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
	@SuppressWarnings( "unused" )
	private void invertImage(final Img<UnsignedByteType> img) {
		for (final UnsignedByteType t : img) {
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
	}

	/** Explicit array version. */
	private void invertArrayImage(final ArrayImg<UnsignedByteType, ByteArray> img) {
		final ArrayCursor<UnsignedByteType> c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
	}

	/** Explicit cell version. */
	private void invertCellImage(final CellImg<UnsignedByteType, ByteArray, ?> img) {
		final CellCursor< UnsignedByteType, ByteArray, ? > c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
	}

	/** Explicit planar version. */
	private void invertPlanarImage(final PlanarImg<UnsignedByteType, ByteArray> img) {
		final PlanarCursor<UnsignedByteType> c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
	}

	/** Explicit ImagePlus version. */
	private void invertImagePlusImage(final ByteImagePlus<UnsignedByteType> img) {
		final PlanarCursor<UnsignedByteType> c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
		}
	}

	/** Explicit list version. */
	private void invertListImage(final ListImg<UnsignedByteType> img) {
		final ListCursor<UnsignedByteType> c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final int result = 255 - value;
			t.set(result);
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
	@SuppressWarnings( "unused" )
	private void randomizeImage(final Img<UnsignedByteType> img) {
		for (final UnsignedByteType t : img) {
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
	}

	/** Explicit array version. */
	private void randomizeArrayImage(final ArrayImg<UnsignedByteType, ByteArray> img) {
		final ArrayCursor<UnsignedByteType> c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
	}

	/** Explicit cell version. */
	private void randomizeCellImage(final CellImg<UnsignedByteType, ByteArray, ?> img) {
		final CellCursor< UnsignedByteType, ByteArray, ? > c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
	}

	/** Explicit planar version. */
	private void randomizePlanarImage(final PlanarImg<UnsignedByteType, ByteArray> img) {
		final PlanarCursor<UnsignedByteType> c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
	}

	/** Explicit ImagePlus version. */
	private void randomizeImagePlusImage(final ByteImagePlus<UnsignedByteType> img) {
		final PlanarCursor<UnsignedByteType> c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
	}

	/** Explicit list version. */
	private void randomizeListImage(final ListImg<UnsignedByteType> img) {
		final ListCursor<UnsignedByteType> c = img.cursor();
		while ( c.hasNext() ) {
			final UnsignedByteType t = c.next();
			final int value = t.get();
			final double result = expensiveOperation(value);
			t.set((int) result);
		}
	}

	private double expensiveOperation(final int value) {
		return 255 * Math.random() * Math.sin(value / 255.0);
	}

}
