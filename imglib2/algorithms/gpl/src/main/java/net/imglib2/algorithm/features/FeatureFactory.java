/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   20 May 2010 (hornm): created
 */
package net.imglib2.algorithm.features;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates a specific set of features (double values) for a particular
 * {@link FeatureTarget}. The feature factory itself basically takes care about
 * which features are enabled.
 * 
 * @author hornm, dietzc, University of Konstanz
 * @param <T>
 */
public class FeatureFactory {

	private final Map<Class<?>, List<FeatureTargetUpdater>> m_targetListeners = new HashMap<Class<?>, List<FeatureFactory.FeatureTargetUpdater>>();

	/*
	 * objects to be shared among feature sets
	 */
	private final Map<Class<?>, Object> m_sharedObjects = new HashMap<Class<?>, Object>();

	/*
	 * the feature sets
	 */
	protected final List<FeatureSet> m_featureSetList = new ArrayList<FeatureSet>();

	protected final List<Integer> m_featureSetIdOffset = new ArrayList<Integer>();

	protected final List<String> m_featNames = new ArrayList<String>();

	protected int[] m_featIdxMap;

	/* the enabled features */
	private BitSet m_enabled = null;

	/**
	 * Creates a new feature factory
	 * 
	 * @param enableAll
	 *            if all features of the added feature sets have to be enabled
	 * @param fsets
	 */
	public FeatureFactory(boolean enableAll,
			Collection<? extends FeatureSet> fsets) {
		this(enableAll, fsets.toArray(new FeatureSet[fsets.size()]));
	}

	/**
	 * Creates a new feature factory
	 * 
	 * @param enableAll
	 *            if all features of the added feature sets have to be enabled
	 * @param fsets
	 */
	public FeatureFactory(boolean enableAll, FeatureSet... fsets) {
		int currentOffset = 0;
		for (FeatureSet fset : fsets) {
			// look for FeatureTargetListener annotations and add
			// them to
			// the listener map
			collectFeatureTargetListenersRecursively(fset.getClass(), fset);

			if (fset instanceof SharesObjects) {
				Class<?>[] clazzes = ((SharesObjects) fset)
						.getSharedObjectClasses();

				Object[] instances = new Object[clazzes.length];

				for (int i = 0; i < clazzes.length; i++) {
					Object obj;
					if ((obj = m_sharedObjects.get(clazzes[i])) == null) {
						try {
							obj = clazzes[i].newInstance();
						} catch (Exception e) {
							// LOG.error("Can not create instance of class "
							// + clazzes[i] + ".", e);
						}
						m_sharedObjects.put(clazzes[i], obj);
					}
					instances[i] = obj;
				}
				((SharesObjects) fset).setSharedObjectInstances(instances);

			}

			for (int i = 0; i < fset.numFeatures(); i++) {
				m_featNames.add(fset.name(i));
				m_featureSetList.add(fset);
				m_featureSetIdOffset.add(currentOffset);
			}

			currentOffset += fset.numFeatures();
		}

		if (enableAll) {
			initFeatureFactory(null);
		}
	}

	public void initFeatureFactory(BitSet enabledFeatures) {
		if (m_enabled != null) {
			throw new IllegalStateException(
					"Feature factory was already initialized!");
		}

		if (enabledFeatures == null) {
			m_enabled = new BitSet();
			m_enabled.set(0, m_featNames.size());
		} else {
			m_enabled = enabledFeatures.get(0, m_featNames.size());
		}
		m_featIdxMap = new int[m_enabled.cardinality()];
		int featIdx = 0;
		for (int i = 0; i < m_featNames.size(); i++) {
			if (m_enabled.get(i)) {
				m_featIdxMap[featIdx] = i;
				featIdx++;
				m_featureSetList.get(i).enable(i - m_featureSetIdOffset.get(i));
			}

		}
	}

