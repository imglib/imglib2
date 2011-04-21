/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.imglib2.ui;

import java.io.IOException;
import loci.formats.FormatException;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.io.ImgOpener;
import net.imglib2.io.ImgPlus;
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
		}
		catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}
		catch (FormatException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
		
}
