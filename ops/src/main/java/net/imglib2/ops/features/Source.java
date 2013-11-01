package net.imglib2.ops.features;

/**
 * Sources are not added automatically to the {@link FeatureProcessor}.
 * {@link Source}s need to be added manually.
 * 
 * @param <A>
 */
public interface Source< A >
{
	/**
	 * Update the {@link Source}
	 * 
	 * @param obj
	 */
	void update( A obj );
}
