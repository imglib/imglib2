package net.imglib2.ops.features;

import java.util.Iterator;

public interface FeatureSet< T, V >
{

	/**
	 * @return the number of features
	 */
	int numFeatures();

	/**
	 * Returns a unique identifier of this feature set. If the feature set is
	 * initialized with parameters, it should be included in the returned id.
	 * 
	 * @return a unique id
	 */
	String featureSetName();

}
