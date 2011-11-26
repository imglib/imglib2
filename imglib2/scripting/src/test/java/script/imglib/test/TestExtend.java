package script.imglib.test;

import ij.IJ;
import ij.ImageJ;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.view.Extend;
import net.imglib2.script.view.ExtendMirrorDouble;
import net.imglib2.script.view.ExtendMirrorSingle;
import net.imglib2.script.view.ExtendPeriodic;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class TestExtend
{
	public static void main(String[] args) {
		
		Img<UnsignedByteType> img = ImgLib.wrap(IJ.openImage("/home/albert/Desktop/t2/bridge.gif"));
		
		Img<UnsignedByteType> multiPeriodic = new ExtendPeriodic<UnsignedByteType>(img, new long[]{2048,2048});
		Img<UnsignedByteType> multiMirroredDouble = new ExtendMirrorDouble<UnsignedByteType>(img, new long[]{2048, 2048});
		Img<UnsignedByteType> multiMirroredSingle = new ExtendMirrorSingle<UnsignedByteType>(img, new long[]{2048, 2048});
		Img<UnsignedByteType> centered = new Extend<UnsignedByteType>(img, new long[]{-768, -768}, new long[]{2048, 2048}, 0);
		
		// Above, notice the negative offsets. This needs fixing, it's likely due to recent change
		// in how offsets are used. TODO
		
		try {
			ImgLib.show(multiPeriodic, "periodic");
			ImgLib.show(multiMirroredDouble, "mirror double edge");
			ImgLib.show(multiMirroredSingle, "mirror single edge");
			ImgLib.show(centered, "translated 0 background");
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}
}
