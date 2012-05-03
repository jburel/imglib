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
 *   4 Jun 2010 (hornm): created
 */
package net.imglib2.ops.knip.labeling;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.knip.ImgUtils;
import net.imglib2.ops.knip.util.ExtendedPolygon;
import net.imglib2.ops.knip.util.PermutationSort;
import net.imglib2.ops.knip.util.PolarImageFactory;
import net.imglib2.ops.knip.util.Signature;
import net.imglib2.ops.knip.util.Vector;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * 
 * @author hornm, University of Konstanz
 */
public class ContourDetector< T extends RealType< T >>
{

	/*
	 * the polar image sources to retrieve the signature and, hence, the contour
	 */
	private PolarImageFactory< T >[] m_polFacs;

	/* the parameters of the detector */
	private int m_numAng; // number of angles

	private double m_maxOverlap; //

	private double m_minScore;

	private int m_minArea;

	private Vector[] m_seedingPoints;

	private final int m_maxLineVariance;

	private final boolean m_smooth;

	/* the results of the contour detection */
	private List< ExtendedPolygon > m_contours = null;

	private List< Double > m_scores;

	private List< Integer > m_models;

	/*
	 * Operation to preprocess the polar image before detection the actual
	 * contour
	 */
	private final UnaryOperation< Img< T >, Img< T >> m_preProc;

	/**
	 * @param pif
	 *            produces the polar images
	 * @param preProc
	 *            a operation for the pre-processing of the polar image before
	 *            the contour is detected (e.g. calculation a gradient), if
	 *            <code>null</code>, no preprocessing will be applied.
	 * @param numAng
	 *            number of angles (sampling lines)
	 * @param seedingPoints
	 *            for each seeding point, a polar image will be created and a
	 *            signature/contour retrieved
	 * @param maxLineVariance
	 * @param maxOverlap
	 * @param minScore
	 * @param minArea
	 * @param smooth
	 */
	public ContourDetector( final PolarImageFactory< T >[] pif, UnaryOperation< Img< T >, Img< T >> preProc, final int numAng, final Vector[] seedingPoints, final int maxLineVariance, final double maxOverlap, final double minScore, final int minArea, boolean smooth )
	{

		m_preProc = preProc;
		// the parameters
		m_maxLineVariance = maxLineVariance;
		m_polFacs = pif;
		m_numAng = numAng;
		m_maxOverlap = maxOverlap;
		m_seedingPoints = seedingPoints;
		m_minScore = minScore;
		m_minArea = minArea;
		m_smooth = smooth;

	}

	/**
	 * Detects the contours according the set parameters.
	 */
	public void detectContours()
	{

		// initalize the result lists
		m_contours = new ArrayList< ExtendedPolygon >( m_seedingPoints.length );
		m_scores = new ArrayList< Double >( m_seedingPoints.length );
		m_models = new ArrayList< Integer >( m_seedingPoints.length );

		Img< T > polImg = null;
		Signature[] signs = new Signature[ m_seedingPoints.length * m_polFacs.length ];
		int i = 0;

		long[] pos = new long[ 2 ];
		Img< T > tmpImg = null;
		for ( int j = 0; j < m_polFacs.length; j++ )
		{

			for ( Vector p : m_seedingPoints )
			{
				pos[ 0 ] = p.getLongPosition( 0 );
				pos[ 1 ] = p.getLongPosition( 1 );
				if ( polImg == null )
				{
					polImg = m_polFacs[ j ].createPolarImage( pos, m_numAng );
				}
				else
				{
					m_polFacs[ j ].createPolarImage( pos, m_numAng, polImg );
				}

				if ( m_preProc != null )
				{
					if ( tmpImg == null )
					{
						tmpImg = ImgUtils.createEmptyImg( polImg );
					}
					m_preProc.compute( polImg, tmpImg );

				}
				else
				{
					tmpImg = polImg;
				}

				// AWTImageTools.showInFrame(tmpImg, "tmp img",
				// 4);
				// AWTImageTools.showInFrame(polImg, "pol img",
				// 4);

				// AWTImageTools.showInFrame(polImg,
				// "pol img partially projected");
				signs[ i ] = new Signature( tmpImg, m_maxLineVariance );
				// AWTImageTools.showInFrame(signs[i].createImage(),
				// "" + signs[i].getScore());
				signs[ i ].setCentre( pos );
				// AWTImageTools.showInFrame(signs[i].createImage(),
				// "signature",
				// 4);
				i++;
			}
		}

		int[] perm = PermutationSort.sort( signs, new SignatureComparator() );

		ExtendedPolygon poly;

		// iterate through all collected signatures and neglect or keep
		// them
		// with respect to the given constraints (area, score, overlap).
		boolean overlap = false;
		for ( int s = 0; s < signs.length; s++ )
		{
			if ( signs[ perm[ s ] ].getArea() < m_minArea )
			{
				continue;
			}
			if ( signs[ perm[ s ] ].getScore() < m_minScore )
			{
				break;
			}

			if ( m_smooth )
			{
				signs[ perm[ s ] ].lowPassFilter( 10 );
			}
			poly = signs[ perm[ s ] ].createPolygon();

			// test overlap
			overlap = false;
			for ( ExtendedPolygon ctest : m_contours )
			{
				if ( overlap( poly, ctest ) > m_maxOverlap )
				{
					overlap = true;
					break;
				}

			}
			if ( !overlap )
			{
				m_contours.add( poly );
				m_scores.add( signs[ perm[ s ] ].getScore() );
				m_models.add( perm[ s ] / m_seedingPoints.length );
			}
		}

	}

