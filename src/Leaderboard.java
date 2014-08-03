
#include "Game_Config.h"

package PACKAGE_NAME;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.lcdui.game.Sprite;
import java.util.Vector;
import javax.microedition.io.*;

class Leaderboard extends List implements CommandListener
{
	public static Leaderboard Instance;
	private Command backCommand;		
	
	public Leaderboard(String title)
	{
		super(title, List.IMPLICIT);
		Instance = this;
		
		append("Submit my score", null);
		append("Top 10", null);
		append("My ranking", null);
		
		backCommand = new Command("Back", Command.BACK, 1);		
		this.addCommand(backCommand);
		this.setCommandListener(this);		
	}
	
	public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {
			AppCanvas.midlet.switchToGame();
		}
		else if (c == List.SELECT_COMMAND) 
		{
			int index = this.getSelectedIndex();
			switch( index )
			{
			case 0:			
				AppCanvas.midlet.setDisplayable(new LeaderboardSubmit());				
			break;
			case 1:
				AppCanvas.midlet.setDisplayable(new LeaderboardTop10());
			break;
			case 2:
				AppCanvas.midlet.setDisplayable(new LeaderboardRankings());
			break;
			}
		}
	}
}

class LeaderboardRankings extends Form implements CommandListener
{
	private Command backCommand;	
	private RankingsThread thread;
	
	public LeaderboardRankings()
	{
		super("My ranking");
		
		this.setCommandListener(this);		
		backCommand = new Command("Back", Command.BACK, 1);		
		this.addCommand(backCommand);		
		
		if( Game.optionUsername == null || Game.optionUsername.length() < 1 )
		{
			Alert alert = new Alert("Error", "Please submit your score first.", null, AlertType.ERROR);
				alert.setTimeout(Alert.FOREVER);
				alert.setCommandListener(this);
				AppCanvas.midlet.setDisplayable(alert);
		}
		else
		{
			append("Please wait...");		
			
			thread = new RankingsThread(this);
			new Thread(thread).start();
		}
	}
	
	public void EndWait()
	{
		this.delete(0);		
	}
	
	public void AddItem(String name, String score)
	{
		append(new LeaderboardItem(name, score));
	}
	
	public void PrependItem(String name, String score)
	{
		insert(0, new LeaderboardItem(name, score));
	}
	
	public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {
			// Destroy
			thread.stop();
			thread = null;
		
			AppCanvas.midlet.setDisplayable(Leaderboard.Instance);
		}
		else if (c == Alert.DISMISS_COMMAND) {
			AppCanvas.midlet.setDisplayable(Leaderboard.Instance);
		}
	}	
	
	class RankingsThread extends LeaderboardThread
	{
		private LeaderboardRankings myParent;
		
		RankingsThread(LeaderboardRankings parent)
		{
			super();
			myParent = parent;
		}
	
		public void handleConnectionResult()
		{
			try
			{
				myParent.EndWait();
			
				// Error
				if( is.read() == 'E' )
				{
					Alert alert = new Alert("Error", "Please enter correct username, or submit your score first.", null, AlertType.ERROR);
					alert.setTimeout(Alert.FOREVER);
					alert.setCommandListener(myParent);
					AppCanvas.midlet.setDisplayable(alert);
					return;
				}
			
				// OK
				is.read(); // bypass the newline character
				
				int state = 0;
				StringBuffer temp = new StringBuffer(1024);
				int read;
				String currentName = null;
				
				int lowRank = -1;
				int highRank = -1;
				int yourRank = -1;
				int yourScore = Game.highScore;
				while ((read=is.read()) != -1) 
				{
					if( read == '\n' )
					{
						if( state == 1 )
						{
							myParent.PrependItem(yourRank + ". " + Game.optionUsername, "" + Game.highScore);
						}
						++state;
						temp = new StringBuffer(1024);
					}
					else if( read == '\t' )
					{					
						// Rank and server score
						if( state == 0 )
						{
							if( yourRank < 0 )
							{
								try
								{
									yourRank = Integer.parseInt(temp.toString());
								}
								catch( Exception ex )
								{
									yourRank = 0;
								}
								lowRank = yourRank + 1;
								highRank = yourRank - 1;
							}
							else
							{
								try
								{
									yourScore = Integer.parseInt(temp.toString());
									Game.highScore = yourScore;
								}
								catch( Exception ex )
								{
									yourScore = Game.highScore;
								}
							}
						}
						// Lower
						else if( state == 1 )
						{
							if( currentName == null )
							{
								currentName = temp.toString();
							}
							else
							{
								myParent.AddItem(lowRank + ". " + currentName, temp.toString());
								++lowRank;
								currentName = null;
							}
						}
						// Higher
						else if( state == 2 )
						{
							if( currentName == null )
							{
								currentName = temp.toString();
							}
							else
							{
								myParent.PrependItem(highRank + ". " + currentName, temp.toString());
								--highRank;
							
								currentName = null;
							}
						}
						
						temp = new StringBuffer(1024);
					}
					else
					{
						temp.append((char)read);
					}
				}
			}
			catch( Exception ex )
			{
			}
		}		
		
		public  HttpConnection openConnection()
		{
			try
			{
				String url = "http://www.kinoastudios.com:80/CastleDefender/javaaround.php?username="
					+ Util.urlEncode(Game.optionUsername);
				return (HttpConnection)Connector.open(url, Connector.READ_WRITE, true);
			}
			catch( Exception ex )
			{
			}
			
			return null;
		}
	}
}

