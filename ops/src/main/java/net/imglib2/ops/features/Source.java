package net.imglib2.ops.features;

/**
 * Sources are not added automatically to the {@link FeatureProcessorBuilder}.
 * {@link Source}s need to be added manually.
 * 
 * @param <A>
 */
public interface Source< A > extends Feature< A >
{
	/**
	 * Update the {@link Source}
	 * 
	 * @param obj
	 */
	void update( A obj );
}
