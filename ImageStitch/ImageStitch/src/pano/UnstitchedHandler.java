


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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;

public class UnstitchedHandler implements IClusterHandler {
	
	IClusterHandler out;
	String loc = null;

	public UnstitchedHandler( IClusterHandler _out ) {
		out = _out;
	}

	@Override
	public void handleCluster(HashSet<File> f) throws Throwable {
		loc = null;
		
		if( f.size() == 1 )
		{
			Iterator<File> it = f.iterator();
			File fi = it.next();
			
			final String command = "cp " + fi.getAbsolutePath() + " unstitched/" + fi.getName();
			
			final Process p = Runtime.getRuntime().exec( command );
			
			final OutputStream os = p.getOutputStream();
			
			final InputStream es = p.getErrorStream();
			
			final InputStream is = p.getInputStream();
			
			

			
			final Runnable runna = new Runnable()
			{
				public void run()
				{
					try
					{
					   os.close();
					}
					catch( Throwable ex )
					{
						ex.printStackTrace( System.out );
					}
				}
			};
			
			
			
			final Runnable runnb = new Runnable()
			{
				public void run()
				{
					try
					{
					    LineNumberReader li = new LineNumberReader( new InputStreamReader( es ) );
					    String ln = li.readLine();
					    while( ln!= null )
					    {
					    	ln = li.readLine();
					    }
					}
					catch( Throwable ex )
					{
						ex.printStackTrace( System.out );
					}
				}
			};
			
			
			
			
			final Runnable runnc = new Runnable()
			{
				public void run()
				{
					try
					{
					    LineNumberReader li = new LineNumberReader( new InputStreamReader( is ) );
					    String ln = li.readLine();
					    while( ln!= null )
					    {
					    	ln = li.readLine();
					    }
					}
					catch( Throwable ex )
					{
						ex.printStackTrace( System.out );
					}
				}
				
			};
			
			
			
			final Thread tha = new Thread( runna );
			
			tha.start();
			
			
			final Thread thb = new Thread( runnb );
			
			thb.start();
			
			
			final Thread thc = new Thread( runnc );
			
			thc.start();
			
			
			
			tha.join();
			thb.join();
			thc.join();
			
		}
		else
		{
			out.handleCluster(f);
			loc = out.getStitchLoc();
		}

	}
	
	
	public String getStitchLoc()
	{
		return( loc );
	}
	

}

