package net.imglib2.algorithm.localization;


/**
 * A dummy {@link FunctionFitter} that simply skips the fitting process and leaves
 * the starting estimate untouched. 
 * <p>
 * Use this when you want to rely solely on 
 * {@link StartPointEstimator} results and skip the extra curve fitting step. 
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> - 2013
 */
public class DummySolver implements FunctionFitter {

	@Override
	public void fit(double[][] x, double[] y, double[] a, FitFunction f) throws Exception {
		return;
	}
	
	@Override
	public String toString() {
		return "Dummy curve fitting algorithm";
	}

}
