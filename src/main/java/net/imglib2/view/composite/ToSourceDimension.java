package net.imglib2.view.composite;

/**
 * @author Philipp Hanslovsky
 */
public interface ToSourceDimension {

    public int toSourceDimension( int d );

    public static class Identity implements ToSourceDimension {

        @Override
        public int toSourceDimension(int d) {
            return d;
        }
    }

    public static class AddOne implements ToSourceDimension {

        @Override
        public int toSourceDimension(int d) {
            return d + 1;
        }
    }

    public static class AddOneIfGreaterOrEqual implements ToSourceDimension {

        private final int dimension;

        public AddOneIfGreaterOrEqual( int dimension) {
            this.dimension = dimension;
        }

        @Override
        public int toSourceDimension( int d ) {
            return d >= this.dimension ? d + 1 : d;
        }
    }

}
