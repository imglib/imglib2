package net.imglib2.algorithm.convolver.filter.linear;

///*
// * ------------------------------------------------------------------------
// *
// *  Copyright (C) 2003 - 2010
// *  University of Konstanz, Germany and
// *  KNIME GmbH, Konstanz, Germany
// *  Website: http://www.knime.org; Email: contact@knime.org
// *
// *  This program is free software; you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License, Version 3, as
// *  published by the Free Software Foundation.
// *
// *  This program is distributed in the hope that it will be useful, but
// *  WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with this program; if not, see <http://www.gnu.org/licenses>.
// *
// *  Additional permission under GNU GPL version 3 section 7:
// *
// *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
// *  Hence, KNIME and ECLIPSE are both independent programs and are not
// *  derived from each other. Should, however, the interpretation of the
// *  GNU GPL Version 3 ("License") under any applicable laws result in
// *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
// *  you the additional permission to use and propagate KNIME together with
// *  ECLIPSE with only the license terms in place for ECLIPSE applying to
// *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
// *  license terms of ECLIPSE themselves allow for the respective use and
// *  propagation of ECLIPSE together with KNIME.
// *
// *  Additional permission relating to nodes for KNIME that extend the Node
// *  Extension (and in particular that are based on subclasses of NodeModel,
// *  NodeDialog, and NodeView) and that only interoperate with KNIME through
// *  standard APIs ("Nodes"):
// *  Nodes are deemed to be separate and independent programs and to not be
// *  covered works.  Notwithstanding anything to the contrary in the
// *  License, the License does not apply to Nodes, you are not required to
// *  license Nodes under the License, and you are granted a license to
// *  prepare and propagate Nodes, in each case even if such Nodes are
// *  propagated with or for interoperation with KNIME. The owner of a Node
// *  may freely choose the license terms applicable to such Node, including
// *  when such Node is propagated with or for interoperation with KNIME.
// * ------------------------------------------------------------------------
// * 
// * History
// *   2 Dec 2010 (hornm): created
// */
//package org.imalib.filter.linear;
//
//import net.imglib2.img.Img;
//import net.imglib2.type.numeric.RealType;
//import net.imglib2.type.numeric.complex.ComplexFloatType;
//import net.imglib2.type.numeric.real.FloatType;
//
//import org.imalib.awt.converter.AWTImageTools;
//import org.imalib.ops.ImgMap;
//import org.imalib.ops.type.GetComplex;
//import org.imalib.ops.type.GetReal;
//import org.imalib.util.SSTImageTools;
//import org.knime.ip.logic.operations2.operation.ImgCopy;
//import org.knime.ip.logic.operations2.operation.ImgNormalize;
//
///**
// * 
// * @author hornm, University of Konstanz
// */
//public class ConvolutionTest {
//
//	public static <T extends RealType<T>> void main(String[] args)
//			throws Exception {
//
//		// int r = 20;
//		// float theta = (float) Math.PI * 22.0f / 5.0f;
//		// float theta = 0;
//		float scale = .5f;
//		float freq = 1;
//		// float elong = 1;
//
//		// org.knime.ip.logic.linearfilter2d.DerivativeOfGaussian imglibDOG =
//		// new org.knime.ip.logic.linearfilter2d.DerivativeOfGaussian(
//		// 20, theta, 1.5f, 1);
//		//
//		// show(imglibDOG, "imglib dog");
//		//
//		// shared.image.filter.DerivativeOfGaussian sstDOG = new
//		// shared.image.filter.DerivativeOfGaussian(
//		// 20, theta, 1.5, 1);
//		// show(SSTImageTools.makeFloatImage(sstDOG), "sst dog");
//
//		// org.knime.ip.logic.linearfilter2d.LaplacianOfGaussian imglibLOG = new
//		// org.knime.ip.logic.linearfilter2d.LaplacianOfGaussian(
//		// 20, theta);
//		// show(imglibLOG, "imglib log");
//		//
//		// shared.image.filter.LaplacianOfGaussian sstLOG = new
//		// shared.image.filter.LaplacianOfGaussian(
//		// 20, theta);
//		// show(SSTImageTools.makeFloatImage(sstLOG), "sst log");
//
//		org.imalib.filter.linear.GaborCircular imglibGC = new org.imalib.filter.linear.GaborCircular(
//				20, scale, freq);
//		show(new ImgMap<ComplexFloatType, FloatType>(
//				new GetReal<ComplexFloatType, FloatType>(new FloatType()))
//				.compute(imglibGC),
//				"imglib real gabor circular");
//		show(new ImgMap<ComplexFloatType, FloatType>(
//				new GetComplex<ComplexFloatType, FloatType>(new FloatType()))
//				.compute(imglibGC),
//				"imglib complex gabor circular");
//
//		shared.image.filter.GaborCircular sstGC = new shared.image.filter.GaborCircular(
//				20, scale, freq);
//		Img<ComplexFloatType> tmp = SSTImageTools.toComplexImage(sstGC);
//		show(new ImgMap<ComplexFloatType, FloatType>(
//				new GetReal<ComplexFloatType, FloatType>(new FloatType()))
//				.compute(tmp),
//				"sst real gabor circular");
//		show(new ImgMap<ComplexFloatType, FloatType>(
//				new GetComplex<ComplexFloatType, FloatType>(new FloatType()))
//				.compute(tmp),
//				"sst complex gabor circular");
//
//		// imglib
//		// org.knime.ip.logic.filter.linear.Gabor imglibG = new
//		// org.knime.ip.logic.filter.linear.Gabor(
//		// r, theta, scale, freq, elong);
//		// show(new GetReal<ComplexFloatType>().proc(imglibG),
//		// "imglib real gabor");
//		// show(new GetComplex<ComplexFloatType>().proc(imglibG),
//		// "imglib complex gabor");
//
//		// sst
//		// shared.image.filter.Gabor sstG = new shared.image.filter.Gabor(r,
//		// theta, scale, freq, elong);
//		// Image<ComplexFloatType> tmp = SSTImageTools.makeComplexImage(sstG);
//		// show(new GetReal<ComplexFloatType>().proc(tmp),
//		// "sst real gabor");
//		// show(new GetComplex<ComplexFloatType>().proc(tmp),
//		// "sst complex gabor");
//
//		// fft
//		// Img<T> img = loadImage("/home/hornm/cell_images/rect5201.ome.tif");
//		//
//		// int[] size = new int[2];
//		// size[0] = img.getDimension(0);
//		// size[1] = img.getDimension(1);
//		// img = ImageTools.removeOneSizedDims(img);
//		//
//		// ImageConvolution<T, FloatType> imglibICS = new
//		// ImgLibImageConvolution<T, FloatType>(
//		// img, new GetReal<ComplexFloatType>().proc(imglibG));
//		// show(imglibICS.convolve(), "convolved imglib real");
//		// // imglibICS.setKernel(new
//		// GetComplex<ComplexFloatType>().proc(imglibG)
//		// // );
//		// // show(imglibICS.convolve(), "convolved imglib complex");
//		//
//		//
//		// Img<FloatType> rekernel = new ImgMap<ComplexFloatType, FloatType>(
//		// new GetReal<ComplexFloatType, FloatType>(new FloatType()))
//		// .compute(imglibG);
//		// Img<FloatType> complkernel = new ImgMap<ComplexFloatType, FloatType>(
//		// new GetComplex<ComplexFloatType, FloatType>(new FloatType()))
//		// .compute(imglibG);
//
//		// ImageConvolution<T, FloatType> sstICS = new SSTImageConvolution<T,
//		// FloatType>(
//		// rekernel);
//
//		// show(sstICS.compute(img), "convolved sst real");
//		//
//		// sstICS.setKernel(complkernel);
//		//
//		// show(sstICS.compute(img), "convolved sst complex");
//		// // //
//		// // ImageConvolution<T, FloatType> directICS = new
//		// DirectImageConvolution<T, FloatType>(
//		// // rekernel);
//		//
//		// show(directICS.compute(img), "convolved direct real");
//		//
//		// directICS.setKernel(complkernel);
//		//
//		// show(directICS.compute(img), "convolved direct complex");
//
//	}
//
//	private static <T extends RealType<T>> void show(Img<T> img, String title) {
//		Img<T> tmp = new ImgCopy<T>().compute(img);
//		new ImgNormalize<T>(0).manipulate(tmp);
//
//		AWTImageTools.showInFrame(tmp, title, 5);
//	}
//
//	// private static <T extends RealType<T>> Img<T> loadImage(final String img)
//	// throws Exception {
//	// FileImageSource source = new FileImageSource("", new ArrayImgFactory());
//	// ImageReference ref = new ImageReference("test", "", img);
//	// return source.getImg(ref, new FinalInterval(source.getDimensions(ref)));
//	// }
// }
