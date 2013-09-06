package net.imglib2.algorithm.morphology;

import ij.ImageJ;

import java.util.HashMap;
import java.util.Map;

import net.imglib2.algorithm.regiongrowing.AbstractRegionGrowingAlgorithm;
import net.imglib2.algorithm.regiongrowing.AbstractRegionGrowingAlgorithm.GrowingMode;
import net.imglib2.algorithm.regiongrowing.ThresholdRegionGrowing;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class RegionGrowingToolsTest {

	public static void main(final String[] args) throws ImgIOException, IncompatibleTypeException {

		final String fn = "/Users/tinevez/Desktop/Data/brightblobs.tif";
		final Img<UnsignedByteType> img = new ImgOpener().openImg(fn);
		final long[][] seeds = new long[][] { { 105, 110 }, { 102, 73 } };

		final Map<long[], Integer> seedLabels = new HashMap<long[], Integer>(seeds.length);
		for (int i = 0; i < seeds.length; i++) {
			seedLabels.put(seeds[i], Integer.valueOf(i + 1));
		}

		final long[][] strel = AbstractRegionGrowingAlgorithm.get8ConStructuringElement(img.numDimensions());
		final GrowingMode mode = GrowingMode.ASYNCHRONOUS;
		final UnsignedByteType threshold = new UnsignedByteType(120);
		final ThresholdRegionGrowing<UnsignedByteType> algo = new ThresholdRegionGrowing<UnsignedByteType>(img, threshold, seedLabels, mode, strel);

		if (!algo.checkInput() || !algo.process()) {
			System.out.println(algo.getErrorMessage());
		} else {
			System.out.println("Processed in " + algo.getProcessingTime());
		}

		ImageJ.main(args);
		ImageJFunctions.show(img);
		final NativeImgLabeling<Integer, IntType> out = (NativeImgLabeling<Integer, IntType>) algo.getResult();
		ImageJFunctions.show(out.getStorageImg());
	}
}
