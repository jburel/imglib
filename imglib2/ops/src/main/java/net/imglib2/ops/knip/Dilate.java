package net.imglib2.ops.knip;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.knip.types.ConnectedType;
import net.imglib2.ops.knip.util.BinaryOps;
import net.imglib2.type.logic.BitType;

public final class Dilate<K extends RandomAccessibleInterval<BitType> & IterableInterval<BitType>>
                implements UnaryOperation<K, K> {

        private final int m_neighbourhoodCount;

        private ConnectedType m_type;

        public Dilate(ConnectedType type, final int neighbourhoodCount) {
                m_neighbourhoodCount = neighbourhoodCount;
                m_type = type;
        }

        @Override
        public K compute(K op, K r) {
                return new BinaryOps<K>().dilate(m_type, r, op,
                                m_neighbourhoodCount);
        }

        @Override
        public UnaryOperation<K, K> copy() {
                return new Dilate<K>(m_type, m_neighbourhoodCount);
        }
}
