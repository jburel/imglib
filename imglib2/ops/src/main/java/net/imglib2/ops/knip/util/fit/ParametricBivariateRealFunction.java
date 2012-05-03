package net.imglib2.ops.knip.util.fit;

public interface ParametricBivariateRealFunction {
        /**
         * Compute the value of the function.
         * 
         * @param x
         *                first coordinate of point for which the function value
         *                should be computed.
         * @param y
         *                second coordinate of point for which the function
         *                value should be computed.
         * @param parameters
         *                Function parameters.
         * @return the value.
         */
        double value(double x, double y, double... parameters);

        /**
         * Compute the gradient of the function with respect to its parameters.
         * 
         * @param x
         *                first coordinate of point for which the gradient
         *                should be computed.
         * @param y
         *                second coordinate of point for which the gradient
         *                should be computed.
         * @param parameters
         *                Function parameters.
         * @return the value.
         */
        double[] gradient(double x, double y, double... parameters);
}
