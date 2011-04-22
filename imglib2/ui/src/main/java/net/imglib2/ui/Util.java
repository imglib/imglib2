/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.imglib2.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.formats.FormatException;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgPlus;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 *
 * @author GBH
 */
public class Util {

	public static <T extends RealType<T> & NativeType<T>> ImgPlus<T> loadImage(String path) {
		try {
			return new ImgOpener().openImg(path);
		} catch (ImgIOException ex) {
			Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IncompatibleTypeException ex) {
			Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

}
