package net.imglib2.ops.features;

import java.util.Iterator;

/**
 * 
 * @author <a href="mailto:dietzc85@googlemail.com">Christian Dietz</a>
 * @author <a href="mailto:horn_martin@gmx.de">Martin Horn</a>
 * @author <a href="mailto:michael.zinsmaier@googlemail.com">Michael
 *         Zinsmaier</a>
 */
public interface FeatureSet< T, V >
{

	/**
	 * @return the number of features
	 */
	int numFeatures();

	/**
	 * @param feature
	 * 
	 */
	void register( Feature< V > feature );

	/**
	 * Returns a unique identifier of this feature set. If the feature set is
	 * initialized with parameters, it should be included in the returned id.
	 * 
	 * @return a unique id
	 */
	String featureSetName();

	/**
	 * Retrieve iterator over active features (which were directly added, not
	 * indirectly via dependencies). Features are calculated before hands so if
	 * you call .get() on a feature twice without any update, this should be
	 * cached.
	 * 
	 * @param objectOfInterest
	 * @return Result Cursor
	 */
	Iterator< Feature< V >> iterator( T objectOfInterest );
}