class LeaderboardSubmit extends Form implements CommandListener, ItemCommandListener
{
	private Command backCommand;	
	private Command okCommand;
	private StringItem cmdOK;
	
	private TextField txtUsername;
	private TextField txtPassword;
	
	public LeaderboardSubmit()
	{
		super("Submit my score");
		
		txtUsername = new TextField("Username", Game.optionUsername, 128, 0);
		txtPassword = new TextField("Password", Game.optionPassword, 128, 0);
				
		append( txtUsername );
		append( txtPassword );
		append( "Score: " + Game.highScore );
		
		okCommand = new Command("Ok", Command.ITEM, 1);    
		cmdOK = new StringItem("", "Submit", Item.BUTTON);                                    
		cmdOK.setDefaultCommand(okCommand);
		cmdOK.setItemCommandListener(this); 
		append(cmdOK);
		
		backCommand = new Command("Back", Command.BACK, 1);		
		this.addCommand(backCommand);
		this.setCommandListener(this);				
	}	
	
	public void commandAction(Command c, Item item) 
	{
		if( c == okCommand ) {
			String username = txtUsername.getString();
			String password = txtPassword.getString();
			if( username.length() < 1 )
			{
				Alert alert = new Alert("Error", "Please enter an username", null, AlertType.ERROR);
				alert.setTimeout(Alert.FOREVER);
				alert.setCommandListener(this);
				AppCanvas.midlet.setDisplayable(alert);
			}
			else if( password.length() < 1 )
			{
				Alert alert = new Alert("Error", "Please enter a password", null, AlertType.ERROR);
				alert.setTimeout(Alert.FOREVER);
				alert.setCommandListener(this);
				AppCanvas.midlet.setDisplayable(alert);
			}
			else
			{
				Game.optionUsername = username;
				Game.optionPassword = password;			
				AppCanvas.game.saveProfile();
			
				AppCanvas.midlet.setDisplayable(new LeaderboardSubmitWait(this));		
			}
		}
	}
	
	public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {			
			AppCanvas.midlet.setDisplayable(Leaderboard.Instance);
		}		
		else if (c == Alert.DISMISS_COMMAND) {
			AppCanvas.midlet.setDisplayable(this);
		}
	}		
}

class LeaderboardSubmitWait extends Form implements CommandListener
{
	private Command backCommand;	
	private SubmitThread thread;
	private LeaderboardSubmit myParent;
	
	public LeaderboardSubmitWait(LeaderboardSubmit parent)
	{
		super("Submit my score");
		myParent = parent;
		
		append("Please wait...");
		
		backCommand = new Command("Back", Command.BACK, 1);		
		this.addCommand(backCommand);
		this.setCommandListener(this);		
		
		thread = new SubmitThread(this);
		new Thread(thread).start();
	}
	
	public void EndWait()
	{
		this.delete(0);		
	}	
	
	public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {
			// Destroy
			thread.stop();
			thread = null;
		
			AppCanvas.midlet.setDisplayable(Leaderboard.Instance);
		}
		else if (c == Alert.DISMISS_COMMAND) {
			AppCanvas.midlet.setDisplayable(myParent);
		}
	}	
	
	class SubmitThread extends LeaderboardThread
	{
		private LeaderboardSubmitWait myParent;
		
		SubmitThread(LeaderboardSubmitWait parent)
		{
			super();
			myParent = parent;
		}
	
		public void handleConnectionResult()
		{
			try
			{
				myParent.EndWait();
				
				int read = is.read();
				if( read == '0' )
				{
					AppCanvas.midlet.setDisplayable(Leaderboard.Instance);
				}
				else if( read == '1' )
				{
					Alert alert = new Alert("Error", "Wrong password or name already taken. Please enter correct password or submit as new name.", null, AlertType.ERROR);
					alert.setTimeout(Alert.FOREVER);
					alert.setCommandListener(myParent);
					AppCanvas.midlet.setDisplayable(alert);
				}
				else
				{
					Alert alert = new Alert("Error", "Something went wrong, please try again.", null, AlertType.ERROR);
					alert.setTimeout(Alert.FOREVER);
					alert.setCommandListener(myParent);
					AppCanvas.midlet.setDisplayable(alert);
				}
			}
			catch( Exception ex )
			{
			}
		}		
		
		public  HttpConnection openConnection()
		{
			try
			{
				String username = Util.urlEncode(Game.optionUsername);
				String password = Util.urlEncode(Util.md5String(Game.optionPassword));
				String score = "" + Game.highScore;
				
				String combined = username + password + score;
				String check = Util.md5String(combined);
				
				String url = "http://www.kinoastudios.com:80/CastleDefender/javasubmit.php?username="
					+ username + "&password=" + password + "&score=" + score + "&check=" + check;
				return (HttpConnection)Connector.open(url, Connector.READ_WRITE, true);
			}
			catch( Exception ex )
			{
			}
			
			return null;
		}
	}
}

