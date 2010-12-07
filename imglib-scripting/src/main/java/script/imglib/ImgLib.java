package script.imglib;

import ij.IJ;
import ij.ImagePlus;

import ij.io.FileSaver;

import java.io.IOException;

import loci.formats.FormatException;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImagePlusAdapter;

import mpicbg.imglib.image.display.imagej.ImageJFunctions;

import mpicbg.imglib.io.ImageOpener;

import mpicbg.imglib.type.numeric.RealType;

/* TODO license? */

/**
 * A simple wrapper class that is supposed to contain only functions for scripting.
 *
 * To make things very scriptable, the only exception thrown is a RuntimeException, and
 * the corresponding stack traces are output to stderr.
 * 
 * @author Johannes Schindelin
 * @version 1.0 2010-12-07
 * @see Image
 */
public class ImgLib {
	public static<T extends RealType<T>> Image open(String pathOrURL) {
		try {
			return new ImageOpener().<T>openImage(pathOrURL);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Got I/O exception: " + e);
		}
		catch (FormatException e) {
			e.printStackTrace();
			throw new RuntimeException("Got format exception: " + e);
		}
	}

	public static<T extends RealType<T>> Image wrap(ImagePlus imp) {
		return ImagePlusAdapter.<T>wrap(imp);
	}

	// this infers the type from the extension
	public static<T extends RealType<T>> void save(Image<T> image, String path) {
		int dot = path.lastIndexOf('.');
		if (dot < 0 || path.length() - dot - 1 > 4)
			throw new RuntimeException("Could not infer file type from filename: " + path);
		save(image, path.substring(dot + 1), path);
	}

	// TODO: use LOCI for this
	public static<T extends RealType<T>> void save(Image<T> image, String fileType, String path) {
		ImagePlus imp = ImageJFunctions.displayAsVirtualStack(image);
		FileSaver saver = new FileSaver(imp);
		fileType = fileType.toLowerCase();
		if (fileType.equals("tif") || fileType.equals("tiff"))
			saver.saveAsTiff(path);
		else if (fileType.equals("zip"))
			saver.saveAsZip(path);
		else if (fileType.equals("gif"))
			saver.saveAsGif(path);
		else if (fileType.equals("jpg") || fileType.equals("jpeg"))
			saver.saveAsJpeg(path);
		else if (fileType.equals("bmp"))
			saver.saveAsBmp(path);
		else if (fileType.equals("pgm"))
			saver.saveAsPgm(path);
		else if (fileType.equals("png"))
			saver.saveAsPng(path);
		else if (fileType.equals("raw"))
			saver.saveAsRaw(path);
		else
			throw new RuntimeException("Unknown fileformat: " + fileType);
	}
}
