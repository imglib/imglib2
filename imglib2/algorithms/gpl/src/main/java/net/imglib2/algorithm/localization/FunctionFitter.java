package net.imglib2.algorithm.localization;

public interface FunctionFitter {
	
	/**
	 * Minimizes <code>E = sum {(y[k] - f(x[k],a)) }^2</code>.
	 * Note that function implements the value and gradient of f(x,a),
	 * NOT the value and gradient of E with respect to a!
	 * 
	 * @param x array of domain points, each may be multidimensional. 
	 * (For instance, for 2D data, provides a double array of N double arrays 
	 * of 2 elements: x & y.)
	 * @param y corresponding array of values.
	 * @param a the parameters/state of the model. Is updated by the call. 
	 * @param f  the function to fit on the domain points.
	 * @throws Exception  if the fitting process meets a numerical problem.
	 */
	public void fit(double[][] x, double[] y, double[] a, FitFunction f) throws Exception;

}
