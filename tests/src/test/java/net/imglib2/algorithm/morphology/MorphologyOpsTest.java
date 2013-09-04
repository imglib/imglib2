package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import net.imglib2.algorithm.region.localneighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class MorphologyOpsTest {

	public static void main(final String[] args) {
		final ArrayImg<UnsignedByteType, ByteArray> img = ArrayImgs.unsignedBytes(new long[] { 3, 9 });
		final ArrayRandomAccess<UnsignedByteType> ra = img.randomAccess();
		ra.setPosition(new int[] { 1, 4 });
		ra.get().set(255);

		final Shape strel = new CenteredRectangleShape(new int[] { 5, 2 }, true);
		final Img<UnsignedByteType> full = MorphologicalOperations.dilateFull(img, strel, 1);

		ImageJ.main(args);
		ImageJFunctions.show(img);
		ImageJFunctions.show(full);

	}
}
