


//$$strtCprt
/**
* ImageStitch -- Image Stitching Program
* 
* Copyright (C) 2012 Thornton Green
* 
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with this program; if not, 
* see <http://www.gnu.org/licenses>.
* Additional permission under GNU GPL version 3 section 7
*
* If you modify this Program, or any covered work, by linking or combining it with metadata-extractor/xmpcore 
* (or a modified version of that library), containing parts covered by the terms of the Apache License, 
* the licensors of this Program grant you additional permission to convey the resulting work. {Corresponding Source for
* a non-source form of such a combination shall include the source code for the parts of metadata-extractor/xmpcore 
* used as well as that of the covered work.}
* 
*
*
* metadata-extractor and xmpcore are available at http://code.google.com/p/metadata-extractor/
* 
*
*/
//$$endCprt





package pano;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;


public class TraversingAssocHandler implements IAssociationHandler {
	
	
	protected HashMap<File,HashSet<File>> maps = new HashMap<File,HashSet<File>>( 50 );

	
	@Override
	public void handleAssociation(File fa, File fb) throws Throwable {
		handleAdd( fa , fb );
		handleAdd( fb , fa );
		System.out.print( "*" );
	}
	
	
	protected void handleAdd( File fa , File fb )
	{
		HashSet<File> fc = maps.get( fa );
		if( fc != null )
		{
			fc.add( fb );
		}
		else
		{
			fc = new HashSet<File>();
			fc.add( fb );
			maps.put( fa , fc );
		}
	}
	
	
	public HashMap<File,HashSet<File>> getMaps()
	{
		return( maps );
	}

	
}

