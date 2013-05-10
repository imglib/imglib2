/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
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

package net.imglib2.ops.sandbox;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.ops.pointset.GeneralPointSet;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSetIterator;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class SpeedCheckPointSets {

	// Test how quickly you can iterate and grab pixels from images using the
	// old and new point set implementations
	
	// NB - try to make sure loops aren't completely optimized away because they
	// did no real work.

	private static double unimportantSum = 0;

	static final int ITERS = 7;

	private static void init(Img<FloatType> img) {
		float i = 0;
		for (FloatType pix : img) pix.set(i++);
	}
	
	private static List<long[]> points(long size) {
		List<long[]> points = new ArrayList<long[]>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				points.add(new long[]{i,j});
			}
		}
		return points;
	}
	
	private static void testGeneral() {
		final long SIZE = 700;
		final Img<FloatType> img1 = ArrayImgs.floats(SIZE, SIZE);
		final Img<FloatType> img2 = ArrayImgs.floats(SIZE, SIZE);
		final Img<FloatType> img3 = ArrayImgs.floats(SIZE, SIZE);
		final Img<FloatType> img4 = ArrayImgs.floats(SIZE, SIZE);
		
		init(img1);
		init(img2);
		init(img3);
		init(img4);
	
		final List<long[]> pts1 = points(SIZE);
		final List<long[]> pts2 = points(SIZE);
		final List<long[]> pts3 = points(SIZE);
		final List<long[]> pts4 = points(SIZE);
		
		System.out.println("Iterate old general point set");
		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{

			GeneralPointSet ps = new GeneralPointSet(pts1.get(0), pts1);
			@Override
			public void run()
			{
				for (int i = 0; i < ITERS; i++) {
					PointSetIterator iter = ps.iterator();
					while (iter.hasNext()) {
						iter.next();
						unimportantSum++;
					}
				}
			}
		});
		
		System.out.println("Iterate new general point set");
		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{

			BoundGeneralPointSet ps = new BoundGeneralPointSet(pts2);
			@Override
			public void run()
			{
				for (int i = 0; i < ITERS; i++) {
					Cursor<?> cursor = ps.cursor();
					while (cursor.hasNext()) {
						cursor.fwd();
						unimportantSum++;
					}
				}
			}
		});
		
		System.out.println("Access old general point set");
		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{

			GeneralPointSet ps = new GeneralPointSet(pts3.get(0), pts3);
			@Override
			public void run()
			{
				for (int i = 0; i < ITERS; i++) {
					RandomAccess<FloatType> access = img1.randomAccess();
					PointSetIterator iter = ps.iterator();
					while (iter.hasNext()) {
						long[] pos = iter.next();
						access.setPosition(pos);
						unimportantSum += access.get().get();
					}
					// System.out.println("Final sum == "+sum);
				}
			}
		});
		
		System.out.println("Access new general point set");
		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{

			BoundGeneralPointSet ps = new BoundGeneralPointSet(pts4);
			@Override
			public void run()
			{
				for (int i = 0; i < ITERS; i++) {
					Cursor<FloatType> cursor = ps.bind(img2.randomAccess());
					while (cursor.hasNext()) {
						unimportantSum += cursor.next().getRealFloat();
					}
				}
				//System.out.println("Final sum == "+sum);
			}
		});
	}
	
	private static void testHyperVolume() {
		final long SIZE = 600;
		final long[] BOX_MIN = new long[] { 0, 0 };
		final long[] BOX_MAX = new long[] { SIZE - 1, SIZE - 1 };
		final Img<FloatType> img1 = ArrayImgs.floats(SIZE, SIZE);
		final Img<FloatType> img2 = ArrayImgs.floats(SIZE, SIZE);

		System.out.println("Iterate old hypervolume point set");
		BenchmarkHelper.benchmarkAndPrint(10, true, new Runnable() {

			HyperVolumePointSet ps = new HyperVolumePointSet(BOX_MIN, BOX_MAX);

			@Override
			public void run() {
				for (int i = 0; i < ITERS; i++) {
					PointSetIterator iter = ps.iterator();
					while (iter.hasNext()) {
						iter.next();
						unimportantSum++;
					}
				}
			}
		});

		System.out.println("Iterate new hypervolume point set");
		BenchmarkHelper.benchmarkAndPrint(10, true, new Runnable() {

			BoundHyperVolumePointSet ps = new BoundHyperVolumePointSet(BOX_MIN,
				BOX_MAX);

			@Override
			public void run() {
				for (int i = 0; i < ITERS; i++) {
					Cursor<?> cursor = ps.cursor();
					while (cursor.hasNext()) {
						cursor.fwd();
						unimportantSum++;
					}
				}
			}
		});

		System.out.println("Access old hypervolume point set");
		BenchmarkHelper.benchmarkAndPrint(10, true, new Runnable() {

			HyperVolumePointSet ps = new HyperVolumePointSet(BOX_MIN, BOX_MAX);

			@Override
			public void run() {
				for (int i = 0; i < ITERS; i++) {
					RandomAccess<FloatType> access = img1.randomAccess();
					PointSetIterator iter = ps.iterator();
					while (iter.hasNext()) {
						long[] pos = iter.next();
						access.setPosition(pos);
						unimportantSum += access.get().get();
					}
					// System.out.println("Final sum == "+sum);
				}
			}
		});

		System.out.println("Access new hypervolume point set");
		BenchmarkHelper.benchmarkAndPrint(10, true, new Runnable() {

			BoundHyperVolumePointSet ps = new BoundHyperVolumePointSet(BOX_MIN,
				BOX_MAX);

			@Override
			public void run() {
				for (int i = 0; i < ITERS; i++) {
					Cursor<FloatType> cursor = ps.bind(img2.randomAccess());
					while (cursor.hasNext()) {
						unimportantSum += cursor.next().getRealFloat();
					}
					// System.out.println("Final sum == "+sum);
				}
			}
		});
	}

	public static void main(String[] args) {
		testGeneral();
		testHyperVolume();
		System.out.println("Unimportant sum = " +
			String.format("%20.0f", unimportantSum));
	}
}
