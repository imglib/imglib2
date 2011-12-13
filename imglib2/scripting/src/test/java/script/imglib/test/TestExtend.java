package script.imglib.test;

import ij.IJ;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.view.Extend;
import net.imglib2.script.view.ExtendMirrorDouble;
import net.imglib2.script.view.ExtendMirrorSingle;
import net.imglib2.script.view.ExtendPeriodic;
import net.imglib2.script.view.ROI;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class TestExtend
{
	public static void main(String[] args) {
		
		Img<UnsignedByteType> img = ImgLib.wrap(IJ.openImage("/home/albert/Desktop/t2/bridge.gif"));
		
		Img<UnsignedByteType> multiPeriodic =
			new ROI<UnsignedByteType>(
				new ExtendPeriodic<UnsignedByteType>(img),
				new long[]{-512, -512},
				new long[]{1024, 1024});
		
		Img<UnsignedByteType> multiMirroredDouble =
			new ROI<UnsignedByteType>(
				new ExtendMirrorDouble<UnsignedByteType>(img),
				new long[]{-512, -512},
				new long[]{1024, 1024});
		
		Img<UnsignedByteType> multiMirroredSingle =
			new ROI<UnsignedByteType>(
				new ExtendMirrorSingle<UnsignedByteType>(img),
				new long[]{-512, -512},
				new long[]{1024, 1024});
		Img<UnsignedByteType> centered =
			new ROI<UnsignedByteType>(
					new Extend<UnsignedByteType>(img, 0),
					new long[]{-512, -512},
					new long[]{1024, 1024});
		
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
