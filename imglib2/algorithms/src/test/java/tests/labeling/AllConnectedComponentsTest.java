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

import net.imglib2.Cursor;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.logic.BitType;

public class AllConnectedComponentsTest {
	private void test2D(boolean [][] input, int[][] expected, long [][] structuringElement, int start, int background) {
		long [] dimensions = new long [] { input.length, input[0].length };
		ArrayImgFactory<BitType> imgFactory = new ArrayImgFactory<BitType>();
		ArrayImgFactory<LabelingType<Integer>> labelingFactory = new ArrayImgFactory<LabelingType<Integer>>();
		ArrayImg<BitType, ? > image = imgFactory.create(dimensions, new BitType());
		NativeImgLabeling<Integer> labeling = new NativeImgLabeling<Integer>(dimensions, labelingFactory);
		labeling.setLinkedType(new LabelingType<Integer>(labeling));
		/*
		 * Fill the image.
		 */
		Cursor<BitType> c = image.localizingCursor();
		int [] position = new int[2];
		while(c.hasNext()) {
			BitType t = c.next();
			c.localize(position);
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
		Cursor<LabelingType<Integer>> lc = labeling.localizingCursor();
		HashMap<Integer, Integer> map = new HashMap<Integer,Integer>();
		while(lc.hasNext()) {
			LabelingType<Integer> lt = lc.next();
			lc.localize(position);
			List<Integer> labels = lt.getLabeling();
			int expectedValue = expected[(int)(position[0])][(int)(position[1])]; 
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
		for (long [] offset:AllConnectedComponents.getStructuringElement(2)) {
			boolean [][] input = new boolean [3][3];
			int [][] expected = new int[3][3];
			
			input[(int)(offset[0])+1][(int)(offset[1])+1] = true;
			expected[(int)(offset[0])+1][(int)(offset[1])+1] = 1;
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
	public void testBigBigObject() {
		/*
		 * This is a regression test of a bug reported
		 * by Jean-Yves Tinevez. The code should fail on a
		 * 2-d array that needs to push more than 675 / 2 elements.
		 */
		boolean [][] input = new boolean [100][100];
		int [][] expected = new int [100][100];
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
		long [][] strel = {{-1,0},{1,0},{0,-1},{0,1}};
		test2D(input, expected, strel, 1, 0);
		
	}
}
