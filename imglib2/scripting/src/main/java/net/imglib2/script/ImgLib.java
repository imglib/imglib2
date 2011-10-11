package net.imglib2.script;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;

import net.imglib2.exception.ImgLibException;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.io.img.virtual.VirtualImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/* TODO license? */

/**
 * A simple wrapper class that is supposed to contain only functions for scripting.
 * 
 * @author Johannes Schindelin and Albert Cardona
 * @version 1.0 2010-12-07
 * @see Img
 */
public class ImgLib {
	/** Open an image from a file path or a web URL. 
	 * @throws IncompatibleTypeException 
	 * @throws ImgIOException */
	public static<T extends RealType<T> & NativeType<T>> Img<T> open(String pathOrURL) throws ImgIOException, IncompatibleTypeException {
		return new ImgOpener().openImg(pathOrURL);
		// Old:
		//return wrap(IJ.openImage(pathOrURL));
	}

	public static<T extends RealType<T> & NativeType<T>> Img<T> openVirtual(String pathOrUrl) throws ImgIOException, IncompatibleTypeException {
		return new ImgOpener().openImg(pathOrUrl, new VirtualImgFactory<T>());
	}

	/** Wrap an ImageJ's {@link ImagePlus} as an Imglib {@link Image} of the appropriate type.
	 * The data is not copied, but merely accessed with a PlanarArrayContainer.
	 * @see ImagePlusAdapter */
	public static<T extends RealType<T>> Img<T> wrap(ImagePlus imp) {
		return ImagePlusAdapter.<T>wrap(imp);
	}

	/** Wrap an Imglib's {@link Image} as an ImageJ's {@link ImagePlus} of the appropriate type.
	 * The data is not copied, but accessed with a special-purpose VirtualStack subclass. 
	 * @throws ImgLibException */
	static public final <T extends RealType<T> & NativeType<T>> ImagePlus wrap(final Img<T> img) throws ImgLibException {
		return new ImagePlusImgFactory<T>().create(img, img.firstElement().createVariable()).getImagePlus();
	}

	/** Save an image in the appropriate file format according to
	 * the filename extension specified in {@param path}. 
	 * @throws ImgLibException */
	public static final <T extends RealType<T> & NativeType<T>> boolean save(Img<T> image, String path) throws ImgLibException {
		int dot = path.lastIndexOf('.');
		if (dot < 0 || path.length() - dot - 1 > 4)
			throw new RuntimeException("Could not infer file type from filename: " + path);
		return save(image, path.substring(dot + 1), path);
	}

	/** Save an image in the format specified by {@param fileType}, which can be any of:
	 *  "tif", "tiff", "zip", "gif", "jpg", "jpeg", "bmp", "pgm", "png", "raw".
	 *  
	 *  When saving as TIFF, if the image has more than 2 dimensions, it will be saved
	 *  as a stack. 
	 * @throws ImgLibException */
	public static<T extends RealType<T> & NativeType<T>> boolean save(Img<T> image, String fileType, String path) throws ImgLibException {
		// TODO: use LOCI for this
		ImagePlus imp = wrap(image);
		FileSaver saver = new FileSaver(imp);
		fileType = fileType.toLowerCase();
		if (fileType.equals("tif") || fileType.equals("tiff")) {
			if (image.numDimensions() > 2) {
				return saver.saveAsTiffStack(path);
			} else {
				return saver.saveAsTiff(path);
			}
		} else if (fileType.equals("zip"))
			return saver.saveAsZip(path);
		else if (fileType.equals("gif"))
			return saver.saveAsGif(path);
		else if (fileType.equals("jpg") || fileType.equals("jpeg"))
			return saver.saveAsJpeg(path);
		else if (fileType.equals("bmp"))
			return saver.saveAsBmp(path);
		else if (fileType.equals("pgm"))
			return saver.saveAsPgm(path);
		else if (fileType.equals("png"))
			return saver.saveAsPng(path);
		else if (fileType.equals("raw"))
			return saver.saveAsRaw(path);
		else
			throw new RuntimeException("Unknown fileformat: " + fileType);
	}
}