class LeaderboardTop10 extends Form implements CommandListener
{
	private Command backCommand;	
	private Top10Thread thread;
	
	public LeaderboardTop10()
	{
		super("Top 10");
		
		append("Please wait...");
		
		backCommand = new Command("Back", Command.BACK, 1);		
		this.addCommand(backCommand);
		this.setCommandListener(this);		
		
		thread = new Top10Thread(this);
		new Thread(thread).start();
	}
	
	public void EndWait()
	{
		this.delete(0);		
	}
	
	public void AddItem(String name, String score)
	{
		append(new LeaderboardItem(name, score));
	}	
	
	public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {
			// Destroy
			thread.stop();
			thread = null;
		
			AppCanvas.midlet.setDisplayable(Leaderboard.Instance);
		}
	}	
	
	class Top10Thread extends LeaderboardThread
	{
		private LeaderboardTop10 myParent;
		
		Top10Thread(LeaderboardTop10 parent)
		{
			super();
			myParent = parent;
		}
	
		public void handleConnectionResult()
		{
			try
			{
				myParent.EndWait();
			
				StringBuffer temp = new StringBuffer(1024);
				int read;
				String currentName = null;
				int ranking = 1;
				while ((read=is.read()) != -1) 
				{
					if( read == '\n' )
					{
						if( currentName == null )
						{
							currentName = temp.toString();
						}
						else
						{
							myParent.AddItem(ranking + ". " + currentName, temp.toString());									
							++ranking;
							currentName = null;
						}
						temp = new StringBuffer(1024);
					}
					else
					{
						temp.append((char)read);
					}
				}
			}
			catch( Exception ex )
			{
			}
		}		
		
		public  HttpConnection openConnection()
		{
			try
			{
				return (HttpConnection)Connector.open("http://www.kinoastudios.com:80/CastleDefender/javatop10.php", Connector.READ_WRITE, true);
			}
			catch( Exception ex )
			{
			}
			
			return null;
		}
	}
}

class LeaderboardItem extends CustomItem 
{
	private String myScore;

	public LeaderboardItem(String title, String score) 
	{ 
		super(title); 
		myScore = score;
	}

	public int getMinContentWidth() { return 200; }
	public int getMinContentHeight() { return 30; }

	public int getPrefContentWidth(int width) {
		return getMinContentWidth();
	}

	public int getPrefContentHeight(int height) {
		return getMinContentHeight();
	}

	public void paint(Graphics g, int w, int h) {
		g.setColor(0xFF0000);
		g.drawString(myScore, 0, 0, Graphics.LEFT | Graphics.TOP);
	}
}

abstract class LeaderboardThread implements Runnable
{
	protected boolean isRunning;
	
	protected HttpConnection hc;
	protected InputStream is;
	protected int tries;
	
	LeaderboardThread()
	{
		isRunning = true;
		hc = null;
		is = null;
		tries = 0;
	}

	public void stop()
	{
		synchronized(this)
		{
			isRunning = false;
		}
	}
	
	public abstract void handleConnectionResult();
	public abstract HttpConnection openConnection();
	
	public void run()
	{
		while( isRunning )
		{
			try
			{			
				if( hc == null || (tries > 1) )
				{
					tries = 0;
					
					try
					{
						if( hc != null )
						{
							hc.close();
							hc = null;
						}
						
						if( is != null )
						{
							is.close();
							is = null;
						}
					}
					catch( Exception ex )
					{
					}
					
					Thread.sleep(1000);
					Thread.yield();
					
					hc = openConnection();		
					if( hc != null )
					{
						hc.setRequestMethod(HttpConnection.GET); //default
						is = hc.openInputStream();
					}
				}
					
				// Check the Content-Length first
				long len = hc.getLength(); 
				if( len != -1 ) 
				{
					handleConnectionResult();
					
					isRunning = false;						
				}					

				if( isRunning )
				{
					Thread.sleep(4000);					
					Thread.yield();					
				}
				
				++tries;
			}
			catch( Exception ex )
			{
			}
		}
		
		try
		{
			if( hc != null )
			{
				hc.close();	
				hc = null;
			}
			
			if( is != null )
			{
				is.close();
				is = null;
			}
		}
		catch( Exception ex )
		{
		}
	}
}
