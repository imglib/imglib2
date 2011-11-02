package net.imglib2.algorithm.fft;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class TestPhaseCorrelation {

	static public final void main(String[] args) {
		
		try {
			
			FloatProcessor b1 = new FloatProcessor(512, 512);
			b1.setValue(255);
			b1.setRoi(new Roi(100, 100, 200, 200));
			b1.fill();
			
			FloatProcessor b2 = new FloatProcessor(512, 512);
			b2.setValue(255);
			b2.setRoi(new Roi(10, 30, 200, 200));
			b2.fill();
			
			// Expected translation: 90, 70
			
			Img<FloatType> img1 = ImageJFunctions.wrap(new ImagePlus("1", b1));
			Img<FloatType> img2 = ImageJFunctions.wrap(new ImagePlus("2", b2));
			
			PhaseCorrelation<FloatType, FloatType> pc = new PhaseCorrelation<FloatType, FloatType>(img1, img2, 10, true);
			
			System.out.println("check input:" + pc.checkInput());
			System.out.println("process: " + pc.process() + " -- time: " + pc.getProcessingTime());
			
			for (PhaseCorrelationPeak peak : pc.getAllShifts()) {
				long[] pos = peak.getPosition();
				StringBuilder sb = new StringBuilder().append(pos[0]);
				for (int i=1; i<pos.length; ++i) sb.append(',').append(pos[i]);
				System.out.println("peak: " + sb.toString());
			}

			System.out.println("Best peak is last.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
