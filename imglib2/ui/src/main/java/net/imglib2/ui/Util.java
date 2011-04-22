package net.imglib2.ui;

import loci.common.StatusEvent;
import loci.common.StatusListener;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgPlus;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgIOUtils;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 *
 * @author GBH
 */
public class Util {

		public  static <T extends RealType<T> & NativeType<T>> ImgPlus<T> loadImage(final String url) {
		try {
			System.out.println("Downloading " + url);
			final String id = ImgIOUtils.cacheId(url);
			System.out.println("Opening " + id);
			final ImgOpener imgOpener = new ImgOpener();
			imgOpener.addStatusListener(new StatusListener() {

				@Override
				public void statusUpdated(StatusEvent e) {
					System.out.println(e.getStatusMessage());
				}

			});
			return imgOpener.openImg(id);
		} catch (final IncompatibleTypeException e) {
			e.printStackTrace();
		} catch (final ImgIOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
