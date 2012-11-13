package net.imglib2.algorithm.convolver.filter.linear;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Pair;

/**
 * Constant kernels of image filters.
 *
 * @author Stephan Sellien, University of Konstanz
 */
@SuppressWarnings("unchecked")
public enum ConstantFilter {
        /**
         * Sobel filter.
         */
        Sobel("Sobel", new Pair<String, int[][]>("X", new int[][] {
                        { 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 } }),
                        new Pair<String, int[][]>("Y", new int[][] {
                                        { 1, 2, 1 }, { 0, 0, 0 },
                                        { -1, -2, -1 } })),
        /**
         * Sharpening filter (discrete Laplacian).
         */
        Laplacian("Laplacian", new Pair<String, int[][]>("normal", new int[][] {
                        { 0, 1, 0 }, { 1, -4, 1 }, { 0, 1, 0 } }),
                        new Pair<String, int[][]>("diagonal",
                                        new int[][] { { 1, 1, 1 },
                                                        { 1, -8, 1 },
                                                        { 1, 1, 1 } })), /**
         *
         *
         *
         *
         * Prewitt operator.
         */
        Prewitt("Prewitt", new Pair<String, int[][]>("X", new int[][] {
                        { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } }),
                        new Pair<String, int[][]>("Y", new int[][] {
                                        { -1, -1, -1 }, { 0, 0, 0 },
                                        { 1, 1, 1 } })),
        /**
         * Roberts operator.
         */
        Roberts("Roberts", new Pair<String, int[][]>("X", new int[][] {
                        { -1, 0 }, { 0, 1 } }), new Pair<String, int[][]>("Y",
                        new int[][] { { 0, -1 }, { 1, 0 } })),
        /** Kirsch-Operator (http://en.wikipedia.org/wiki/Kirsch_operator) */
        Kirsch("Kirsch", new Pair<String, int[][]>("1", new int[][] {
                        { 5, 5, 5 }, { -3, 0, -3 }, { -3, -3, -3 } }),
                        new Pair<String, int[][]>("2", new int[][] {
                                        { 5, 5, -3 }, { 5, 0, -3 },
                                        { -3, -3, -3 } }),
                        new Pair<String, int[][]>("3", new int[][] {
                                        { 5, -3, -3 }, { 5, 0, -3 },
                                        { 5, -3, -3 } }),
                        new Pair<String, int[][]>("4", new int[][] {
                                        { -3, -3, -3 }, { 5, 0, -3 },
                                        { 5, 5, -3 } }),

                        new Pair<String, int[][]>("5", new int[][] {
                                        { -3, -3, -3 }, { -3, 0, -3 },
                                        { 5, 5, 5 } }),
                        new Pair<String, int[][]>("6", new int[][] {
                                        { -3, -3, -3 }, { -3, 0, 5 },
                                        { -3, 5, 5 } }),

                        new Pair<String, int[][]>("7", new int[][] {
                                        { -3, -3, 5 }, { -3, 0, 5 },
                                        { -3, -3, 5 } }),
                        new Pair<String, int[][]>("8", new int[][] {
                                        { -3, 5, 5 }, { -3, 0, 5 },
                                        { -3, -3, -3 } })),
        /** Frei&Chen (http://www.roborealm.com/help/Frei_Chen.php) */
        FreiAndChen("Frei & Chen", new Pair<String, int[][]>("1", new int[][] {
                        { 2, 3, 4 }, { 0, 0, 0 }, { -2, -3, -2 } }),
                        new Pair<String, int[][]>("2", new int[][] {
                                        { 2, 0, -2 }, { 0, -2, 3 },
                                        { 3, -2, 0 } }),
                        new Pair<String, int[][]>("3", new int[][] {
                                        { 3, 0, -3 }, { 2, 0, -2 },
                                        { -2, 0, 2 } }),
                        new Pair<String, int[][]>("4", new int[][] {
                                        { 2, 0, -2 }, { -3, 2, 0 },
                                        { 0, 2, -3 } }));

        private Pair<String, int[][]>[] m_variants;
        private String m_displayName;

        private ConstantFilter(String displayName,
                        Pair<String, int[][]>... variants) {
                m_displayName = displayName;
                m_variants = variants;
        }

        // convenience
        private ConstantFilter(String displayName, int[][] matrix) {
                this(displayName,
                                new Pair<String, int[][]>(displayName, matrix));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
                return m_displayName;
        }

        /**
         * Creates the kernel image for the given variant.
         *
         * @param variantName
         *                the name of the variant
         * @return the filter
         */
        public Img<DoubleType> createImage(String variantName) {
                for (int i = 0; i < m_variants.length; i++) {
                        if (m_variants[i].a.equals(variantName)) {
                                return createImage(i);
                        }
                }
                throw new IllegalArgumentException(
                                "Variant name must be valid.");
        }

        /**
         * Creates an {@link Img} for the selected filter containing its kernel.
         *
         * @param index
         *                index of the variant
         *
         * @return the kernel as {@link Img}
         */
        public Img<DoubleType> createImage(int index) {
                if (index < 0 || index >= m_variants.length) {
                        throw new IllegalArgumentException(
                                        "Index must be valid.");
                }
                int[][] matrix = m_variants[index].b;
                Img<DoubleType> img = new ArrayImgFactory<DoubleType>().create(
                                new long[] { matrix.length, matrix[0].length },
                                new DoubleType());
                RandomAccess<DoubleType> ra = img.randomAccess();
                for (int y = 0; y < matrix.length; y++) {
                        for (int x = 0; x < matrix[y].length; x++) {
                                ra.setPosition(new int[] { x, y });
                                ra.get().set(matrix[y][x]);
                        }
                }
                return img;
        }

        /**
         * Get the names of the variants.
         *
         * @return the names
         */
        public String[] getVariantNames() {
                String[] variantNames = new String[m_variants.length];
                for (int i = 0; i < m_variants.length; i++) {
                        variantNames[i] = m_variants[i].a;
                }
                return variantNames;
        }
}
