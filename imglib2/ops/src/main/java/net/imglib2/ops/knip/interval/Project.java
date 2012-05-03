/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   30 Dec 2010 (hornm): created
 */
package net.imglib2.ops.knip.interval;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;

/**
 * Img projection.
 * 
 * @author dietzc, hornm, University of Konstanz
 */
public class Project< T extends RealType< T >> implements UnaryOutputOperation< Img< T >, Img< T >>
{

	/**
	 * Different projection types which can be used by ImageJ
	 */
	public enum ProjectionType
	{
		MAX_INTENSITY, MEDIAN_INTENSITY, AVG_INTENSITY, MIN_INTENSITY;
	}

	/* Type of projection */
	private final ProjectionType m_projectionType;

	/* Dimension of projection */
	private final int m_projectionDim;

	/**
	 * Projects the pixels onto all dimensions in the direction of
	 * <code>projectionDim</code>
	 * 
	 * @param type
	 * @param imgFactory
	 * @param projectionDim
	 */
	public Project( ProjectionType type, int projectionDim )
	{
		m_projectionDim = projectionDim;
		m_projectionType = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Img< T > createEmptyOutput( Img< T > op )
	{
		/* The new dimensions of the projected image */
		long[] projectedImgDimSizes = new long[ op.numDimensions() - 1 ];
		for ( int d = 0; d < op.numDimensions(); d++ )
		{
			if ( d < m_projectionDim )
			{
				projectedImgDimSizes[ d ] = op.dimension( d );
			}
			if ( d > m_projectionDim )
			{
				projectedImgDimSizes[ d - 1 ] = op.dimension( d );
			}
		}

		/* The projected Image */
		Img< T > projectedImage = op.factory().create( projectedImgDimSizes, op.randomAccess().get().createVariable() );

		return projectedImage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Img< T > compute( Img< T > op, Img< T > r )
	{

		Cursor< T > projCur = r.localizingCursor();
		RandomAccess< T > srcRA = op.randomAccess();

		double[] projVec = new double[ ( int ) op.dimension( m_projectionDim ) ];
		while ( projCur.hasNext() )
		{
			projCur.fwd();
			for ( int d = 0; d < op.numDimensions(); d++ )
			{
				if ( d < m_projectionDim )
				{
					srcRA.setPosition( projCur.getIntPosition( d ), d );
				}
				if ( d > m_projectionDim )
				{
					srcRA.setPosition( projCur.getIntPosition( d - 1 ), d );
				}
			}
			for ( int projPos = 0; projPos < op.dimension( m_projectionDim ); projPos++ )
			{
				srcRA.setPosition( projPos, m_projectionDim );
				projVec[ projPos ] = srcRA.get().getRealDouble();
			}

			projCur.get().setReal( handleProjection( projVec ) );

		}
		return r;
	}

	/*
	 * Projection is handled according to the projection type m_d has the size
	 * same size as the amount of points which are projected
	 * 
	 * @param resultList
	 * 
	 * @param type
	 */
	private final double handleProjection( double[] resultList )
	{

		switch ( m_projectionType )
		{
		case AVG_INTENSITY:
			double sum = 0;
			for ( int i = 0; i < resultList.length; i++ )
			{
				sum += resultList[ i ];
			}
			return sum / resultList.length;

		case MEDIAN_INTENSITY:

			Arrays.sort( resultList );
			return resultList[ resultList.length / 2 ];

		case MAX_INTENSITY:
			double max = Double.NEGATIVE_INFINITY;

			for ( int i = 0; i < resultList.length; i++ )
			{
				max = Math.max( max, resultList[ i ] );
			}

			return max;

		case MIN_INTENSITY:
			double min = Double.POSITIVE_INFINITY;

			for ( int i = 0; i < resultList.length; i++ )
			{
				min = Math.min( min, resultList[ i ] );
			}

			return min;

		default:
			throw new IllegalArgumentException( "Projection Method doesn't exist" );
		}
	}

	@Override
	public UnaryOutputOperation< Img< T >, Img< T >> copy()
	{
		return new Project< T >( m_projectionType, m_projectionDim );
	}

	@Override
	public Img< T > compute( Img< T > arg0 )
	{
		return compute( arg0, createEmptyOutput( arg0 ) );
	}

}
