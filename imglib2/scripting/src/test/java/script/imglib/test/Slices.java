package script.imglib.test;

import ij.ImageJ;

import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.slice.SliceXY;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class Slices {

	static public final void main(String[] args) {
		
		new ImageJ();
		
		try {
			String src = "http://imagej.nih.gov/ij/images/bat-cochlea-volume.zip";
			//String src = "/home/albert/Desktop/t2/bat-cochlea-volume.tif";	
			Img<UnsignedByteType> img = ImgLib.open(src);
			
			Img<UnsignedByteType> s = new SliceXY<UnsignedByteType>(img, 23);

			ImgLib.wrap(s, "23").show();
			ImgLib.wrap(img, "bat").show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