	/**
	 * Looks for FeatureTargetListener annotations and adds them to the listener
	 * map
	 */
	protected void collectFeatureTargetListenersRecursively(Class<?> clazz,
			Object listener) {
		if (clazz == null) {
			return;
		}

		Method[] methods = clazz.getMethods();

		// LOG.debug("Looking for FeatureTargetListener annotations for class "
		// + clazz + ", methods:" + Arrays.toString(methods));

		for (Method method : methods) {

			FeatureTargetListener targetAnnotation = method
					.getAnnotation(FeatureTargetListener.class);
			if (targetAnnotation != null) {

				Class<?>[] types = method.getParameterTypes();
				if (types.length != 1) {
					// LOG.error("Only methods with exactly one parameter are allowed as feature target listener. Method '"
					// + method + "' skipped.");
				} else {

					// LOG.debug("Found FeatureTargetListener: "
					// + targetAnnotation + "  on method '" + method + "'");
					List<FeatureTargetUpdater> listeners;
					if ((listeners = m_targetListeners.get(types[0])) == null) {
						listeners = new ArrayList<FeatureFactory.FeatureTargetUpdater>();
						m_targetListeners.put(types[0], listeners);
					}
					listeners.add(new FeatureTargetUpdater(listener, method));
				}

			}
		}

		collectFeatureTargetListenersRecursively(clazz.getSuperclass(),
				listener);
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> interfaze : interfaces) {
			collectFeatureTargetListenersRecursively(interfaze, listener);
		}
	}

	/**
	 * Updates a feature target.
	 * 
	 * @param target
	 * @param obj
	 */
	public void updateFeatureTarget(Object obj) {
		updateFeatureTargetRecursively(obj, obj.getClass());
	}

	private void updateFeatureTargetRecursively(Object obj, Class<?> clazz) {
		if (clazz == null) {
			return;
		}
		List<FeatureTargetUpdater> ftus = m_targetListeners.get(clazz);
		if (ftus != null) {
			for (FeatureTargetUpdater ftu : ftus)
				ftu.updateFeatureTarget(obj);
			return;
		} else {
			// LOG.debug("No listener registert for feature target "
			// + obj.getClass().getSimpleName() + "!");
			updateFeatureTargetRecursively(obj, clazz.getSuperclass());
			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> interfaze : interfaces) {
				updateFeatureTargetRecursively(obj, interfaze);
			}

		}
	}

	/**
	 * 
	 * @param featID
	 * @return the feature for the given feature ID, the feature id is assigned
	 *         according to the order the feature sets were added
	 */
	public double getFeatureValue(final int featID) {
		return m_featureSetList.get(m_featIdxMap[featID]).value(
				m_featIdxMap[featID]
						- m_featureSetIdOffset.get(m_featIdxMap[featID]));
	}

	// /**
	// * Returns the enabled feature values for the given feature set class
	// *
	// * @return the feature value
	// */
	// public double[] getFeatureValues(Class<? extends FeatureSet> clazz) {
	// FeatureSet fset = m_featureSetMap.get(clazz);
	// double[] res = new double[fset.numFeatures()];
	// for (int i = 0; i < res.length; i++) {
	// res[i] = fset.value(i);
	// }
	// return res;
	// }

	/**
	 * @return the feature values of all enabled features
	 */
	public double[] getFeatureValues() {
		return getFeatureValues(new double[getNumFeatures()]);
	}

	/**
	 * @return the feature values of all enabled features
	 */
	public double[] getFeatureValues(double[] vec) {
		int i = 0;
		for (int feat = m_enabled.nextSetBit(0); feat >= 0; feat = m_enabled
				.nextSetBit(feat + 1)) {
			vec[i++] = m_featureSetList.get(feat).value(
					feat - m_featureSetIdOffset.get(feat));
		}
		return vec;
	}

	/**
	 * The total number of enabled features.
	 * 
	 * @return num enabled features
	 */
	public int getNumFeatures() {
		isInitialized();
		return m_enabled.cardinality();
	}

	/**
	 * 
	 * 
	 * @return the names of the enabled features.
	 */
	public String[] getFeatureNames() {
		isInitialized();
		String[] res = new String[getNumFeatures()];

		int i = 0;
		for (int feat = m_enabled.nextSetBit(0); feat >= 0; feat = m_enabled
				.nextSetBit(feat + 1)) {
			res[i++] = m_featNames.get(feat);
		}
		return res;
	}

	// /**
	// * The enabled features. A copy of the BitSet is made.
	// *
	// * To iteratively set and retrieve the according feature values, e.g.
	// * use:
	// *
	// *
	// * <code>
	// * BitSet enabled = fac.getEnabledFeatures();
	// * for (int featID = enabled.nextSetBit(0); featID >= 0; featID =
	// enabled
	// * .nextSetBit(featID + 1)) {
	// * fac.setFeatureID(feadID);
	// * fac.getFeatureValue();
	// * }</code>
	// *
	// *
	// * @return encoded in a bit set (1 is enabled). It returns a copy.
	// */
	// public BitSet getEnabledFeatures() {
	// if (m_enabled == null) {
	// m_enabled = new BitSet(getNumTotalFeatures());
	// m_enabled.set(0, getNumTotalFeatures());
	// }
	// return (BitSet) m_enabled.clone();
	// }

	/**
	 * @param obj
	 * @return
	 */
	public boolean isFeatureTargetRequired(Object obj) {
		return m_targetListeners.get(obj.getClass()) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "";

	}

	private void isInitialized() {
		if (m_enabled == null) {
			throw new IllegalStateException(
					"Feature factory not initialized, yet!");
		}
	}

	protected static class FeatureTargetUpdater {

		private final Method m_method;

		private final Object m_listener;

		public FeatureTargetUpdater(Object listener, Method method) {
			m_method = method;
			m_listener = listener;
		}

		public void updateFeatureTarget(Object target) {
			try {
				m_method.invoke(m_listener, target);
			} catch (Exception e) {
				throw new RuntimeException(
						"InvocationTargetException when invoking annotated method from FeatureTarget Update. Data: "

								+ target.toString()
								+ ", subscriber:"
								+ m_listener, e);
			}
		}

	}

}
