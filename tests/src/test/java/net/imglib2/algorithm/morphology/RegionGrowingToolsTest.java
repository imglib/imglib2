package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import net.imglib2.RandomAccess;
import net.imglib2.algorithm.regiongrowing.RegionGrowingTools;
import net.imglib2.algorithm.regiongrowing.RegionGrowingTools.GrowingMode;
import net.imglib2.algorithm.regiongrowing.ThresholdRegionGrowing;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class RegionGrowingToolsTest {

	public static void main(final String[] args) throws ImgIOException, IncompatibleTypeException {

		final String fn = "/Users/tinevez/Desktop/Data/brightblobs.tif";
		//		final String fn = "/Users/JeanYves/Desktop/Data/brightblobs.tif";
		@SuppressWarnings( "unchecked" )
		final Img<UnsignedByteType> img = new ImgOpener().openImg(fn);
		final long[][] seeds = new long[][] { { 105, 110 }, { 102, 73 } };

		final Map<long[], Integer> seedLabels = new HashMap<long[], Integer>(seeds.length);
		for (int i = 0; i < seeds.length; i++) {
			seedLabels.put(seeds[i], Integer.valueOf(i + 1));
		}

		final long[][] strel = RegionGrowingTools.get4ConStructuringElement(img.numDimensions());
		final GrowingMode mode = GrowingMode.SEQUENTIAL;
		final UnsignedByteType threshold = new UnsignedByteType(120);

		final long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);
		final Img<UnsignedByteType> target = ArrayImgs.unsignedBytes(dims);
		final RandomAccess<UnsignedByteType> ra = target.randomAccess();

		NativeImgLabeling<Integer, IntType> labeling;
		{
			final Img<IntType> backup = target.factory().imgFactory(new IntType()).create(img, new IntType());
			labeling = new NativeImgLabeling<Integer, IntType>(backup);
		}
		final ThresholdRegionGrowing<UnsignedByteType, Integer> algo = new ThresholdRegionGrowing<UnsignedByteType, Integer>(img.randomAccess(), threshold, seedLabels, mode, strel, labeling) {

			private int stepIndex = 1;

			@Override
			public void finishedGrowStep(final Queue<long[]> childPixels, final Integer label) {
				for (final long[] pixel : childPixels) {
					ra.setPosition(pixel);
					ra.get().set(stepIndex);
				}
				if (stepIndex++ > 255) {
					stepIndex = 1;
				}
				super.finishedGrowStep(childPixels, label);
			}
		};

		if (!algo.checkInput() || !algo.process()) {
			System.out.println(algo.getErrorMessage());
		} else {}

		ImageJ.main(args);
		ImageJFunctions.show(img);
		final NativeImgLabeling<Integer, IntType> out = algo.getResult();
		ImageJFunctions.show(out.getStorageImg());
		ImageJFunctions.show(target);
	}
}
