/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package tests.labeling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mpicbg.imglib.algorithm.labeling.AllConnectedComponents;
import mpicbg.imglib.algorithm.labeling.Watershed;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.labeling.Labeling;
import mpicbg.imglib.labeling.LabelingType;
import mpicbg.imglib.type.numeric.integer.IntType;

import org.junit.Test;

/**
 * TODO
 *
 * @author Lee Kamentsky
 */
public class WatershedTest {
	private void testSeededCase2D(int [][] image,
			              	int [][] seeds,
			              	int [][] expected,
			              	int [][] structuringElement,
			              	int background) {
		int [] imageDimensions = new int [] { image.length, image[0].length };
		int [] seedDimensions = new int [] { seeds.length, seeds[0].length };
		int [] outputDimensions = new int [] { expected.length, expected[0].length };
		DirectAccessContainerFactory containerFactory = new ArrayContainerFactory();
		ImageFactory<IntType> intFactory = new ImageFactory<IntType>(new IntType(), containerFactory);
		Image<IntType> imageImage = intFactory.createImage(imageDimensions);
		ImageFactory<LabelingType<Integer>> labelingFactory = new ImageFactory<LabelingType<Integer>>(new LabelingType<Integer>(), containerFactory);
		Labeling<Integer> seedLabeling = new Labeling<Integer>(labelingFactory, seedDimensions, "Seeds");
		Labeling<Integer> outputLabeling = new Labeling<Integer>(labelingFactory, outputDimensions, "Output");
		/*
		 * Fill the image.
		 */
		LocalizableCursor<IntType> ic = imageImage.createLocalizableCursor();
		int [] position = imageImage.createPositionArray();
		for (IntType t:ic) {
			ic.getPosition(position);
			t.set(image[position[0]][position[1]]);
		}
		ic.close();
		/*
		 * Fill the seeded image
		 */
		LocalizableCursor<LabelingType<Integer>> sc = seedLabeling.createLocalizableCursor();
		for (LabelingType<Integer> t:sc) {
			sc.getPosition(position);
			int seedLabel = seeds[position[0]][position[1]];
			if (seedLabel == background) continue;
			t.setLabel(seedLabel);
		}
		sc.close();
		if (structuringElement == null) {
			structuringElement = AllConnectedComponents.getStructuringElement(2);
		}
		/*
		 * Run the seeded watershed algorithm
		 */
		Watershed.seededWatershed(imageImage, seedLabeling, structuringElement, outputLabeling);
		/*
		 * Check against expected
		 */
		LocalizableCursor<LabelingType<Integer>> oc = outputLabeling.createLocalizableCursor();
		for (LabelingType<Integer> t:oc) {
			oc.getPosition(position);
			int expectedLabel = expected[position[0]][position[1]];
			List<Integer> l = t.getLabeling(); 
			if (expectedLabel == background) {
				assertTrue(l.isEmpty());
			} else {
				assertEquals(l.size(), 1);
				assertEquals(l.get(0).intValue(), expectedLabel);
			}
		}
	}

	@Test
	public final void testEmpty() {
		testSeededCase2D(
				new int [][] { { 0,0,0 }, { 0,0,0 }, { 0,0,0 } },
				new int [][] { { 0,0,0 }, { 0,0,0 }, { 0,0,0 } },
				new int [][] { { 0,0,0 }, { 0,0,0 }, { 0,0,0 } },
				null, 0);
	}
	@Test
	public final void testOne() {
		testSeededCase2D(
				new int [][] { { 0,0,0 }, { 0,0,0 }, { 0,0,0 } },
				new int [][] { { 0,0,0 }, { 0,1,0 }, { 0,0,0 } },
				new int [][] { { 1,1,1 }, { 1,1,1 }, { 1,1,1 } },
				null, 0);
	}
	@Test
	public final void testTwo() {
		testSeededCase2D(
				new int [][] { { 0,0,0 }, { 0,0,0 }, { 1,1,1 }, { 0,0,0 } },
				new int [][] { { 0,1,0 }, { 0,0,0 }, { 0,0,0 }, { 0,2,0 } },
				new int [][] { { 1,1,1 }, { 1,1,1 }, { 2,2,2 }, { 2,2,2 } },
				null, 0);
	}
}
