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
 * Created on Oct 11, 2013 by dietzc
 */
package net.imglib2.ops.features.testing;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.roi.EllipseRegionOfInterest;
import net.imglib2.type.numeric.real.FloatType;

/**
 *
 * @author dietzc
 */
public class Main {

    public static void main(final String[] args) {

        // TODO: We should also test against barrys implementation. (measurementtests2)
        // standard test image
        Img<FloatType> testImg = new ArrayImgFactory<FloatType>().create(new long[]{10000, 10000}, new FloatType());
        Cursor<FloatType> cursor = testImg.cursor();
        while (cursor.hasNext()) {
            cursor.fwd();
            float val = (float)Math.random();
            cursor.get().set(val);
        }


        EllipseRegionOfInterest sphere = new EllipseRegionOfInterest(2);
        sphere.setRadius(100);
        sphere.setOrigin(new Point(new long[]{200, 200}));

        // Create test factories
        FeatureFactoryTests<FloatType> newFrameWorkTest = new FeatureFactoryTests<FloatType>();
        ImgLib2Tests<FloatType> test = new ImgLib2Tests<FloatType>();
        // tests

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
         newFrameWorkTest.runFirstOrderTest(sphere.getIterableIntervalOverROI(testImg));
            test.runFirstOrderTest(sphere.getIterableIntervalOverROI(testImg));
        }

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("finished in " + estimatedTime + "ms");

        //Testing against imglib
    }
}
