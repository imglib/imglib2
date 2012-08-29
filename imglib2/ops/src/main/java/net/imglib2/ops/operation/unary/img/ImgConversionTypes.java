package net.imglib2.ops.operation.unary.img;

public enum ImgConversionTypes {
        DIRECT("Copy"), DIRECTCLIP("Clip"), SCALE("Scale"), NORMALIZESCALE(
                        "Normalize and scale"), NORMALIZEDIRECT("Normalize"), NORMALIZEDIRECTCLIP(
                        "Normalize (clipped)");

        private final String m_label;

        public static String[] labelsAsStringArray() {
                ImgConversionTypes[] types = ImgConversionTypes.values();
                String[] res = new String[types.length];
                for (int i = 0; i < res.length; i++) {
                        res[i] = types[i].getLabel();
                }

                return res;

        }

        /**
         * @param label
         * @return the conversion type for the label, null, if doesn't match any
         */
        public static ImgConversionTypes getByLabel(String label) {
                for (ImgConversionTypes t : ImgConversionTypes.values()) {
                        if (t.getLabel().equals(label)) {
                                return t;
                        }
                }
                return null;
        }

        private ImgConversionTypes(String label) {
                m_label = label;
        }

        public String getLabel() {
                return m_label;
        }

}
