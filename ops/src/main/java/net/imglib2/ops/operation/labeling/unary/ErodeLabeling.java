/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */
package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.unary.morph.StructuringElementCursor;
import net.imglib2.view.Views;

/**
 * Erode operation on Labeling.
 * 
 * @author Christian Dietz (University of Konstanz)
 * @author Felix Schoenenberger (University of Konstanz)
 * 
 * @param <L>
 */
public class ErodeLabeling<L extends Comparable<L>> implements
		UnaryOperation<Labeling<L>, Labeling<L>> {

	private final long[][] m_struc;

	private final boolean m_labelBased;

	public ErodeLabeling(final long[][] structuringElement) {
		this(structuringElement, true);
	}

	/**
	 * 
	 * @param structuringElement
	 * @param labelBased
	 *            Label-based / binary-based switch.
	 *            <ul>
	 *            <li>Label-based: Each region defined by a label is eroded
	 *            individually. If the label is not present in one of the
	 *            neighbor pixel it is also removed from the center.</li>
	 *            <li>Binary-based: The labeling is treated as a binary image.
	 *            If one of the neighbor pixels is empty the center pixel is
	 *            also set to the empty list.</li>
	 *            </ul>
	 */
	public ErodeLabeling(final long[][] structuringElement,
			final boolean labelBased) {
		m_struc = structuringElement;
		m_labelBased = labelBased;
	}

	@Override
	public Labeling<L> compute(final Labeling<L> input, final Labeling<L> output) {

		if (m_labelBased) {
			return computeLabelBased(input, output);
		}
		return computeBinaryBased(input, output);
	}

	private Labeling<L> computeLabelBased(final Labeling<L> input,
			final Labeling<L> output) {
		final StructuringElementCursor<LabelingType<L>> inStructure = new StructuringElementCursor<LabelingType<L>>(
				Views.extendValue(input, new LabelingType<L>()).randomAccess(),
				m_struc);

		Cursor<LabelingType<L>> outcursor = output.cursor();
		Cursor<LabelingType<L>> inCursor = input.cursor();

		HashSet<L> eroded = new HashSet<L>();

		while (inCursor.hasNext()) {
			List<L> labeling = inCursor.next().getLabeling();
			outcursor.fwd();

			// Make sure that output is also empty where input is empty.
			// we can't assume the input to be empty
			if (labeling.isEmpty()) {
				outcursor.get().setLabeling(labeling);
			}

			inStructure.relocate(inCursor);

			// Clear list for current iteration
			eroded.clear();

			while (inStructure.hasNext()) {
				inStructure.next();
				for (final L label : labeling) {
					// if label was already processed in this structuring
					// element -> ignore
					if (eroded.contains(label))
						continue;

					if (!inStructure.get().getLabeling().contains(label)) {
						// add it to list of already processed labels
						eroded.add(label);

						List<L> newLabels = new ArrayList<L>();
						for (L anyLabel : outcursor.get().getLabeling()) {
							if (anyLabel.compareTo(label) != 0) {
								newLabels.add(anyLabel);
							}
						}

						outcursor.get().setLabeling(newLabels);
					}
				}
			}

			// add label point to output if not eroded
			for (L label : labeling) {
				if (!eroded.contains(label)) {
					addLabel(outcursor.get(), label);
				}
			}
		}

		return output;
	}

	private Labeling<L> computeBinaryBased(final Labeling<L> input,
			final Labeling<L> output) {
		final StructuringElementCursor<LabelingType<L>> inStructure = new StructuringElementCursor<LabelingType<L>>(
				Views.extendValue(input, new LabelingType<L>()).randomAccess(),
				m_struc);
		final Cursor<LabelingType<L>> out = output.localizingCursor();
		next: while (out.hasNext()) {
			out.next();
			inStructure.relocate(out);
			final List<L> center = inStructure.get().getLabeling();
			if (center.isEmpty()) {
				out.get().setLabeling(out.get().getMapping().emptyList());
				continue next;
			}
			while (inStructure.hasNext()) {
				inStructure.next();
				if (inStructure.get().getLabeling().isEmpty()) {
					out.get().setLabeling(out.get().getMapping().emptyList());
					continue next;
				}
			}
			out.get().setLabeling(center);
		}
		return output;
	}

	private void addLabel(final LabelingType<L> type, final L elmnt) {
		if (type.getLabeling().contains(elmnt)) {
			return;
		}
		final List<L> current = type.getLabeling();
		final ArrayList<L> tmp = new ArrayList<L>();
		tmp.addAll(current);
		tmp.add(elmnt);
		type.setLabeling(tmp);
	}

	@Override
	public UnaryOperation<Labeling<L>, Labeling<L>> copy() {
		return new ErodeLabeling<L>(m_struc, m_labelBased);
	}
}
