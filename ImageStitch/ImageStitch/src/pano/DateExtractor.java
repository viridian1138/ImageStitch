





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
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class DateExtractor {
	
	
	protected static PrintStream ps = null;
	
	
	protected static void writeLog( File jpegFile ) throws Throwable 
	{
		if( ps == null )
		{
			ps = new PrintStream( new FileOutputStream( "badJpeg.log" ) );
		}
		
		ps.println( jpegFile.getAbsolutePath() );
		
		Metadata metadata = null;
	     metadata = JpegMetadataReader.readMetadata(jpegFile);
	        // iterate through metadata directories
	        Iterator directories = metadata.getDirectories().iterator();
	        if(directories != null) {
	            while (directories.hasNext()) {
	                Directory directory = (Directory)directories.next();
	                // iterate through tags and print to System.out
	                Iterator tags = directory.getTags().iterator();
	                while (tags.hasNext()) {
	                    Tag tag = (Tag)tags.next();
	                    String str = tag.toString();
	                    String dname = tag.getDirectoryName();
	                    String tname = tag.getTagName();
	                    String desc =tag.getDescription();
	                    
	                    ps.println( tname );
	                    ps.flush();
	                    // use Tag.toString()
	                    //System.out.println(tag);
	                }
	            }
	        }
		
	}
	
	
	public static Long getTime( File jpegFile ) throws Throwable
	{
		 Metadata metadata = null;
	     metadata = JpegMetadataReader.readMetadata(jpegFile);
	        // iterate through metadata directories
	        if(directories != null) {
	            for ( final Object ii : metadata.getDirectories() ) {
	                Directory directory = (Directory) ii;
	                // iterate through tags and print to System.out
	                for ( final Object jj : directory.getTags() ) {
	                    Tag tag = (Tag) jj;
	                    String str = tag.toString();
	                    String dname = tag.getDirectoryName();
	                    String tname = tag.getTagName();
	                    String desc =tag.getDescription();
	                    if( tname.equals( "Date/Time Original" ) )
	                    {
	                    	return( parseDateString( desc ) );
	                    }
	                    // use Tag.toString()
	                    //System.out.println(tag);
	                }
	            }
	        }
	        
	        
	        writeLog( jpegFile );
	        
	        
	        return( null );
	       
	    }
	
	
	
	protected static long parseDateString(String dateStr)
	{
		Calendar cal = GregorianCalendar.getInstance();
		StringTokenizer st = new StringTokenizer( dateStr , " :" );
		int year = Integer.parseInt( st.nextToken() );
		int month = Integer.parseInt( st.nextToken() );
		int day = Integer.parseInt( st.nextToken() );
		int hour = Integer.parseInt( st.nextToken() );
		int minute = Integer.parseInt( st.nextToken() );
		int second = Integer.parseInt( st.nextToken() );
		
		cal.set(year, month, day, hour, minute, second);
		
		return( cal.getTimeInMillis() );
	}
	
	
	
	}
