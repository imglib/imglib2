package script.imglib.test;

import java.io.File;

import ij.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.slice.SliceXY;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.real.FloatType;

public class Slices {

	static public final void main(String[] args) {
		
		new ImageJ();
		
		try {
			String src = "http://imagej.nih.gov/ij/images/bat-cochlea-volume.zip";
			src = "/home/albert/Desktop/t2/bat-cochlea-volume.tif";
			
			System.out.println("exists:" + new File(src).exists());
				
			Img<ByteType> img = ImgLib.open(src);

			Img<FloatType> r = new SliceXY<ByteType>(img, 23).asImage();

			ImgLib.wrap(r).show();
			ImgLib.wrap(img).show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
