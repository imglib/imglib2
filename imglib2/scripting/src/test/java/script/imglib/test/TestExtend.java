package script.imglib.test;

import ij.IJ;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.view.ExtendMirroringDouble;
import net.imglib2.script.view.ExtendMirroringSingle;
import net.imglib2.script.view.ExtendPeriodic;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class TestExtend
{
	public static void main(String[] args) {
		
		Img<UnsignedByteType> img = ImgLib.wrap(IJ.openImage("/home/albert/Desktop/t2/bridge.gif"));
		
		Img<UnsignedByteType> multiPeriodic = new ExtendPeriodic<UnsignedByteType>(img, new long[]{2048,2048});
		Img<UnsignedByteType> multiMirroredDouble = new ExtendMirroringDouble<UnsignedByteType>(img, new long[]{2048, 2048});
		Img<UnsignedByteType> multiMirroredSingle = new ExtendMirroringSingle<UnsignedByteType>(img, new long[]{2048, 2048});
		
		try {
			ImgLib.show(multiPeriodic, "periodic");
			ImgLib.show(multiMirroredDouble, "mirror double edge");
			ImgLib.show(multiMirroredSingle, "mirror single edge");
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}

}
