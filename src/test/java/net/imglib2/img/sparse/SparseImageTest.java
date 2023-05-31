package net.imglib2.img.sparse;

import java.util.HashMap;
import java.util.Map;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;


public class SparseImageTest {

	protected static Map<String, CompressedStorageImg<DoubleType, LongType>> sparseImgs;

	@Test
	public void CsrSetupIsCorrect() {
		CsrImg<DoubleType, LongType> csr = setupCsr();
		assertEquals(2, csr.numDimensions());
		assertArrayEquals(new long[]{0, 0}, csr.minAsLongArray());
		assertArrayEquals(new long[]{9, 8}, csr.maxAsLongArray());
	}

	@Test
	public void equalityTestIsCorrect() {
		CsrImg<DoubleType, LongType> csr = setupCsr();
		CsrImg<DoubleType, LongType> csr2 = csr.copy();
		CscImg<DoubleType, LongType> csc = setupCsc();
		assertTrue(csr.equals(csr));
	}

	@Test
	public void CsrNonzeroEntriesAreCorrect() {
		int[] x = new int[]{2, 5, 0, 6, 9};
		int[] y = new int[]{0, 1, 2, 8, 8};

		for (int i = 0; i < x.length; i++) {
			RandomAccess<DoubleType> ra = setupCsr().randomAccess();
			assertEquals("Mismatch for x=" + x[i] + ", y=" + y[i], 1.0, ra.setPositionAndGet(x[i],y[i]).getRealDouble(), 1e-6);
		}
	}

	@Test
	public void sparseHasCorrectNumberOfNonzeros() {
		for (Map.Entry<String, CompressedStorageImg<DoubleType, LongType>> entry : sparseImgs.entrySet()) {
			assertEquals("Mismatch for " + entry.getKey(), 5, CompressedStorageImg.getNumberOfNonzeros(entry.getValue()));
		}
	}

	@Test
	public void conversionToSparseIsCorrect() {
		for (CompressedStorageImg<DoubleType, LongType> sparse : sparseImgs.values()) {
			assertEquals(5, CompressedStorageImg.getNumberOfNonzeros(sparse));
			CompressedStorageImg<DoubleType, LongType> newCsr = CompressedStorageImg.convertToSparse(sparse, 0);
			assertTrue(newCsr instanceof CsrImg);
			assert2DRaiEquals(sparse, newCsr);
			CompressedStorageImg<DoubleType, LongType> newCsc = CompressedStorageImg.convertToSparse(sparse, 1);
			assertTrue(newCsc instanceof CscImg);
			assert2DRaiEquals(sparse, newCsc);
		}
	}

	@Test
	public void CscIsCsrTransposed() {
		CsrImg<DoubleType, LongType> csr = setupCsr();
		CscImg<DoubleType, LongType> csc = setupCsc();
		assert2DRaiEquals(csr, Views.permute(csc, 0, 1));
	}

	protected CsrImg<DoubleType, LongType> setupCsr() {
		return (CsrImg<DoubleType, LongType>) sparseImgs.get("CSR");
	}

	protected CscImg<DoubleType, LongType> setupCsc() {
		return (CscImg<DoubleType, LongType>) sparseImgs.get("CSC");
	}

	@BeforeClass
	public static void setupSparseImages() {
		Img<DoubleType> data = ArrayImgs.doubles(new double[]{1.0, 1.0, 1.0, 1.0, 1.0}, 5);
		Img<LongType> indices = ArrayImgs.longs(new long[]{2L, 5L, 0L, 6L, 9L}, 5);
		Img<LongType> indptr = ArrayImgs.longs(new long[]{0L, 1L, 2L, 3L, 3L, 3L, 3L, 3L, 3L, 5L}, 10);

		sparseImgs = new HashMap<>();
		sparseImgs.put("CSR", new CsrImg<>(10, 9, data, indices, indptr));
		sparseImgs.put("CSC", new CscImg<>(9, 10, data, indices, indptr));
	}

	protected static <T extends Type<T>> void assert2DRaiEquals(RandomAccessibleInterval<T> expected, RandomAccessibleInterval<T> actual) {
		assertEquals("Number of columns not the same.", expected.dimension(0), actual.dimension(0));
		assertEquals("Number of rows not the same.", expected.dimension(1), actual.dimension(1));

		RandomAccess<T> raExpected = expected.randomAccess();
		RandomAccess<T> raActual = actual.randomAccess();
		for (int i = 0; i < expected.dimension(0); ++i)
			for (int j = 0; j < expected.dimension(1); ++j)
				assertEquals("Rai's differ on entry (" + i + "," + j +")", raExpected.setPositionAndGet(i, j), raActual.setPositionAndGet(i, j));
	}
}