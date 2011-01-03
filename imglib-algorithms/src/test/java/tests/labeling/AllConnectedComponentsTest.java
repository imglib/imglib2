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


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

import mpicbg.imglib.algorithm.labeling.AllConnectedComponents;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.labeling.Labeling;
import mpicbg.imglib.labeling.LabelingType;
import mpicbg.imglib.type.logic.BitType;

public class AllConnectedComponentsTest {
	private void test2D(boolean [][] input, int [][] expected, int [][] structuringElement, int start, int background) {
		int [] dimensions = new int [] { input.length, input[0].length };
		DirectAccessContainerFactory containerFactory = new ArrayContainerFactory();
		ImageFactory<BitType> bitFactory = new ImageFactory<BitType>(new BitType(), containerFactory);
		Image<BitType> image = bitFactory.createImage(dimensions);
		ImageFactory<LabelingType<Integer>> labelingFactory = new ImageFactory<LabelingType<Integer>>(new LabelingType<Integer>(), containerFactory);
		Labeling<Integer> labeling = new Labeling<Integer>(labelingFactory, dimensions, "Foo");
		/*
		 * Fill the image.
		 */
		LocalizableCursor<BitType> c = image.createLocalizableCursor();
		int [] position = image.createPositionArray();
		for (BitType t:c) {
			c.getPosition(position);
			t.set(input[position[0]][position[1]]);
		}
		/*
		 * Run the algorithm.
		 */
		Iterator<Integer> names = AllConnectedComponents.getIntegerNames(start);
		if (structuringElement == null)
			AllConnectedComponents.labelAllConnectedComponents(labeling, image, names);
		else
			AllConnectedComponents.labelAllConnectedComponents(labeling, image, names, structuringElement);
			
		/*
		 * Check the result
		 */
		LocalizableCursor<LabelingType<Integer>> lc = labeling.createLocalizableCursor();
		HashMap<Integer, Integer> map = new HashMap<Integer,Integer>();
		for (LabelingType<Integer> lt:lc) {
			lc.getPosition(position);
			List<Integer> labels = lt.getLabeling();
			int expectedValue = expected[position[0]][position[1]]; 
			if (expectedValue == background)
				assertEquals(labels.size(),0);
			else {
				assertEquals(labels.size(), 1);
				final Integer value = labels.get(0);
				if (map.containsKey(value))
					assertEquals(expectedValue, map.get(value).intValue());
				else
					map.put(value, expectedValue);
			}
		}
	}
	@Test
	public void testEmpty() {
		boolean [][] input = new boolean[3][3];
		int [][] expected = new int[3][3];
		test2D(input, expected, null, 1, 0);
	}
	@Test
	public void testOne() {
		boolean [][] input = new boolean[] [] {
				{ false, false, false },
				{ false, true, false },
				{ false, false, false }};
		int [][] expected = new int [][] {
				{ 0, 0, 0 },
				{ 0, 1, 0 },
				{ 0, 0, 0 }};
		test2D(input, expected, null, 1, 0);
	}
	@Test
	public void testOutOfBounds() {
		/*
		 * Make sure that the labeler can handle out of bounds conditions
		 */
		for (int [] offset:AllConnectedComponents.getStructuringElement(2)) {
			boolean [][] input = new boolean [3][3];
			int [][] expected = new int[3][3];
			
			input[offset[0]+1][offset[1]+1] = true;
			expected[offset[0]+1][offset[1]+1] = 1;
			test2D(input, expected, null, 1, 0);
		}
	}
	@Test
	public void testOneObject() {
		boolean [][] input = new boolean [][] {
				{ false, false, false, false, false },
				{ false, true, true, true, false },
				{ false, true, true, true, false },
				{ false, true, true, true, false },
				{ false, false, false, false, false }};
		int [][] expected = new int [][] {
				{ 0,0,0,0,0 },
				{ 0,1,1,1,0 },
				{ 0,1,1,1,0 },
				{ 0,1,1,1,0 },
				{ 0,0,0,0,0 }};
		test2D(input, expected, null, 1, 0);
	}
	@Test
	public void testTwoObjects() {
		boolean [][] input = new boolean [][] {
				{ false, false, false, false, false },
				{ false, true, true, true, false },
				{ false, false, false, false, false },
				{ false, true, true, true, false },
				{ false, false, false, false, false }};
		int [][] expected = new int [][] {
				{ 0,0,0,0,0 },
				{ 0,1,1,1,0 },
				{ 0,0,0,0,0 },
				{ 0,2,2,2,0 },
				{ 0,0,0,0,0 }};
		test2D(input, expected, null, 1, 0);
	}
	
	@Test
	public void testBigObject() {
		/*
		 * Internally, AllConnectedComponents has a custom stack
		 * that grows when it hits 100 elements. So we exercise that code.
		 */
		boolean [][] input = new boolean [25][25];
		int [][] expected = new int [25][25];
		for (int i=0;i<input.length; i++) {
			Arrays.fill(input[i], true);
			Arrays.fill(expected[i], 1);
		}
		test2D(input, expected, null, 1, 0);
	}
	@Test
	public void testCustomStrel() {
		boolean [][] input = new boolean [][] {
				{ false, false, false, false, false },
				{ false, true, true, true, false },
				{ true, false, false, false, false },
				{ false, true, true, true, false },
				{ false, false, false, false, false }};
		int [][] expected = new int [][] {
				{ 0,0,0,0,0 },
				{ 0,1,1,1,0 },
				{ 3,0,0,0,0 },
				{ 0,2,2,2,0 },
				{ 0,0,0,0,0 }};
		int [][] strel = {{-1,0},{1,0},{0,-1},{0,1}};
		test2D(input, expected, strel, 1, 0);
		
	}
}
