

import ij.IJ;
import ij.ImagePlus;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import mpicbg.imglib.algorithm.pde.AnisotropicDiffusion;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.type.numeric.RealType;

public class AnistotropicDiffusion_TestDrive {

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws InterruptedException, URISyntaxException, MalformedURLException {
	
		ij.ImageJ.main(args);
		
		ImagePlus imp = IJ.openImage("http://rsb.info.nih.gov/ij/images/boats.gif");
		
		Image<? extends RealType> source = ImageJFunctions.wrap(imp);
		
		AnisotropicDiffusion<?> algo = new AnisotropicDiffusion(source, 2, 10);
//		AnisotropicDiffusion<?> algo = new AnisotropicDiffusion(source, 1, new AnisotropicDiffusion.WideRegionEnhancer(20));
		algo.setNumThreads();
		
		if (!algo.checkInput()) {
			System.out.println("Check input failed! With: "+algo.getErrorMessage());
			return;
		}
		
		imp.show();

		int niter = 10;
		algo.setDimensions(new int[] { 1 } );
		for (int i = 0; i < niter; i++) {
			System.out.println("Iteration "+(i+1)+" of "+niter+".");
			algo.process();
			imp.updateAndDraw();
			Thread.sleep(500);
		}
		
		System.out.println("Done in "+algo.getProcessingTime()+" ms.");

		

	}
}
