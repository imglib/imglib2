package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;

/**
 * Copies a sub interval into a target interval.
 * 
 * @author hornm, University of Konstanz
 * @param <T>
 *                image type
 */
public class Inset<T extends Type<T>, K extends RandomAccessibleInterval<T>>
                implements UnaryOperation<IterableInterval<T>, K> {

        private final long[] m_offset;

        public Inset(long[] offset) {
                m_offset = offset;
        }

        @Override
        public K compute(IterableInterval<T> inset, K res) {
                long[] pos = new long[inset.numDimensions()];

                RandomAccess<T> ra = res.randomAccess();
                for (int d = 0; d < Math.min(res.numDimensions(),
                                m_offset.length); d++) {
                        ra.setPosition(m_offset[d], d);
                }

                Cursor<T> c = inset.localizingCursor();

                while (c.hasNext()) {
                        c.fwd();
                        c.localize(pos);
                        for (int d = 0; d < pos.length; d++) {
                                ra.setPosition(m_offset[d] + pos[d], d);
                        }
                        ra.get().set(c.get());
                }
                return res;
        }

        @Override
        public Inset<T, K> copy() {
                return new Inset<T, K>(m_offset.clone());
        }
}
