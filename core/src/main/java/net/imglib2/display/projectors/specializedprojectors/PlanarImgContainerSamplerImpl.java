package net.imglib2.display.projectors.specializedprojectors;

import net.imglib2.img.planar.PlanarImg.PlanarContainerSampler;


public class PlanarImgContainerSamplerImpl implements PlanarContainerSampler {

        private int m_currentSliceIndex = -1;

        public PlanarImgContainerSamplerImpl() {

        }

        public PlanarImgContainerSamplerImpl(int startIndex) {
                m_currentSliceIndex = startIndex;
        }

        @Override
        public int getCurrentSliceIndex() {
                return m_currentSliceIndex;
        }

        public int fwd() {
                return m_currentSliceIndex++;
        }

        public void setCurrentSlice(int slice) {
                m_currentSliceIndex = slice;
        }

}
