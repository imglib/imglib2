/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package tests.labeling;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.labeling.Labeling;
import mpicbg.imglib.labeling.LabelingType;
import mpicbg.imglib.type.label.FakeType;

import org.junit.Test;

/**
 * TODO
 *
 * @author Lee Kamentsky
 */
public class LabelingTest {
	protected <T extends Comparable<T>> Labeling<T> makeLabeling(T exemplar, int [] dimensions) {
		DirectAccessContainer<LabelingType<T>, IntAccess> container;
		Labeling<T> labeling;
		LabelingType<T> type;
		container = new ArrayContainerFactory().createIntInstance(dimensions, 1);
		type = new LabelingType<T>(container);
		labeling = new Labeling<T>(container, type);
		container.setLinkedType(type);
		return labeling;
	}
	protected <T extends Comparable<T>> Labeling<T> makeLabeling(
			int [][] coordinates, T [] labels, int [] dimensions) {
		assertTrue(labels.length > 0);
		assertEquals(labels.length, coordinates.length);
		Labeling<T> labeling = makeLabeling(labels[0], dimensions);
		LocalizableByDimCursor<LabelingType<T>> c = labeling.createLocalizableByDimCursor();
		for (int i=0;i < coordinates.length; i++) {
			c.setPosition(coordinates[i]);
			List<T> currentLabels = new ArrayList<T>(c.getType().getLabeling());
			if (! currentLabels.contains(labels[i]))
				currentLabels.add(labels[i]);
			c.getType().setLabeling(currentLabels);
		}
		return labeling;
	}
	
	protected <T extends Comparable<T>> void labelSphere(
			Labeling<T> labeling, T label, double [] center, double radius) {
		LocalizableCursor<LabelingType<T>> c = labeling.createLocalizableCursor();
		int [] position = labeling.createPositionArray();
		for (LabelingType<T> t:c) {
			c.getPosition(position);
			double distance2 = 0;
			double distance = 0;
			for (int i=0; i<position.length; i++) {
				distance = ((double) position[i] - center[i]);
				distance2 += distance * distance;
			}
			distance = Math.sqrt(distance2);
			if (distance <= radius) {
				List<T> labels = new ArrayList<T>(t.getLabeling());
				if (! labels.contains(label))
					labels.add(label);
					t.setLabeling(labels);
			}
		}
	}
	@Test
	public void testContainerConstructor() {
		int [] dimensions = { 5,6,7 };
		DirectAccessContainer<LabelingType<String>, IntAccess> container;
		Labeling<String> labeling;
		LabelingType<String> type;
		container = new ArrayContainerFactory().createIntInstance(dimensions, 1);
		type = new LabelingType<String>(container);
		labeling = new Labeling<String>(container, type);
		assertTrue(Arrays.equals(dimensions, labeling.getDimensions()));
	}
	
	@Test
	public void testNamedContainerConstructor() {
		int [] dimensions = { 5,6,7 };
		DirectAccessContainer<LabelingType<String>, IntAccess> container;
		Labeling<String> labeling;
		LabelingType<String> type;
		container = new ArrayContainerFactory().createIntInstance(dimensions, 1);
		type = new LabelingType<String>(container);
		labeling = new Labeling<String>(container, type, "Foo");
		assertTrue(Arrays.equals(dimensions, labeling.getDimensions()));
	}
	
	@Test
	public void testImageFactoryConstructor() {
		int [] dimensions = { 5,6,7 };
		ImageFactory<LabelingType<String>> factory;
		factory = new ImageFactory<LabelingType<String>>(
				new LabelingType<String>(), 
				new ArrayContainerFactory());
		Labeling<String> labeling = new Labeling<String>(factory, dimensions, "Foo");
		assertTrue(Arrays.equals(dimensions, labeling.getDimensions()));
	}
	
