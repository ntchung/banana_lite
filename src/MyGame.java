
#include "Game_Config.h"

package PACKAGE_NAME;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Hashtable;

public class MyGame extends MIDlet
{
	public static AppCanvas myCanvas;
	
	private final void retrieveVersionString()
	{
		try
		{
			Game.versionString = getAppProperty("MIDlet-Version");
		}
		catch( Exception ex )
		{
			Game.versionString = "1.0";
		}
	}

    public MyGame()
    {
		myCanvas = new AppCanvas();
        myCanvas.midlet = this;

		retrieveVersionString();
    }

    public void startApp()
    {
		/*String test = null;
		
		try
		{
			test = getAppProperty("MIDlet-Jar-URL");
		}
		catch( Exception ex )
		{
			notifyDestroyed();
			return;
		}
		
		if( test == null )
		{
			notifyDestroyed();
			return;
		}*/
		
		if( myCanvas == null ) 
  		{
			myCanvas = new AppCanvas();
			retrieveVersionString();
		}
		Display.getDisplay( this ).setCurrent( myCanvas );		
		
		myCanvas.start();  		
    }

	public void exit() 
	{
		destroyApp(true);
		notifyDestroyed();
	}

	protected void pauseApp()
	{
	}
	
    protected void destroyApp( boolean unconditional )
    {		
		if( myCanvas != null )
		{
			if( myCanvas.game != null )
			{
				myCanvas.game.saveGameData();
			}
			myCanvas.stopApp();
		}
    }	
}
