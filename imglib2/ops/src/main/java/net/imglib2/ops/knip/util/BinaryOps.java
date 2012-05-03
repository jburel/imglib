package net.imglib2.ops.knip.util;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.ops.knip.types.ConnectedType;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

/**
 * 
 * @author schoenen
 */
public class BinaryOps<K extends RandomAccessibleInterval<BitType> & IterableInterval<BitType>> {

        public K erode(ConnectedType type, final K r, final K op,
                        final int count) {
                return binaryop(type, r, op, true, count);
        }

        public K dilate(ConnectedType type, final K r, final K op,
                        final int count) {
                return binaryop(type, r, op, false, count);
        }

        private K binaryop(ConnectedType type, final K r, final K op,
                        final boolean erode, final int count) {
                long[] dim = new long[r.numDimensions()];
                r.dimensions(dim);
                switch (r.numDimensions()) {
                case 2:
                        switch (type) {
                        case EIGHT_CONNECTED:
                                unrolled2DEightConnected(r, op, erode, count);
                                break;
                        case FOUR_CONNECTED:
                                unrolled2DFourConnected(r, op, erode, count);
                                break;
                        default:
                                throw new IllegalArgumentException(
                                                "Can't find ConnectionType. Please choose between for connected and eightconnected");
                        }

                        break;
                default:
                        switch (type) {
                        case EIGHT_CONNECTED:
                                nDEightConnected(r, op, dim, erode, count);
                                break;
                        case FOUR_CONNECTED:
                                nDFourConnected(r, op, dim, erode, count);
                                break;
                        default:
                                throw new IllegalArgumentException(
                                                "Can't find ConnectionType. Please choose between for connected and eightconnected");
                        }

                        break;
                }
                return r;
        }

        private void unrolled2DFourConnected(final K r, final K op,
                        final boolean erode, final int count) {

                Cursor<BitType> resCur = r.cursor();
                Cursor<BitType> srcCur = op.localizingCursor();
                RandomAccess<BitType> srcRA = Views.extendValue(op,
                                new BitType(false)).randomAccess();

                int c = 0;
                int[] pos = new int[op.numDimensions()];

                while (srcCur.hasNext()) {
                        srcCur.fwd();
                        resCur.fwd();

                        boolean t = srcCur.get().get();

                        if (erode == t) {
                                srcRA.setPosition(srcCur);
                                srcRA.localize(pos);
                                c = 0;

                                // first row
                                srcRA.setPosition(pos[0] - 1, 0);
                                if (srcRA.get().get())
                                        c++;
                                srcRA.setPosition(pos[0] + 1, 0);
                                if (srcRA.get().get())
                                        c++;

                                // second row
                                srcRA.setPosition(pos[0], 0);
                                srcRA.setPosition(pos[1] - 1, 1);
                                if (srcRA.get().get())
                                        c++;
                                srcRA.setPosition(pos[1] + 1, 1);
                                if (srcRA.get().get())
                                        c++;

                                if (erode) {
                                        resCur.get().set(4 - c < count);
                                } else {
                                        resCur.get().set(c >= count);
                                }
                        } else {
                                resCur.get().set(t);
                        }
                }
        }

        private void unrolled2DEightConnected(final K r, final K op,
                        final boolean erode, final int count) {

                Cursor<BitType> resCur = r.cursor();
                Cursor<BitType> srcCur = op.localizingCursor();
                RandomAccess<BitType> srcRA = Views.extendValue(op,
                                new BitType(false)).randomAccess();

                int c = 0;
                int[] pos = new int[op.numDimensions()];

                while (srcCur.hasNext()) {
                        srcCur.fwd();
                        resCur.fwd();

                        boolean t = srcCur.get().get();

                        if (erode == t) {
                                srcRA.setPosition(srcCur);
                                srcRA.localize(pos);
                                c = 0;

                                // first row
                                srcRA.setPosition(pos[1] - 1, 1);
                                srcRA.setPosition(pos[0] - 1, 0);
                                if (srcRA.get().get())
                                        c++;
                                srcRA.setPosition(pos[0], 0);
                                if (srcRA.get().get())
                                        c++;
                                srcRA.setPosition(pos[0] + 1, 0);
                                if (srcRA.get().get())
                                        c++;

                                // second row
                                srcRA.setPosition(pos[1], 1);
                                srcRA.setPosition(pos[0] - 1, 0);
                                if (srcRA.get().get())
                                        c++;
                                srcRA.setPosition(pos[0], 0);
                                if (srcRA.get().get())
                                        c++;
                                srcRA.setPosition(pos[0] + 1, 0);
                                if (srcRA.get().get())
                                        c++;

                                // third row
                                srcRA.setPosition(pos[1] + 1, 1);
                                srcRA.setPosition(pos[0] - 1, 0);
                                if (srcRA.get().get())
                                        c++;
                                srcRA.setPosition(pos[0], 0);
                                if (srcRA.get().get())
                                        c++;
                                srcRA.setPosition(pos[0] + 1, 0);
                                if (srcRA.get().get())
                                        c++;

                                if (erode) {
                                        resCur.get().set(9 - c < count);
                                } else {
                                        resCur.get().set(c >= count);
                                }
                        } else {
                                resCur.get().set(t);
                        }
                }
        }

