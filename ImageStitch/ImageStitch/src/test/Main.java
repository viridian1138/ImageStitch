


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





package test;
/*
 * Main.java
 *
 * Created on June 6, 2007, 9:43 PM
 *
 */


import java.io.File;

import pano.ExcessiveOverlapHandler;
import pano.FirstStageIterationHandler;
import pano.IAssociationHandler;
import pano.IClusterHandler;
import pano.PanomaticAssocHandler;
import pano.PanomaticClusterHandler;
import pano.PreStitch;
import pano.ProjectionOverride;
import pano.SecondStageIterationHandler;
import pano.StitchingErrorHandler;
import pano.TraversingAssocHandler;
import pano.UnstitchedHandler;

 

public class Main {
   
    /** Creates a new instance of Main */
    public Main() {
    }
    
    
    
    
    public static void handleTopDir( File f ) throws Throwable
    {
    	System.out.println( "handleTopDir " + ( f.getAbsolutePath() ) );
    	
    	TraversingAssocHandler ha = new TraversingAssocHandler();
        
        IAssociationHandler h = new PanomaticAssocHandler( ha );
        
        FirstStageIterationHandler.performIter( f , h );
        
        System.out.println( "" );
        
        IClusterHandler h2 = new PanomaticClusterHandler( f.getName() );
        
        h2 = new UnstitchedHandler( new StitchingErrorHandler( new ExcessiveOverlapHandler( h2 ) ) );
        
        SecondStageIterationHandler.performIter( f , ha.getMaps() , h2 );
        
        FirstStageIterationHandler.clear();
        
        SecondStageIterationHandler.clear();
    }
    
    
    
    
    public static void handleDirFile( File f ) throws Throwable
    {
    	File[] lst = f.listFiles();
    	boolean jpgFound = false;
    	int cnt;
    	for( cnt = 0 ; cnt < lst.length ; cnt++ )
    	{
    		File ff = lst[ cnt ];
    		if( ff.isDirectory() )
    		{
    			handleDirFile( ff );
    		}
    		else
    		{
    			if( !jpgFound )
    			{
    				String lcase = ff.getName().toLowerCase();
    				if( ( lcase.endsWith( ".jpeg" ) ) || ( lcase.endsWith( ".jpg" ) ) )
    				{
    					jpgFound = true;
    					handleTopDir( f );
    				}
    			}
    		}
    	}
    }
    
    
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Throwable {
    	
    	File f = new File( args[ 0 ] );
    	
    	if( args.length >= 2 )
    	{
    		ProjectionOverride.setProjectionNum( Integer.parseInt( args[ 1 ] ) );
    	}
    	
    	if( args.length >= 3 )
    	{
    		ProjectionOverride.setProjectionFieldOfView( Integer.parseInt( args[ 2 ] ) );
    	}
        
        PreStitch.execute();
        
        handleDirFile( f );
       
       
    }
   
}

