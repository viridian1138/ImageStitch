



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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;

public class PanomaticClusterHandler implements IClusterHandler {
	
	protected String descString;
	
	protected static int lst = 0;

	
	public PanomaticClusterHandler( String _descString ) {
		descString = filterDesc( _descString );
	}
	
	
	protected String filterDesc( String in )
	{
		String ret = in.replace(' ', '_');
		return( ret );
	}
	
	
	public String getStitchLoc()
	{
		return( "" + lst + "_" + descString );
	}
	
	
	
	@Override
	public void handleCluster(HashSet<File> f) throws Throwable {
		runPanomatic( f );
		runCpclean();
		runLinefind();
		runAutooptimize();
		runPanomodify();
		runPrecopyA();
		runMk();
		postProcessMakefileForJpeg();
		runPrecopyB();
		runMake();
		runCopyTiffAndJpeg();
		runDeleteTiffAndJpeg();
		
		File tpto = new File( "tmp.pto" );
		File tpmk = new File( "tmp.mk" );
		
		boolean b = tpto.delete();
		if( !b ) throw( new RuntimeException( "Failed" ) );
		
		b = tpmk.delete();
		if( !b ) throw( new RuntimeException( "Failed" ) );
		
	}

	
	
	protected void postProcessMakefileForJpeg() throws Throwable
	{
		File tmp = File.createTempFile( "temporaryMakeFile" , ".mk" );
		File fi = new File( "tmp.mk" );
		handleStdCmd( "cp tmp.mk " + ( tmp.getAbsolutePath() ) );
		
		LineNumberReader li = new LineNumberReader( new FileReader( tmp ) );
		PrintStream ps = new PrintStream( new FileOutputStream( fi ) );
		
		String ln = li.readLine();
		while( ln != null )
		{
			String t0 = ln.replaceAll( "prefix\\.tif" , "prefix.jpg" );
			String t1 = t0.replaceAll( "compression=LZW" , "compression=100" );
			if( t1.contains( "$(ENBLEND)" ) )
			{
				File fz = new File( "enblend_log.txt" );
				t1 = t1 + " 2> " + fz.getAbsolutePath();
			}
			if( ( t1.startsWith( "HUGIN_PROJECTION=" ) ) && ( ProjectionOverride.getProjectionNum() != null )  )
			{
				t1 = "HUGIN_PROJECTION=" + ( ProjectionOverride.getProjectionNum() );
			}
			if( ( t1.startsWith( "HUGIN_HFOV=" ) ) && ( ProjectionOverride.getProjectionFieldOfView() != null ) )
			{
				t1 = "HUGIN_HFOV=" + ( ProjectionOverride.getProjectionFieldOfView() );
			}
			ps.println( t1 ); 
			ln = li.readLine();
		}
		
		li.close();
		ps.close();
		
		// String str = "/bin/sh -c \"sed -i 's/outputImageType tif/outputImageType jpg/' stitch/tmp_" + getLstDesc() + "/tmp.pto\"";
		// System.out.println( str );
		// handleStdCmd( str );
	}
	
	protected void runCpclean() throws Throwable
	{
		handleStdCmd( "cpclean -o stitch/tmp_" + getStitchLoc() + "/tmp.pto stitch/tmp_" + getStitchLoc() + "/tmp.pto" );
	}
	
	
	protected void runLinefind() throws Throwable
	{
		handleStdCmd( "linefind -o stitch/tmp_" + getStitchLoc() + "/tmp.pto stitch/tmp_" + getStitchLoc() + "/tmp.pto" );
	}
	
	
	protected void runAutooptimize() throws Throwable
	{
		handleStdCmd( "autooptimiser -a -m -l -s -o stitch/tmp_" + getStitchLoc() + "/tmp.pto stitch/tmp_" + getStitchLoc() + "/tmp.pto" );
	}
	
	
	protected void runPanomodify() throws Throwable
	{
		handleStdCmd( "pano_modify --canvas=AUTO --crop=AUTO -o stitch/tmp_" + getStitchLoc() + "/tmp.pto stitch/tmp_" + getStitchLoc() + "/tmp.pto" );
	}
	
	
	protected void runPrecopyA() throws Throwable
	{
		handleStdCmd( "cp stitch/tmp_" + getStitchLoc() + "/tmp.pto ." );
	}
	
	
	
	protected void runMk() throws Throwable 
	{	
		handleStdCmd( "pto2mk -o tmp.mk -p prefix tmp.pto" );
	}
	
	
	
	
	
	protected void runPrecopyB() throws Throwable
	{
		handleStdCmd( "cp tmp.mk stitch/tmp_" + getStitchLoc() );
	}
	
	
	protected void runMake() throws Throwable
	{
		
		if( ( new File( "enblend_log.txt" ) ).exists() )
		{
			handleStdCmd( "rm enblend_log.txt" );
		}
		
		handleStdCmd( "make -f tmp.mk all" );
		
		
		Thread.sleep( 5000 );
		
		
		if( ( new File( "enblend_log.txt" ) ).exists() )
		{
			int imgNum = -1;
			LineNumberReader li = new LineNumberReader( new FileReader( new File( "enblend_log.txt" ) ) );
		    String ln = li.readLine();
		    if( ln != null )
		    {
		    	if( ln.contains( "enblend: info: loading next image:" ) )
				{
					imgNum++;
				}
		    	
		    	if( ln.contains( "excessive overlap detected;" ) )
				{
		    		throw( new MakeExcessiveOverlapException( "" + ( imgNum) ) );
				}
		    }
		    while( ln!= null )
		    {
		    	ln = li.readLine();
		    	if( ln != null )
		    	{
		    		if( ln.contains( "enblend: info: loading next image:" ) )
					{
						imgNum++;
					}
		    		
		    		if( ln.contains( "excessive overlap detected;" ) )
					{
		    			throw( new MakeExcessiveOverlapException( "" + ( imgNum ) ) );
					}
		    	}
		    }
		}
	}
	
	
	
	protected void runCopyTiffAndJpeg() throws Throwable
	{
		File[] f = ( new File( "." ) ).listFiles();
		int count;
		for( count = 0 ; count < f.length ; count++ )
		{
			File fi = f[ count ];
			if( ( fi.getName().contains( ".tif" ) ) || ( fi.getName().contains( ".jpg" ) ) )
			{
				handleStdCmd( "cp " + ( fi.getAbsolutePath() ) + " stitch/tmp_" + getStitchLoc() );

			}
		}
	}
	
	
	protected void runDeleteTiffAndJpeg() throws Throwable
	{
		File[] f = ( new File( "." ) ).listFiles();
		int count;
		for( count = 0 ; count < f.length ; count++ )
		{
			File fi = f[ count ];
			if( ( fi.getName().contains( ".tif" ) ) || ( fi.getName().contains( ".jpg" ) ) )
			{
				handleStdCmd( "rm " + ( fi.getAbsolutePath() ) );

			}
		}
	}
	
	
	protected void runPanomatic(HashSet<File> f) throws Throwable {
		
		lst++;
		
		File dir = new File( "stitch/tmp_" + getStitchLoc() );
		
		dir.mkdir();
		
		
		String command = "panomatic -o stitch/tmp_" + getStitchLoc() + "/tmp.pto";
		
		
		for( final File ii : f )
		{
			command = command + " " + ( ii.getAbsolutePath() );
		}
		
		
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
	
	
	
protected void handleStdCmd(final String command) throws Throwable {
		
		
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




