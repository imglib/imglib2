package net.imglib2.util;

import static org.junit.Assert.*;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

public class ImgUtilTest {

	@Test
	public void testCopyDoubleArrayIntIntArrayImgOfT() {
		double [] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		int [] offsets = { 0, 0, 8 };
		int [][] strides = { {1, 3}, {3, 1}, { -1, -3} };
		double [][][] expected = {
				{ 
					{ 0, 1, 2 },
					{ 3, 4, 5 },
					{ 6, 7, 8 }
				},{
					{ 0, 3, 6 },
					{ 1, 4, 7 },
					{ 2, 5, 8 }
				},{
					{ 8, 7, 6 },
					{ 5, 4, 3 },
					{ 2, 1, 0 }
				} };
		for (int i=0; i < offsets.length; i++) {
			Img<DoubleType> img = new ArrayImgFactory<DoubleType>().create(new long [] {3,3} , new DoubleType());
			ImgUtil.copy(input, offsets[i], strides[i], img);
			RandomAccess<DoubleType> ra = img.randomAccess();
			long [] location = new long[2];
			for (int x = 0; x<3; x++) {
				location[0] = x;
				for (int y=0; y<3; y++) {
					location[1] = y;
					ra.setPosition(location);
					assertEquals(expected[i][y][x], ra.get().get(), 0);
				}
			}
		}
	}

	@Test
	public void testCopyFloatArrayIntIntArrayImgOfT() {
		float [] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		int [] offsets = { 0, 0, 8 };
		int [][] strides = { {1, 3}, {3, 1}, { -1, -3} };
		float [][][] expected = {
				{ 
					{ 0, 1, 2 },
					{ 3, 4, 5 },
					{ 6, 7, 8 }
				},{
					{ 0, 3, 6 },
					{ 1, 4, 7 },
					{ 2, 5, 8 }
				},{
					{ 8, 7, 6 },
					{ 5, 4, 3 },
					{ 2, 1, 0 }
				} };
		for (int i=0; i < offsets.length; i++) {
			Img<FloatType> img = new ArrayImgFactory<FloatType>().create(new long [] {3,3} , new FloatType());
			ImgUtil.copy(input, offsets[i], strides[i], img);
			RandomAccess<FloatType> ra = img.randomAccess();
			long [] location = new long[2];
			for (int x = 0; x<3; x++) {
				location[0] = x;
				for (int y=0; y<3; y++) {
					location[1] = y;
					ra.setPosition(location);
					assertEquals(expected[i][y][x], ra.get().get(), 0);
				}
			}
		}
	}

	@Test
	public void testCopyLongArrayIntIntArrayImgOfT() {
		long [] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		int [] offsets = { 0, 0, 8 };
		int [][] strides = { {1, 3}, {3, 1}, { -1, -3} };
		long [][][] expected = {
				{ 
					{ 0, 1, 2 },
					{ 3, 4, 5 },
					{ 6, 7, 8 }
				},{
					{ 0, 3, 6 },
					{ 1, 4, 7 },
					{ 2, 5, 8 }
				},{
					{ 8, 7, 6 },
					{ 5, 4, 3 },
					{ 2, 1, 0 }
				} };
		for (int i=0; i < offsets.length; i++) {
			Img<LongType> img = new ArrayImgFactory<LongType>().create(new long [] {3,3} , new LongType());
			ImgUtil.copy(input, offsets[i], strides[i], img);
			RandomAccess<LongType> ra = img.randomAccess();
			long [] location = new long[2];
			for (int x = 0; x<3; x++) {
				location[0] = x;
				for (int y=0; y<3; y++) {
					location[1] = y;
					ra.setPosition(location);
					assertEquals(expected[i][y][x], ra.get().get(), 0);
				}
			}
		}
	}

