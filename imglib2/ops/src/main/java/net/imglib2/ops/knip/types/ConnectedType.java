package net.imglib2.ops.knip.types;

/**
 * Neighborhood types.
 * 
 * @author hornm
 */
public enum ConnectedType {
        /**
         * Touching voxels without diagonal neighbors.
         */
        FOUR_CONNECTED,

        /**
         * All touching voxels.
         */
        EIGHT_CONNECTED;

}
