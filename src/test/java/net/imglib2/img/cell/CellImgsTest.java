package net.imglib2.img.cell;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.list.ListImg;

public class CellImgsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUnsignedBytesLongArray() {
		CellImgs.unsignedBytes(1);
	}

	@Test
	public void testUnsignedBytesByteArrayLongArray() {
		CellImgs.unsignedBytes(new byte[1],1);
	}

	@Test
	public void testUnsignedBytesByteArrayLongArray1() {
		CellImgs.unsignedBytes(new ByteArray(new byte[1]),1);
	}

	@Test
	public void testUnsignedBytesCellOfALongArray() {
		int[] dimensions = {1};
		long[] imgDimensions = {1};
		long[] min = {0};
		ByteArray data = new ByteArray(new byte[] { 122 });
		Cell<ByteArray> cell = new Cell<ByteArray>(dimensions, min, data);
		CellImgs.unsignedBytes(cell, imgDimensions);
	}

	@Test
	public void testUnsignedBytesListImgOfCellOfALongArray() {
		int[] dimensions = {1};
		ArrayList< Cell<ByteArray> > list = new ArrayList<>(2);
		list.add( new Cell<ByteArray>(dimensions, new long[]{0}, new ByteArray(new byte[]{0})) );
		list.add( new Cell<ByteArray>(dimensions, new long[]{1}, new ByteArray(new byte[]{1})) );
		ListImg< Cell<ByteArray> > listImg = new ListImg<>(list, new long[] {2});
		CellImgs.unsignedBytes(listImg, listImg.dimensionsAsLongArray());
	}

	@Test
	public void testBytesLongArray() {
		CellImgs.bytes(1);
	}

	@Test
	public void testBytesByteArrayLongArray() {
		CellImgs.bytes(new byte[1],1);
	}

	@Test
	public void testBytesByteArrayLongArray1() {
		CellImgs.bytes(new ByteArray(new byte[1]),1);
	}

	@Test
	public void testBytesCellOfALongArray() {
		int[] dimensions = {1};
		long[] imgDimensions = {1};
		long[] min = {0};
		ByteArray data = new ByteArray(new byte[] { 122 });
		Cell<ByteArray> cell = new Cell<ByteArray>(dimensions, min, data);
		CellImgs.bytes(cell, imgDimensions);
	}

	@Test
	public void testBytesListImgOfCellOfALongArray() {
		int[] dimensions = {1};
		ArrayList< Cell<ByteArray> > list = new ArrayList<>(2);
		list.add( new Cell<ByteArray>(dimensions, new long[]{0}, new ByteArray(new byte[]{0})) );
		list.add( new Cell<ByteArray>(dimensions, new long[]{1}, new ByteArray(new byte[]{1})) );
		ListImg< Cell<ByteArray> > listImg = new ListImg<>(list, new long[] {2});
		CellImgs.bytes(listImg, listImg.dimensionsAsLongArray());
	}

	@Test
	public void testUnsignedShortsLongArray() {
		CellImgs.unsignedShorts(1);
	}

	@Test
	public void testUnsignedShortsShortArrayLongArray() {
		CellImgs.unsignedShorts(new short[1],1);
	}

	@Test
	public void testUnsignedShortsShortArrayLongArray1() {
		CellImgs.unsignedShorts(new ShortArray(new short[1]),1);
	}

	@Test
	public void testUnsignedShortsCellOfALongArray() {
		int[] dimensions = {1};
		long[] imgDimensions = {1};
		long[] min = {0};
		ShortArray data = new ShortArray(new short[] { 122 });
		Cell<ShortArray> cell = new Cell<ShortArray>(dimensions, min, data);
		CellImgs.unsignedShorts(cell, imgDimensions);
	}

	@Test
	public void testUnsignedShortsListImgOfCellOfALongArray() {
		int[] dimensions = {1};
		ArrayList< Cell<ShortArray> > list = new ArrayList<>(2);
		list.add( new Cell<ShortArray>(dimensions, new long[]{0}, new ShortArray(new short[]{0})) );
		list.add( new Cell<ShortArray>(dimensions, new long[]{1}, new ShortArray(new short[]{1})) );
		ListImg< Cell<ShortArray> > listImg = new ListImg<>(list, new long[] {2});
		CellImgs.unsignedShorts(listImg, listImg.dimensionsAsLongArray());
	}

	@Test
	public void testShortsLongArray() {
		CellImgs.shorts(1);
	}

	@Test
	public void testShortsPrimitiveShortArrayLongArray() {
		CellImgs.shorts(new short[1],1);
	}

	@Test
	public void testShortsShortArrayLongArray() {
		CellImgs.shorts(new ShortArray(new short[1]),1);
	}

	@Test
	public void testShortsCellOfALongArray() {
		int[] dimensions = {1};
		long[] imgDimensions = {1};
		long[] min = {0};
		ShortArray data = new ShortArray(new short[] { 122 });
		Cell<ShortArray> cell = new Cell<ShortArray>(dimensions, min, data);
		CellImgs.shorts(cell, imgDimensions);	}

	@Test
	public void testShortsListImgOfCellOfALongArray() {
		int[] dimensions = {1};
		ArrayList< Cell<ShortArray> > list = new ArrayList<>(2);
		list.add( new Cell<ShortArray>(dimensions, new long[]{0}, new ShortArray(new short[]{0})) );
		list.add( new Cell<ShortArray>(dimensions, new long[]{1}, new ShortArray(new short[]{1})) );
		ListImg< Cell<ShortArray> > listImg = new ListImg<>(list, new long[] {2});
		CellImgs.shorts(listImg, listImg.dimensionsAsLongArray());
	}

}
