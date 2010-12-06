package script.imglib.io;

import java.io.IOException;

import ij.ImagePlus;
import ij.io.FileSaver;
//import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
//import loci.plugins.util.BFVirtualStack;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.ImageOpener;
import mpicbg.imglib.type.numeric.RealType;

/** Script I/O utility methods for imglib scripting.
 * 
 * The functions here all relate to wrapping imglib images as ImagePlus,
 * or the opposite, or opening/saving images from/to disk.
 * 
 * @author Albert Cardona
 */
public class SIO
{
	/** Wrap an ImageJ's {@link ImagePlus} as an Imglib {@link Image}. */
	static public final <R extends RealType<R>> Image<R> wrap(final ImagePlus imp) {
		return ImageJFunctions.wrap(imp);
	}

	/** Wrap an Imglib's {@link Image} as an ImageJ's {@link ImagePlus}. */
	static public final ImagePlus wrap(final Image<?> img) {
		return ImageJFunctions.displayAsVirtualStack(img);
	}

	/** Open an image from disk as an Imglib's {@link Image}. */
	static public final <R extends RealType<R>> Image<R> open(final String filepath) throws FormatException, IOException {
		return new ImageOpener().openImage(filepath);
	}

	/** //The PlanarContainer grabs the native array, so it's not virtual anymore.
	static public final <R extends RealType<R>> Image<R> openVirtual(final String filepath) throws FormatException, IOException {
		ChannelSeparator r = new ChannelSeparator();
		r.setId(filepath);
		BFVirtualStack bfv = new BFVirtualStack(filepath, r, false, false, false);
		return SIO.wrap(new ImagePlus(filepath, bfv));
	}
	*/

	static public final boolean saveAsTiff(final Image<?> img, final String filepath) {
		// TODO: replace with direct saving via LOCI library
		ImagePlus imp = SIO.wrap(img);
		if (imp.getNSlices() > 1)
			return new FileSaver(imp).saveAsTiffStack(filepath);
		return new FileSaver(imp).saveAsTiff(filepath);
	}

	static public final boolean saveAsPng(final Image<?> img, final String filepath) {
		// TODO: replace with direct saving via LOCI library
		ImagePlus imp = SIO.wrap(img);
		if (imp.getNSlices() > 1)
			System.out.println("WARNING: saving only the current slice as PNG!");
		return new FileSaver(imp).saveAsPng(filepath);
	}

	static public final boolean saveAsJpeg(final Image<?> img, final String filepath) {
		// TODO: replace with direct saving via LOCI library
		ImagePlus imp = SIO.wrap(img);
		if (imp.getNSlices() > 1)
			System.out.println("WARNING: saving only the current slice as JPEG!");
		return new FileSaver(imp).saveAsJpeg(filepath);
	}
}