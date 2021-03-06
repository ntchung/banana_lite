
#include "Game_Config.h"

package PACKAGE_NAME;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.lcdui.game.Sprite;
import java.util.Vector;
import java.util.Random;

class ProjectilesManager
{
	public static final int TYPE_ARROW = 0;	
	public static final int TYPE_NUM = 1;
	
	private static final int MAX_PROJECTILES = 200;
		
	public static ProjectilesManager Instance = null;
	
	public ProjectilesManager()
	{
		Instance = this;
		
		listProjectiles = new Projectile[MAX_PROJECTILES];
		for( int i=0; i<MAX_PROJECTILES; ++i )
		{
			listProjectiles[i] = new Projectile();
		}
	}
	
	public void reset()
	{
		projectilesCount = 0;
	}
	
	public void loadAssets()
	{
		if( mainSprite == null )
		{
			mainSprite = new ASprites();
			mainSprite.loadSprite( "/projectiles.png", "/projectiles.dat" );
			
			final int count = mainSprite.animsCount;
			animFramesCount = new byte[count];
			for( int i=0; i<count; ++i )
			{
				animFramesCount[i] = (byte)mainSprite.getAnimFrameCount(i);
			}
		}
	}
	
	public void update()
	{		
		for( int i=0; i<projectilesCount; )
		{
			if( listProjectiles[i].update() )
			{
				++i;
			}
			else
			{
				listProjectiles[i].Copy( listProjectiles[projectilesCount-1] );
				--projectilesCount;
			}
		}		
	}	
	
	public void paint(Graphics g)
	{
		for( int i=0; i<projectilesCount; ++i )
		{
			listProjectiles[i].paint(g);
		}
	}
	
	public Projectile create(int type, int x, int y)
	{
		if( projectilesCount == MAX_PROJECTILES )
		{
			return null;
		}
	
		Projectile prj = listProjectiles[projectilesCount];
		++projectilesCount;
		
		prj.reset(type, x, y);		
		return prj;
	}
	
	public int serialize(byte[] data, int offset)
	{		
		data[offset] = (byte)projectilesCount;
		++offset;
		
		for( int i=0; i<projectilesCount; ++i )
		{
			offset = listProjectiles[i].serialize(data, offset);		
		}
		
		return offset;
	}
	
	public int deserialize(byte[] data, int offset)
	{	
		if( data != null )
		{			
			projectilesCount = data[offset+0];
			++offset;			
			
			for( int i=0; i<projectilesCount; ++i )
			{
				offset = listProjectiles[i].deserialize(data, offset);
			}
		
			return offset;
		}
		
		return 0;
	}
	
	public ASprites mainSprite;
	public byte[] animFramesCount;
	
	private Projectile[] listProjectiles;
	private int projectilesCount;
	
	private Random random;
}
