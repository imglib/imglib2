package net.imglib2.ops.features;

/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
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
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * Created on Oct 7, 2013 by graumanna
 */

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.providers.IterableIntervalProvider;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.DoubleType;

/**
 *
 * @author dietzc
 * @param <T> Type must be the most generic type which is supported by a updatehandler
 */
public class TestFeatureSet<T extends Type<T>> implements FeatureSet<IterableInterval<T>, DoubleType> {

    // all public features
    private final HashMap<String, Feature<DoubleType>> m_publicFeatures;

    // all features including dependencies which are hidden
    private final HashMap<String, Feature<?>> m_requiredFeatures;

    // the actual updater (can be multithreaded etc)
    private final IterableIntervalProvider<T> m_updater;

    /**
     *
     */
    public TestFeatureSet() {
        m_requiredFeatures = new HashMap<String, Feature<?>>();
        m_publicFeatures = new HashMap<String, Feature<DoubleType>>();
        registerHidden(m_updater = new IterableIntervalProvider<T>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int numFeatures() {
        return m_publicFeatures.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(final Feature<DoubleType> feature) {
        registerHidden(feature);

        if (!m_publicFeatures.containsKey(feature.getClass().getCanonicalName())) {
            m_publicFeatures.put(feature.getClass().getCanonicalName(),
                                 (Feature<DoubleType>)m_requiredFeatures.get(feature.getClass().getCanonicalName()));
        }
    }

    public void registerHidden(final Feature<?> feature) {
        if (!m_requiredFeatures.containsKey(feature.getClass().getCanonicalName())) {
            parse(feature);
        }
    }

    private void parse(final Feature<?> feature) {
        if (!m_requiredFeatures.containsKey(feature)) {

            for (Field f : feature.getClass().getDeclaredFields()) {

                if (f.isAnnotationPresent(RequiredFeature.class)) {
                    try {
                        Class<? extends Feature<?>> fieldType = (Class<? extends Feature<?>>)f.getType();

                        if (!Feature.class.isAssignableFrom(fieldType)) {
                            throw new IllegalArgumentException(
                                    "Only Features can be annotated with @RequiredInput since now");
                        }

                        //TODO dirty but works for now
                        AccessibleObject.setAccessible(new AccessibleObject[]{f}, true);
                        Feature<?> storedFeature = m_requiredFeatures.get(fieldType.getCanonicalName());

                        if (storedFeature == null) {
                            if (Source.class.isAssignableFrom(fieldType)) {
                                throw new IllegalArgumentException(
                                        "Sources and Parametrized Features can't be automatically added. Add them manually to the feature set.");
                            }
                            Feature<?> newFeature = fieldType.newInstance();
                            parse(newFeature);
                            m_requiredFeatures.put(fieldType.getCanonicalName(), newFeature);
                            f.set(feature, newFeature);
                        } else {
                            f.set(feature, storedFeature);
                        }

                        // TODO UGLY make sure that access manager is closed again
                        AccessibleObject.setAccessible(new AccessibleObject[]{f}, false);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        throw new IllegalStateException(
                                "Couldn't instantiate a class. Please not that parametrized features have to be added manually.");
                    }
                }
            }// end loop over dependencies

            // Cycle detected
            if (m_requiredFeatures.containsKey(feature)) {
                throw new IllegalStateException("cycle detected");
            }

            m_requiredFeatures.put(feature.getClass().getCanonicalName(), feature);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String featureSetName() {
        return "MyFeatureSet";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Feature<DoubleType>> iterator(final IterableInterval<T> objectOfInterest) {

        m_updater.update(objectOfInterest);

        // Notify the features that something happened
        for (Feature<?> f : m_requiredFeatures.values()) {
            f.update();
        }

        return m_publicFeatures.values().iterator();
    }
}