	/**
	 * @return the number of detected contours
	 */
	public int getNumDetectedContours()
	{
		contoursDetected();
		return m_contours.size();
	}

	/**
	 * @param idx
	 * @return the contour at the specified index
	 */
	public ExtendedPolygon getContour( int idx )
	{
		contoursDetected();
		return m_contours.get( idx );
	}

	/**
	 * @param contourIdx
	 * @return the score of the contour at the specified index
	 */
	public double getContourScore( int contourIdx )
	{
		contoursDetected();
		return m_scores.get( contourIdx );
	}

	/**
	 * @param contourIdx
	 * @return the model index of the contour at the specified index
	 */
	public int getContourModel( int contourIdx )
	{
		return m_models.get( contourIdx );
	}

	/*
	 * Helper to calculate the overlap of two signatures. 0 - no overlap, 1-
	 * identical
	 */
	private double overlap( final ExtendedPolygon p1, final ExtendedPolygon p2 )
	{
		Rectangle r1 = p1.getBounds();
		Rectangle r2 = p2.getBounds();

		if ( r1.intersects( r2 ) )
		{

			int overlapPix = 0;
			int mask1Pix = 0;
			int mask2Pix = 0;
			Img< BitType > mask1 = p1.createBitmask();
			Img< BitType > mask2 = p2.createBitmask();

			Cursor< BitType > mask1Cur = mask1.localizingCursor();
			RandomAccess< BitType > mask2RA = Views.extendValue( mask2, new BitType( false ) ).randomAccess();

			int[] pos = new int[ 2 ];
			while ( mask1Cur.hasNext() )
			{
				mask1Cur.fwd();
				if ( mask1Cur.get().get() )
				{
					pos[ 0 ] = r1.x + mask1Cur.getIntPosition( 0 ) - r2.x;
					pos[ 1 ] = r1.y + mask1Cur.getIntPosition( 1 ) - r2.y;
					mask2RA.setPosition( pos );
					if ( mask2RA.get().get() )
					{
						overlapPix++;
					}
					mask1Pix++;
				}
			}

			Cursor< BitType > c = mask2.cursor();
			while ( c.hasNext() )
			{
				c.fwd();
				if ( c.get().get() )
				{
					mask2Pix++;
				}
			}

			// Segmentation test = new Segmentation(new int[] { 700,
			// 700 });
			// test.addSegment(new int[] { r1.x, r1.y },
			// c1.createBitmask());
			// test.addSegment(new int[] { r2.x, r2.y },
			// c2.createBitmask());
			//
			// AWTImageTools.showInFrame(AWTImageTools.makeImage(test,
			// SegmentRenderer.MASK_RENDERER));
			// System.out.println((double) overlapPix
			// / Math.min(mask1Pix, mask2Pix));

			return ( double ) overlapPix / Math.min( mask1Pix, mask2Pix );

		}

		return 0;
	}

	/**
	 * Distributes a set of points over an area of the specified width and
	 * height as a regular lattice (with gaps-pixel space in between).
	 * 
	 * @param gaps
	 * @param width
	 * @param height
	 * @return
	 */
	public static Vector[] createLattice( final int gaps, final int width, final int height )
	{
		ArrayList< Vector > res = new ArrayList< Vector >();
		for ( int i = gaps; i < width - gaps + 1; i += gaps )
		{
			for ( int j = gaps; j < height - gaps + 1; j += gaps )
			{
				res.add( new Vector( new long[] { i, j } ) );
			}
		}
		return res.toArray( new Vector[ res.size() ] );
	}

	private class SignatureComparator implements Comparator< Signature >
	{

		/**
		 * {@inheritDoc}
		 */
		public int compare( final Signature arg0, final Signature arg1 )
		{
			return ( int ) Math.round( arg1.getScore() * 1000 - arg0.getScore() * 1000 );
		}

	}

	private void contoursDetected()
	{
		if ( m_contours == null ) { throw new IllegalStateException( "Call \"detectContours\" first!" ); }
	}

}
