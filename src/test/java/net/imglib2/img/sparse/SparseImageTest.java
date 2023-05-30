import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import anndata.CompressedStorageRai;
import anndata.CscRandomAccessibleInterval;
import anndata.CsrRandomAccessibleInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SparseImageTest {

	@Test
	public void CSR_setup_is_correct() {
		CsrRandomAccessibleInterval<DoubleType, LongType> csr = setupCsr();
		assertEquals(2, csr.numDimensions());
		assertArrayEquals(new long[]{0, 0}, csr.minAsLongArray());
		assertArrayEquals(new long[]{9, 8}, csr.maxAsLongArray());
	}

	@ParameterizedTest
	@CsvSource({"2,0", "5,1", "0,2", "6,8", "9,8"})
	public void CSR_nonzero_entries_are_correct(int x, int y) {
		RandomAccess<DoubleType> ra = setupCsr().randomAccess();
		assertEquals(1.0, ra.setPositionAndGet(x,y).getRealDouble(), 1e-6);
	}

	@ParameterizedTest
	@MethodSource("setupSparseImages")
	public void sparse_has_correct_number_of_nonzeros(CompressedStorageRai<DoubleType, LongType> sparse) {
		assertEquals(5, CompressedStorageRai.getNumberOfNonzeros(sparse));
	}

	@ParameterizedTest
	@MethodSource("setupSparseImages")
	public void conversion_to_sparse_is_correct(CompressedStorageRai<DoubleType, LongType> sparse) {
		CompressedStorageRai<DoubleType, LongType> newCsr = CompressedStorageRai.convertToSparse(sparse, 0);
		assertTrue(newCsr instanceof CsrRandomAccessibleInterval);
		TestUtils.assertRaiEquals(sparse, newCsr);
		CompressedStorageRai<DoubleType, LongType> newCsc = CompressedStorageRai.convertToSparse(sparse, 1);
		assertTrue(newCsc instanceof CscRandomAccessibleInterval);
		TestUtils.assertRaiEquals(sparse, newCsc);
	}

	@Test
	public void CSC_is_CSR_transposed() {
		CsrRandomAccessibleInterval<DoubleType, LongType> csr = setupCsr();
		CscRandomAccessibleInterval<DoubleType, LongType> csc = setupCsc();
		TestUtils.assertRaiEquals(csr, Views.permute(csc, 0, 1));
	}

	protected CsrRandomAccessibleInterval<DoubleType, LongType> setupCsr() {
		return (CsrRandomAccessibleInterval<DoubleType, LongType>) setupSparseImages().get(0).getPayload();
	}

	protected CscRandomAccessibleInterval<DoubleType, LongType> setupCsc() {
		return (CscRandomAccessibleInterval<DoubleType, LongType>) setupSparseImages().get(1).getPayload();
	}

	protected static List<Named<CompressedStorageRai<DoubleType, LongType>>> setupSparseImages() {
		Img<DoubleType> data = ArrayImgs.doubles(new double[]{1.0, 1.0, 1.0, 1.0, 1.0}, 5);
		Img<LongType> indices = ArrayImgs.longs(new long[]{2L, 5L, 0L, 6L, 9L}, 5);
		Img<LongType> indptr = ArrayImgs.longs(new long[]{0L, 1L, 2L, 3L, 3L, 3L, 3L, 3L, 3L, 5L}, 10);

		return Arrays.asList(
				named("CSR", new CsrRandomAccessibleInterval<>(10, 9, data, indices, indptr)),
				named("CSC", new CscRandomAccessibleInterval<>(9, 10, data, indices, indptr)));
	}
}