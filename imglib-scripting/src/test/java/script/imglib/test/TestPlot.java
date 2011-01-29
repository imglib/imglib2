package script.imglib.test;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import script.imglib.analysis.BarChart;
import script.imglib.analysis.Histogram;

public class TestPlot {
	static public final void main(String[] args) {
		String src = "http://imagej.nih.gov/ij/images/bridge.gif";
		//String src = "/home/albert/Desktop/t2/bridge.gif";
		Image<UnsignedByteType> img = LOCI.openLOCIUnsignedByteType(src, new ArrayContainerFactory());

		try {
			new BarChart(new Histogram<UnsignedByteType>(img));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}