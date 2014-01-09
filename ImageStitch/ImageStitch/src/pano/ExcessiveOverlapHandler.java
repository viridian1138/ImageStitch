




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
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.StringTokenizer;

public class ExcessiveOverlapHandler implements IClusterHandler {
	
	IClusterHandler hndl = null;

	public ExcessiveOverlapHandler( IClusterHandler _hndl ) {
		hndl = _hndl;
	}

	@Override
	public void handleCluster(HashSet<File> f) throws Throwable {
		HashSet<File> f2 = new HashSet<File>( f );
		boolean done = false;
		while( !done )
		{
			try
			{
				done = true;
				hndl.handleCluster( f2 );
			}
			catch( MakeExcessiveOverlapException e )
			{
				if( f2.size() > 2 )
				{
					final int isz = f2.size();
					String msg =e.getMessage();
					int msgNum = Integer.parseInt( msg );
					
					if( msgNum >= 0 )
					{
						final String loc = hndl.getStitchLoc();
						
						File st = getRunFile( loc , msgNum );
						
						if( st == null )
						{
							throw( new RuntimeException( "Internal Error." ) );
						}
						
						f2.remove( st );
						
						if( f2.size() == isz )
						{
							throw( new RuntimeException( "Internal Error." ) );
						}
						
						handleUnstitchedCopy( st );
						
						handleStdCmd( "mv stitch/tmp_" + loc + " stitch_failed" );
						
						done = false;
					}
				}
			}
		}
		
	}
	
	
	
	protected File getRunFile( String loc ,int msgNum ) throws Throwable
	{
		File ret = null;
		final File f = new File( "stitch/tmp_" + loc + "/tmp.mk" );
		LineNumberReader li = new LineNumberReader( new FileReader( f ) );
		
		String line = li.readLine();
		while( line != null )
		{
			String match = "	@echo 'Image " + msgNum + ": /";
			
			if( line.startsWith( match ) )
			{
				StringTokenizer st = new StringTokenizer( line , " '\t" );
				st.nextToken();
				st.nextToken();
				st.nextToken();
				String tf = st.nextToken();
				ret = new File( tf );
			}
			
			line = li.readLine();
		}
		
		return( ret );
	}
	
	
	
	
	public String getStitchLoc()
	{
		return( hndl.getStitchLoc() );
	}
	
	
	protected void handleUnstitchedCopy( File fi ) throws Throwable
	{
		handleStdCmd( "cp " + fi.getAbsolutePath() + " unstitched/" + fi.getName() );
	}
	

	
	
	
protected void handleStdCmd(final String command) throws Throwable 
{
		
		
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
				    System.out.println( ln );
				    while( ln!= null )
				    {
				    	ln = li.readLine();
				    	System.out.println( ln );
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
				    System.out.println( ln );
				    while( ln!= null )
				    {
				    	ln = li.readLine();
				    	System.out.println( ln );
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


	

}




