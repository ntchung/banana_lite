
#include "Game_Config.h"

package PACKAGE_NAME;

import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
 
import javax.microedition.lcdui.game.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

import java.util.Timer;
import java.util.TimerTask;

class AppCanvas extends GameCanvas
{
	public MyGame midlet;
	
	private int kMaxFPS = 30;
	
	public Game game;
	private boolean isPaused;
		
	private GameThread gameThread;
	
	public AppCanvas()
	{
		super( false );

		setFullScreenMode( true );
		
		Game.hasTouch = hasPointerEvents();
		//Game.hasTouch = false;
		
		game = new Game( this );
		game.setSize( this.getWidth(), this.getHeight() );			
		
		isPaused = false;
		Util.initKeyAdaptor(this);		
	}
	
	protected void sizeChanged( int w, int h )
	{
		super.sizeChanged( w, h );
		game.setSize( w, h );
	}
	
	class GameThread extends Thread 
	{
        private boolean pause = true;
        private boolean stop = false;
        private boolean started = false;

        public void requestStart() 
		{
            this.pause = false;
            if (!started) 
			{
                this.start();
                this.started = true;
            } 
			else 
			{
                synchronized (this) 
				{                    
                    notify();
                }
            }
        }

        public void requestPause() 
		{
            this.pause = true;
        }

        public void requestStop() 
		{
            this.stop = true;
        }

        // This example uses only one thread for updating game logic and
        // for rendering with constant frequency.
        public void run() 
		{
            long time = 0;            
            while( !stop )
			{
                try 
				{
                    if( pause ) 
					{                        
                        synchronized (this) 
						{				
							if( !isPaused  )
							{
								game.pauseGame();
								isPaused = true;
							}
							
                            wait();
                        }
                    } 
					else 
					{
                        time = System.currentTimeMillis();
						
						if( isPaused )
						{
							game.resumeGame();
							isPaused = false;
						}	
                        
						if( game.isGameRunning )
						{
							game.update();
								
							final Graphics g = getGraphics();
							paint(g);
							flushGraphics();
						}
						else
						{
							midlet.exit();
						}

						if( !Game.isLoadingProcess )
						{
							// Sleep the rest of the time
							time = (1000/kMaxFPS) - (System.currentTimeMillis() - time);
							//Thread.sleep((time < 0 ? 0 : time));
							Thread.sleep(1);
							Thread.yield();
						}
						else
						{
							Game.isLoadingProcess = false;
						}
						
						//System.gc();
                    }
                } 
				catch (Exception ex) 
				{
					Trace(ex);
				}
            }
        }
    }
	
	public void start()
    {		
		if( gameThread == null ) 
		{
			gameThread = new GameThread();
		}
		try
		{
			Thread.sleep( 100 );
		}
		catch( Exception ex )
		{
			Trace(ex);
		}
        gameThread.requestStart();
	}
	
    public void stopApp()
    {	
		game.destroy();
		this.gameThread.requestStop();
		game.isGameRunning = false;
    }
		
	protected void hideNotify()
    { 		
		gameThread.requestPause();
    }

    protected void showNotify()
    {
		start();
    }

    public void paint( Graphics g )
    {		
		if( !isPaused )
		{
			game.paint( g );
		}
	}
	
	protected void keyPressed( int keyCode )
	{
		Game.onKeyPressed( keyCode );
	}
	
	protected void keyReleased( int keyCode )
	{
		Game.onKeyReleased( keyCode );
	}

	protected void pointerPressed( int x, int y ) 
	{
		game.onPointerPressed( x, y );
	}

	protected void pointerDragged( int x, int y ) 
	{	
		game.onPointerDragged( x, y );
	}

	protected void pointerReleased( int x, int y ) 
	{
		game.onPointerReleased( x, y );
	}
}
