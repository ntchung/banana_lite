
#include "Game_Config.h"

package PACKAGE_NAME;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Hashtable;
import vAdEngine.VservInterface;
import vAdEngine.VservManager;

public class MyGame extends MIDlet implements VservInterface
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
		
    }
	
	public void constructorMainApp()
    {
		myCanvas = new AppCanvas();
        myCanvas.midlet = this;

		retrieveVersionString();
    }
	
	public void startMainApp()
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

    public void startApp()
    {
		Hashtable vservConfigTableAd = new Hashtable();
        vservConfigTableAd.put("zoneId", "41d85cd0");
        vservConfigTableAd.put("showAt", "start");
        new VservManager(this, vservConfigTableAd);
    }

	public void exit() 
	{
		destroyApp(true);
		//notifyDestroyed();
		Hashtable vservConfigTableAd = new Hashtable();
        vservConfigTableAd.put("zoneId", "41d85cd0");
        vservConfigTableAd.put("showAt", "end");
        new VservManager(this, vservConfigTableAd);
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
				myCanvas.game.saveProfile();
				myCanvas.game.saveGameData();
			}
			myCanvas.stopApp();
		}
    }	
	
	public void showMidAds()
	{
		Hashtable vservConfigTableAd = new Hashtable();
		vservConfigTableAd.put("zoneId", "41d85cd0");
		vservConfigTableAd.put("showAt", "mid");
		new VservManager(this, vservConfigTableAd);
	}
	
	public void resumeMainApp()
    {
        Display.getDisplay(this).setCurrent(myCanvas);
    }
}