        private void nDEightConnected(final K res, final K src,
                        final long[] dim, final boolean erode, final int count) {

                RandomAccess<BitType> r = Views.extendValue(res,
                                new BitType(false)).randomAccess();
                RandomAccess<BitType> op = Views.extendValue(src,
                                new BitType(false)).randomAccess();

                final int kernelSize = (int) Math.pow(3, dim.length);
                final int[] kernel = new int[kernelSize];
                int kernelIndex;
                int x0;
                int sum;
                int i;
                // Setup line cursor, points on the first pixel in each line
                final long[] ldim = dim.clone();
                ldim[0] = 1;
                IntervalIterator lineCur = new IntervalIterator(ldim);
                // Region of interest cursor, iterates over the plane with the
                // new pixel
                // values
                final long[] roi = new long[dim.length];
                Arrays.fill(roi, 3);
                roi[0] = 1;
                IntervalIterator roiCur = new IntervalIterator(roi);
                while (lineCur.hasNext()) {
                        lineCur.fwd();
                        // Initialize kernel in new line
                        kernelIndex = 0;
                        sum = 0;
                        for (x0 = -2; x0 < 0; x0++) {
                                op.setPosition(x0, 0);
                                roiCur.reset();
                                while (roiCur.hasNext()) {
                                        roiCur.fwd();
                                        for (i = 1; i < dim.length; i++) {
                                                op.setPosition(lineCur
                                                                .getLongPosition(i)
                                                                + roiCur.getLongPosition(i)
                                                                - 1, i);
                                        }
                                        kernel[kernelIndex] = op.get()
                                                        .getInteger();
                                        sum += kernel[kernelIndex];
                                        kernelIndex = (kernelIndex + 1)
                                                        % kernel.length;
                                }
                        }
                        // Go through the line
                        for (x0 = 0; x0 < dim[0]; x0++) {
                                op.setPosition(x0 + 1, 0);
                                roiCur.reset();
                                while (roiCur.hasNext()) {
                                        roiCur.fwd();
                                        for (i = 1; i < dim.length; i++) {
                                                op.setPosition(lineCur
                                                                .getLongPosition(i)
                                                                + roiCur.getLongPosition(i)
                                                                - 1, i);
                                        }
                                        sum -= kernel[kernelIndex];
                                        kernel[kernelIndex] = op.get()
                                                        .getInteger();
                                        sum += kernel[kernelIndex];
                                        kernelIndex = (kernelIndex + 1)
                                                        % kernel.length;
                                }
                                r.setPosition(x0, 0);
                                for (i = 1; i < dim.length; i++) {
                                        r.setPosition(lineCur
                                                        .getLongPosition(i), i);
                                }
                                if (erode) {
                                        r.get().set((kernelSize - sum) < count);
                                } else {
                                        r.get().set(sum >= count);
                                }
                        }
                }
        }

        private void nDFourConnected(final K r, final K op, final long[] dim,
                        final boolean erode, final int count) {
                Cursor<BitType> resCur = r.cursor();
                Cursor<BitType> srcCur = op.localizingCursor();
                RandomAccess<BitType> srcRA = Views.extendValue(op,
                                new BitType(false)).randomAccess();

                int c = 0;
                int[] pos = new int[op.numDimensions()];
                int kernelsize = dim.length * 2;

                while (srcCur.hasNext()) {
                        srcCur.fwd();
                        resCur.fwd();

                        boolean t = srcCur.get().get();

                        if (erode == t) {
                                srcRA.setPosition(srcCur);
                                srcRA.localize(pos);
                                c = 0;
                                for (int i = 0; i < dim.length; i++) {
                                        srcRA.setPosition(pos[i] - 1, i);
                                        if (srcRA.get().get())
                                                c++;
                                        srcRA.setPosition(pos[i] + 1, i);
                                        if (srcRA.get().get())
                                                c++;
                                        srcRA.setPosition(pos[i], i);
                                }
                                if (erode) {
                                        resCur.get()
                                                        .set(kernelsize - c < count);
                                } else {
                                        resCur.get().set(c >= count);
                                }
                        } else {
                                resCur.get().set(t);
                        }
                }
        }
}
