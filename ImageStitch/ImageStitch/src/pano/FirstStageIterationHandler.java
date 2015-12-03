




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

public class FirstStageIterationHandler {
	
	public static final long del0 = 30000;
	public static final long del1 = del0 /2;
	
	static HashMap<Long,HashSet<File>> m0 = new HashMap<Long,HashSet<File>>();
	static HashMap<Long,HashSet<File>> m1 = new HashMap<Long,HashSet<File>>();
	
	static HashSet<File> mNull = new HashSet<File>();
	
	
	public static void clear()
	{
		m0.clear();
		m1.clear();
		mNull.clear();
	}
	
	
	public static void performIter( File fi , IAssociationHandler hndl ) throws Throwable
	{
		File[] f = fi.listFiles();
		int count;
		for( count = 0 ; count < f.length ; count++ )
		{
			File ff = f[ count ];
			if( ( ff.isFile() ) && ( ff.getName().endsWith( ".jpg" ) ) )
			{
				iterate( ff, hndl );
			}
		}
	}
	
	
	private static void iterate( File f , IAssociationHandler hndl ) throws Throwable
	{
		Long vl = DateExtractor.getTime( f );
		if( vl != null )
		{
			// System.out.println( f + " ---- " + vl );
			long m0v = vl / del0;
			long m1v = ( vl + del1 ) / del0;
		
			HashSet<File> brdcst = new  HashSet<File>();
		 
			handleAdd( m0v , m0 , f , hndl , brdcst );
			handleAdd( m1v , m1 , f , hndl , brdcst );
		}
		else
		{
			HashSet<File> brdcst = new  HashSet<File>();
			
			handleAddNull( mNull , f , hndl , brdcst );
		}
	}
	
	
	
	private static void handleAdd( long mv , HashMap<Long,HashSet<File>> m , File f , IAssociationHandler hndl , HashSet<File> brdcst  ) throws Throwable
	{
		if( m.get( mv ) != null )
		{
			for( final File f2 : m.get( mv ) )
			{
				if( !( brdcst.contains( f2 ) ) )
				{
					hndl.handleAssociation(f, f2);
				}
				brdcst.add( f2 );
			}
			m.get( mv ).add( f );
		}
		else
		{
			HashSet<File> hf = new HashSet<File>();
			hf.add( f );
			m.put( mv , hf );
		}
		
		
	}
	
	
	
	private static void handleAddNull( HashSet<File> m , File f , IAssociationHandler hndl , HashSet<File> brdcst  ) throws Throwable
	{
		
		for( final File f2 : m )
		{
			if( !( brdcst.contains( f2 ) ) )
			{
				hndl.handleAssociation(f, f2);
			}
			brdcst.add( f2 );
		}
		m.add( f );
	}
	
	
	
	

}




