package net.imglib2.script.algorithm.integral.features;

public interface IHFeature<T>
{
	public double get(double min, double max, long[] bins, double[] binValues, long nPixels);
	
	public void get(double min, double max, long[] histogram, double[] binValues, long nPixels, T output);
	
	public double get(double min, double max, long[] bins, double[] binValues, long nPixels, double other);
	
	public void get(double min, double max, long[] histogram, double[] binValues, long nPixels, double other, T output);
}
