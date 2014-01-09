




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
import java.util.Iterator;

public class SecondStageIterationHandler {
	
	protected static HashSet<File> fillSet = new HashSet<File>();
	
	
	public static void clear()
	{
		fillSet.clear();
	}
	
	
	public static void performIter( File fi , HashMap<File,HashSet<File>> maps , IClusterHandler hndl ) throws Throwable
	{
		File[] f = fi.listFiles();
		int count;
		for( count = 0 ; count < f.length ; count++ )
		{
			File ff = f[ count ];
			if( ( ff.isFile() ) && ( ff.getName().endsWith( ".jpg" ) ) )
			{
				if( !( fillSet.contains( ff ) ) )
				{
					iterate( ff, maps , hndl , fillSet );
				}
			}
		}
	}
	
	
	protected static void iterate( File fi , HashMap<File,HashSet<File>> maps , IClusterHandler hndl , HashSet<File> fillSet ) throws Throwable
	{
		int prevSz =-1;
		HashSet<File> prevGrp = new HashSet<File>();
		HashSet<File> grp = new HashSet<File>();
		grp.add( fi );
		while( prevSz != prevGrp.size() )
		{
			prevSz = prevGrp.size();
			Iterator<File> ita = grp.iterator();
			while( ita.hasNext() )
			{
				prevGrp.add( ita.next() );
			}
			
			HashSet<File> ngrp = grp;
			
			grp = new HashSet<File>();
			
			ita = ngrp.iterator();
			while( ita.hasNext() )
			{
				HashSet<File> fb = maps.get( ita.next() );
				if( fb != null )
				{
					Iterator<File> itb = fb.iterator();
					while( itb.hasNext() )
					{
						grp.add( itb.next() );
					}
				}
			}
			
		}
		
		hndl.handleCluster( prevGrp );
		
		Iterator<File> ita = prevGrp.iterator();
		while( ita.hasNext() )
		{
			fillSet.add( ita.next() );
		}
	}
	

}