	@Test
	public void testCopyIntArrayIntIntArrayImgOfT() {
		int [] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		int [] offsets = { 0, 0, 8 };
		int [][] strides = { {1, 3}, {3, 1}, { -1, -3} };
		int [][][] expected = {
				{ 
					{ 0, 1, 2 },
					{ 3, 4, 5 },
					{ 6, 7, 8 }
				},{
					{ 0, 3, 6 },
					{ 1, 4, 7 },
					{ 2, 5, 8 }
				},{
					{ 8, 7, 6 },
					{ 5, 4, 3 },
					{ 2, 1, 0 }
				} };
		for (int i=0; i < offsets.length; i++) {
			Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {3,3} , new IntType());
			ImgUtil.copy(input, offsets[i], strides[i], img);
			RandomAccess<IntType> ra = img.randomAccess();
			long [] location = new long[2];
			for (int x = 0; x<3; x++) {
				location[0] = x;
				for (int y=0; y<3; y++) {
					location[1] = y;
					ra.setPosition(location);
					assertEquals(expected[i][y][x], ra.get().get(), 0);
				}
			}
		}
	}

	@Test
	public void testCopyImgOfTDoubleArrayIntIntArray() {
		double [][] input = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
		int [] offsets = { 0, 0, 8 };
		int [][] strides = { {1, 3}, {3, 1}, { -1, -3} };
		double [][] expected = {
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8 },
				{ 0, 3, 6, 1, 4, 7, 2, 5, 8 },
				{ 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		double [] output = new double[9];
		Img<DoubleType> img = new ArrayImgFactory<DoubleType>().create(new long [] {3,3} , new DoubleType());
		RandomAccess<DoubleType> ra = img.randomAccess();
		long [] location = new long[2];
		for (int x = 0; x<3; x++) {
			location[0] = x;
			for (int y=0; y<3; y++) {
				location[1] = y;
				ra.setPosition(location);
				ra.get().set(input[y][x]);
			}
		}
		for (int i=0; i< offsets.length; i++) {
			ImgUtil.copy(img, output, offsets[i], strides[i]);
			assertArrayEquals(expected[i], output, 0);
		}
	}

	@Test
	public void testCopyImgOfTFloatArrayIntIntArray() {
		float [][] input = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
		int [] offsets = { 0, 0, 8 };
		int [][] strides = { {1, 3}, {3, 1}, { -1, -3} };
		float [][] expected = {
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8 },
				{ 0, 3, 6, 1, 4, 7, 2, 5, 8 },
				{ 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		float [] output = new float[9];
		Img<FloatType> img = new ArrayImgFactory<FloatType>().create(new long [] {3,3} , new FloatType());
		RandomAccess<FloatType> ra = img.randomAccess();
		long [] location = new long[2];
		for (int x = 0; x<3; x++) {
			location[0] = x;
			for (int y=0; y<3; y++) {
				location[1] = y;
				ra.setPosition(location);
				ra.get().set(input[y][x]);
			}
		}
		for (int i=0; i< offsets.length; i++) {
			ImgUtil.copy(img, output, offsets[i], strides[i]);
			assertArrayEquals(expected[i], output, 0);
		}
	}

	@Test
	public void testCopyImgOfTLongArrayIntIntArray() {
		long [][] input = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
		int [] offsets = { 0, 0, 8 };
		int [][] strides = { {1, 3}, {3, 1}, { -1, -3} };
		long [][] expected = {
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8 },
				{ 0, 3, 6, 1, 4, 7, 2, 5, 8 },
				{ 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		long [] output = new long[9];
		Img<LongType> img = new ArrayImgFactory<LongType>().create(new long [] {3,3} , new LongType());
		RandomAccess<LongType> ra = img.randomAccess();
		long [] location = new long[2];
		for (int x = 0; x<3; x++) {
			location[0] = x;
			for (int y=0; y<3; y++) {
				location[1] = y;
				ra.setPosition(location);
				ra.get().set(input[y][x]);
			}
		}
		for (int i=0; i< offsets.length; i++) {
			ImgUtil.copy(img, output, offsets[i], strides[i]);
			assertArrayEquals(expected[i], output);
		}
	}

	@Test
	public void testCopyImgOfTIntArrayIntIntArray() {
		int [][] input = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
		int [] offsets = { 0, 0, 8 };
		int [][] strides = { {1, 3}, {3, 1}, { -1, -3} };
		int [][] expected = {
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8 },
				{ 0, 3, 6, 1, 4, 7, 2, 5, 8 },
				{ 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		int [] output = new int[9];
		Img<LongType> img = new ArrayImgFactory<LongType>().create(new long [] {3,3} , new LongType());
		RandomAccess<LongType> ra = img.randomAccess();
		long [] location = new long[2];
		for (int x = 0; x<3; x++) {
			location[0] = x;
			for (int y=0; y<3; y++) {
				location[1] = y;
				ra.setPosition(location);
				ra.get().set(input[y][x]);
			}
		}
		for (int i=0; i< offsets.length; i++) {
			ImgUtil.copy(img, output, offsets[i], strides[i]);
			assertArrayEquals(expected[i], output);
		}
	}

}
