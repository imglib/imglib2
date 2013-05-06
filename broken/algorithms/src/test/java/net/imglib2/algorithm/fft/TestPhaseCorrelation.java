/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.algorithm.fft;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
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