	@Test
	public void testEmptyImage() {
		Labeling<String> labeling = makeLabeling("Foo", new int [] { 5,6,7});
		assertTrue(labeling.getLabels().isEmpty());
		Cursor<LabelingType<String>> c = labeling.createCursor();
		int iterations = 0;
		for (LabelingType<String> t:c){
			assertTrue(t.getLabeling().size() == 0);
			iterations++;
		}
		c.close();
		assertTrue(iterations == 5 * 6 * 7);
	}
	
	@Test
	public void testLabelOne() {
		int [][] coordinates = {{ 1,1,1}};
		String [] labels = { "Foo" };
		int [] dimensions = new int [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		assertEquals(labeling.getLabels().size(), 1);
		assertTrue(labeling.getLabels().contains("Foo"));
	}
	@Test
	public void testGetAreaOne() {
		int [][] coordinates = {{ 1,1,1}};
		String [] labels = { "Foo" };
		int [] dimensions = new int [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		assertEquals(labeling.getArea("Foo"), 1);
	}
	@Test
	public void testExtentsOne() {
		int [][] coordinates = {{ 1,3,5}};
		String [] labels = { "Foo" };
		int [] dimensions = new int [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		int [] minExtents = labeling.createPositionArray();
		int [] maxExtents = labeling.createPositionArray();
		assertFalse(labeling.getExtents("Bar", minExtents, maxExtents));
		assertTrue(labeling.getExtents("Foo", minExtents, maxExtents));
		assertArrayEquals(coordinates[0], minExtents);
		int [] expectedMaxExtents = coordinates[0].clone();
		for (int i = 0; i<3; i++) expectedMaxExtents[i]++;
		assertArrayEquals(expectedMaxExtents, maxExtents);
	}
	@Test
	public void testRasterStartOne() {
		int [][] coordinates = {{ 1,3,5}};
		String [] labels = { "Foo" };
		int [] dimensions = new int [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		int [] rasterStart = labeling.createPositionArray();
		assertFalse(labeling.getRasterStart("Bar", rasterStart));
		assertTrue(labeling.getRasterStart("Foo", rasterStart));
		assertArrayEquals(coordinates[0], rasterStart);
	}
	@Test
	public void testLocalizableCursorOne() {
		int [][] coordinates = {{ 1,3,2}};
		String [] labels = { "Foo" };
		int [] dimensions = new int [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		LocalizableCursor<FakeType> c = labeling.createLocalizableLabelCursor("Foo");
		int iterations = 0;
		for (@SuppressWarnings("unused") FakeType t:c) {
			iterations++;
			assertEquals(c.getPosition(0), 1);
			assertEquals(c.getPosition(1), 3);
			assertEquals(c.getPosition(2), 2);
		}
		assertEquals(iterations, 1);
		c.close();
	}
	@Test
	public void testLocalizablePerimeterCursorOne() {
		int [][] coordinates = {{ 1,3,2}};
		String [] labels = { "Foo" };
		int [] dimensions = new int [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		LocalizableCursor<FakeType> c = labeling.createLocalizablePerimeterCursor("Foo");
		int iterations = 0;
		for (@SuppressWarnings("unused") FakeType t:c) {
			iterations++;
			assertEquals(c.getPosition(0), 1);
			assertEquals(c.getPosition(1), 3);
			assertEquals(c.getPosition(2), 2);
		}
		assertEquals(iterations, 1);
		c.close();
	}
	@Test
	public void testSphere() {
		int [] dimensions = new int [] { 20,20,20 };
		Labeling<String> labeling = makeLabeling("MyLabels", dimensions);
		labelSphere(labeling, "Foo", new double [] { 10,9,8 }, 5);
		/*
		 * Test the extents
		 */
		int [] minExtents = labeling.createPositionArray();
		int [] maxExtents = labeling.createPositionArray();
		assertTrue(labeling.getExtents("Foo", minExtents, maxExtents));
		assertArrayEquals(new int [] { 5,4,3}, minExtents);
		assertArrayEquals(new int [] { 16, 15, 14 }, maxExtents);
		/*
		 * Test the raster start which should be 5, 9, 8
		 */
		int [] start = labeling.createPositionArray();
		assertTrue(labeling.getRasterStart("Foo", start));
		assertArrayEquals(new int [] { 5, 9, 8}, start);
		double expectedVolumeLow = 4./ 3. * Math.PI * Math.pow(4.5, 3);
		double expectedVolumeHigh = 4./ 3. * Math.PI * Math.pow(5.5, 3);
		assertTrue(labeling.getArea("Foo") > expectedVolumeLow);
		assertTrue(labeling.getArea("Foo") < expectedVolumeHigh);
		/*
		 * Iterate through the perimeter pixels, checking for the
		 * boundary condition.
		 */
		LocalizableCursor<FakeType> c = labeling.createLocalizablePerimeterCursor("Foo");
		LocalizableByDimCursor<LabelingType<String>> dc = labeling.createLocalizableByDimCursor();
		int [] position = labeling.createPositionArray();
		for (@SuppressWarnings("unused") FakeType t: c) {
			c.getPosition(position);
			dc.setPosition(position);
			assertTrue(dc.getType().getLabeling().contains("Foo"));
			boolean foundEdge = false;
			for (int i =0; i<3; i++) {
				for (int j = -1; j <= 1; j += 2) {
					int [] test = position.clone();	
					test[i] += j;
					dc.setPosition(test);
					if (! dc.getType().getLabeling().contains("Foo")) {
						foundEdge = true;
						break;
					}
				}
			}
			assertTrue(foundEdge);
		}
		c.close();
	}
	@Test
	public void testTwoLabels() {
		int [] dimensions = new int [] { 20,20,40 };
		Labeling<String> labeling = makeLabeling("MyLabels", dimensions);
		String [] labels = { "Foo", "Bar" };
		double [][] centers = {{10,9,8}, { 8, 9, 30 }};
		for (int i=0; i<2; i++) {
			labelSphere(labeling, labels[i], centers[i], 5);
		}
		int [] temp = labeling.createPositionArray();
		for (int i=0; i<2; i++ ) {
			double [] coords = new double[3];
			Arrays.fill(coords, 0);
			LocalizableCursor<FakeType> c = labeling.createLocalizableLabelCursor(labels[i]);
			for (@SuppressWarnings("unused") FakeType t:c) {
				c.getPosition(temp);
				for (int j=0;j<temp.length; j++) {
					coords[j] += temp[j];
				}
			}
			c.close();
			for (int j=0;j<coords.length; j++) {
				coords[j] /= labeling.getArea(labels[i]);
				assertTrue(Math.abs(coords[j] - centers[i][j]) < .5);
			}
		}
	}
	@Test
	public void testOverlappingLabels() {
		int [] dimensions = new int [] { 20,20,30 };
		Labeling<String> labeling = makeLabeling("MyLabels", dimensions);
		String [] labels = { "Foo", "Bar" };
		double [][] centers = {{10,9,8}, { 8, 9, 12 }};
		for (int i=0; i<2; i++) {
			labelSphere(labeling, labels[i], centers[i], 5);
		}
		Collection<String> foundLabels = labeling.getLabels();
		assertEquals(foundLabels.size(),2);
		for (int i=0; i<2; i++) {
			assertTrue(foundLabels.contains(labels[i]));
		}
		int [] temp = labeling.createPositionArray();
		for (int i=0; i<2; i++ ) {
			double [] coords = new double[3];
			Arrays.fill(coords, 0);
			LocalizableCursor<FakeType> c = labeling.createLocalizableLabelCursor(labels[i]);
			for (@SuppressWarnings("unused") FakeType t:c) {
				c.getPosition(temp);
				for (int j=0;j<temp.length; j++) {
					coords[j] += temp[j];
				}
			}
			c.close();
			for (int j=0;j<coords.length; j++) {
				coords[j] /= labeling.getArea(labels[i]);
				assertTrue(Math.abs(coords[j] - centers[i][j]) < .5);
			}
		}
	}
}
