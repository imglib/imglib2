package net.imglib2.algorithm.features.seg;

import net.imglib2.img.Img;
import net.imglib2.ops.img.BinaryOperationAssignment;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.unary.morph.Dilate;
import net.imglib2.ops.operation.randomaccessibleinterval.unary.morph.Erode;
import net.imglib2.ops.operation.real.binary.RealXor;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.type.logic.BitType;

/**
 * Extracts the outline of a given connected component in an {@link Img} of
 * {@link BitType}. The outline is here defined as all pixels, which are next to
 * the pixels which are on the border of the connected component. Please be
 * aware that for a correct calculation of the Perimeter only one connected
 * component should be contained in the {@link Img} of {@link BitType}
 * 
 * @author Christian Dietz
 * 
 */
public class ExtractOutlineImg implements
		UnaryOperation<Img<BitType>, Img<BitType>> {

	private final BinaryOperationAssignment<BitType, BitType, BitType> m_imgManWith;

	private final UnaryOperation<Img<BitType>, Img<BitType>> m_op;

	private final boolean m_outlineInsideSegment;

	public ExtractOutlineImg(final boolean outlineInsideSegment) {
		m_outlineInsideSegment = outlineInsideSegment;
		m_imgManWith = new BinaryOperationAssignment<BitType, BitType, BitType>(
				new RealXor<BitType, BitType, BitType>());
		m_op = m_outlineInsideSegment ? new Erode<Img<BitType>>(
				ConnectedType.EIGHT_CONNECTED, 1) : new Dilate<Img<BitType>>(
				ConnectedType.FOUR_CONNECTED, 1);
	}

	@Override
	public Img<BitType> compute(final Img<BitType> op, final Img<BitType> r) {
		if (op.numDimensions() != 2) {
			throw new IllegalArgumentException(
					"Operation only permitted on two dimensions");
		}

		// This produces black results
		// if (!m_outlineInsideSegment) {
		// new ExpandAndCenterImg<BitType, K>(1).compute(op, op);
		// }

		m_op.compute(op, r);
		m_imgManWith.compute(op, r, r);
		return r;
	}

	@Override
	public UnaryOperation<Img<BitType>, Img<BitType>> copy() {
		return new ExtractOutlineImg(m_outlineInsideSegment);
	}
}
