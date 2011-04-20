/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Lee Kamentsky
 *
 */
package tests.labeling;

import static org.junit.Assert.*;

import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.algorithm.labeling.Watershed;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.Test;

public class WatershedTest {
	private void testSeededCase2D(int [][] image,
			              	int [][] seeds,
			              	int [][] expected,
			              	long [][] structuringElement,
			              	int background) {
		long [] imageDimensions = new long [] { image.length, image[0].length };
		long [] seedDimensions = new long [] { seeds.length, seeds[0].length };
		long [] outputDimensions = new long [] { expected.length, expected[0].length };
		NativeImgLabeling<Integer> seedLabeling = new NativeImgLabeling<Integer>(seedDimensions);
		seedLabeling.setLinkedType(new LabelingType<Integer>(seedLabeling));
		NativeImgLabeling<Integer> outputLabeling = new NativeImgLabeling<Integer>(outputDimensions);
		outputLabeling.setLinkedType(new LabelingType<Integer>(outputLabeling));
		NativeImg<IntType, ? extends IntAccess> imageImage = 
			new ArrayImgFactory<IntType>().createIntInstance(imageDimensions, 1);
		imageImage.setLinkedType(new IntType(imageImage));
		/*
		 * Fill the image.
		 */
		Cursor<IntType> ic = imageImage.localizingCursor();
		int [] position = new int [imageImage.numDimensions()];
		while(ic.hasNext()) {
			IntType t = ic.next();
			ic.localize(position);
			t.set(image[position[0]][position[1]]);
		}
		/*
		 * Fill the seeded image
		 */
		Cursor<LabelingType<Integer>> sc = seedLabeling.localizingCursor();
		while(sc.hasNext()) {
			LabelingType<Integer> t = sc.next();
			sc.localize(position);
			int seedLabel = seeds[position[0]][position[1]];
			if (seedLabel == background) continue;
			t.setLabel(seedLabel);
		}
		if (structuringElement == null) {
			structuringElement = AllConnectedComponents.getStructuringElement(2);
		}
		/*
		 * Run the seeded watershed algorithm
		 */
		Watershed<IntType, Integer> watershed = new Watershed<IntType, Integer>();
		watershed.setSeeds(seedLabeling);
		watershed.setIntensityImage(imageImage);
		watershed.setStructuringElement(structuringElement);
		watershed.setOutputLabeling(outputLabeling);
		assertTrue(watershed.process());
		/*
		 * Check against expected
		 */
		Cursor<LabelingType<Integer>> oc = outputLabeling.localizingCursor();
		while(oc.hasNext()) {
			LabelingType<Integer> t = oc.next(); 
			oc.localize(position);
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
