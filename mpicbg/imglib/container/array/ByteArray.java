/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.container.array;

import mpicbg.imglib.container.basictypecontainer.ByteContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class ByteArray<T extends Type<T>> extends Array<T> implements ByteContainer<T>
{
	protected byte data[];

	public ByteArray( ArrayContainerFactory factory, int[] dim, final int entitiesPerPixel )
	{
		super( factory, dim, entitiesPerPixel );
		this.data = new byte[ this.numEntities ];
	}

	@Override
	public void close() { data = null; }

	@Override
	public byte[] getCurrentStorageArray( Cursor<?> c ) { return data; }
}
