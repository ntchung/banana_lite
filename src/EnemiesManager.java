
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

class EnemiesManager
{
	public static final int TYPE_GOBLIN = 0;
	public static final int TYPE_TROLL = 1;
	public static final int TYPE_THROWER = 2;
	public static final int TYPE_SPIDER = 3;
	public static final int TYPE_RAVEN = 4;
	public static final int TYPE_SHAMAN = 5;
	public static final int TYPE_OGRE = 6;
	public static final int TYPE_NUM = 7;
	
	private static final int MAX_ENEMIES = 10;
	
	public static int WALL_MIN_X = -160;
	public static int WALL_MAX_X = 160;	
	
	public static EnemiesManager Instance = null;
	
	public EnemiesManager()
	{
		Instance = this;
		
		random = new Random();
		
		listEnemies = new Enemy[MAX_ENEMIES];
		listEnemiesToPaintBehindWall = new Enemy[MAX_ENEMIES];
		listEnemiesToPaintBeforeWall = new Enemy[MAX_ENEMIES];
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			listEnemies[i] = new Enemy();
		}
	}
	
	public void loadAssets()
	{
		if( spritesPool == null )
		{
			spritesPool = new ASprites[TYPE_NUM];
			animFramesCount = new byte[TYPE_NUM][];			
			loadSprite( TYPE_GOBLIN, "/goblin.png", "/goblin.dat" );
			loadSprite( TYPE_TROLL, "/troll.png", "/troll.dat" );
			loadSprite( TYPE_OGRE, "/ogre.png", "/ogre.dat" );
			loadSprite( TYPE_RAVEN, "/raven.png", "/raven.dat" );
			loadSprite( TYPE_SPIDER, "/spider.png", "/spider.dat" );
			loadSprite( TYPE_THROWER, "/thrower.png", "/thrower.dat" );
			loadSprite( TYPE_SHAMAN, "/shaman.png", "/shaman.dat" );
			
			EnemyPropertiesPool = new EnemyProperties[TYPE_NUM];
			//														nMoveSpeed	nClimbSpeed		nWidth			nHeight		nMaxHP	nAttackRange	nAttackPower	nMinDecisionCountdown	nMaxDecisionCountdown
			EnemyPropertiesPool[TYPE_GOBLIN] = new EnemyProperties(	(2 << 4),	(1 << 4), 		(22 << 4), 		(25 << 4),	1,		(1 << 4),		1,				32,						48);
			EnemyPropertiesPool[TYPE_TROLL] = new EnemyProperties(	(1 << 4),	(1 << 4), 		(42 << 4), 		(45 << 4),	2,		(3 << 4),		2,				32,						48);
			EnemyPropertiesPool[TYPE_RAVEN] = new EnemyProperties(	(3 << 4),	0, 				(25 << 4), 		(20 << 4), 	1, 		(1 << 4), 		1,				32, 					48);
			EnemyPropertiesPool[TYPE_OGRE] = new EnemyProperties(	(1 << 4),	(1 << 3), 		(40 << 4), 		(40 << 4),	5,		(8 << 4),		3,				32,						48);
			EnemyPropertiesPool[TYPE_SPIDER] = new EnemyProperties(	(2 << 4),	(1<<4)+(1<<3), 	(20 << 4), 		(20 << 4),	1,		(1 << 4),		1,				32,						48);
			EnemyPropertiesPool[TYPE_THROWER] = new EnemyProperties((1 << 4),	0,		 		(24 << 4), 		(27 << 4),	1,		0,				1,				32,						48);
			EnemyPropertiesPool[TYPE_SHAMAN] = new EnemyProperties( (1 << 4),	0,		 		(24 << 4), 		(27 << 4),	1,		0,				1,				32,						48);
		}
	}
	
	private void loadSprite(int type, String imageFileName, String dataFileName)
	{
		spritesPool[type] = new ASprites();
		spritesPool[type].loadSprite( imageFileName, dataFileName );
		
		final int count = spritesPool[type].animsCount;
		animFramesCount[type] = new byte[count];
		for( int i=0; i<count; ++i )
		{
			animFramesCount[type][i] = (byte)spritesPool[type].getAnimFrameCount(i);
		}
	}
	
	public void update()
	{
		int i;
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			if( !listEnemies[i].isActive )
			{
				break;
			}
		}
		
		if( i < MAX_ENEMIES )
		{
			listEnemies[i].reset(TYPE_SHAMAN);			
			listEnemies[i].x = ((random.nextInt() & 127) < 64 ? WALL_MIN_X : WALL_MAX_X) << 4;			
			listEnemies[i].y = Math.abs(random.nextInt()) & 255;
			//listEnemies[i].y = (64 + Math.abs(random.nextInt()) % (Game.wallHeight-128)) << 4;
			listEnemies[i].flip = listEnemies[i].x < 0 ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
		}
		
		chasingFlyersCount = 0;
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemies[i];
			if( enemy.isActive && enemy.type == TYPE_RAVEN && enemy.currentAnim == FlyerAnim.chase )
			{
				++chasingFlyersCount;
			}
		}				
		
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive )
			{
				if( !listEnemies[i].update() )
				{
					listEnemies[i].isActive = false;
				}
			}
			
			listEnemiesToPaintBehindWall[i] = null;
			listEnemiesToPaintBeforeWall[i] = null;
		}
		
		// Sort behind wall enemies
		int count = 0;
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive && listEnemies[i].isBehindWall )
			{
				listEnemiesToPaintBehindWall[count] = listEnemies[i];
				++count;
			}
		}
		sortEnemiesByY(listEnemiesToPaintBehindWall, count);
		
		count = 0;
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive && !listEnemies[i].isBehindWall )
			{
				listEnemiesToPaintBeforeWall[count] = listEnemies[i];
				++count;
			}
		}
		sortEnemiesByY(listEnemiesToPaintBeforeWall, count);
	}	
	
	private void sortEnemiesByY(Enemy[] list, int count)
	{
		for( int i=0; i<count; ++i )
		{
			for( int j=i+1; j<count; ++j )
			{
				if( list[j].y > list[i].y )
				{
					Enemy temp = list[i];
					list[i] = list[j];
					list[j] = temp;
				}
			}
		}
	}
	
	public void paintBehindWall(Graphics g)
	{
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemiesToPaintBehindWall[i];
			if( enemy != null )
			{
				enemy.paint(g);
			}
			else
			{
				break;
			}
		}
	}
	
	public void paintBeforeWall(Graphics g)
	{
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemiesToPaintBeforeWall[i];
			if( enemy != null )
			{
				enemy.paint(g);
			}
			else
			{
				break;
			}
		}
	}
	
	public int serialize(byte[] data, int offset)
	{
		int count = 0;
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive )
			{
				++count;
			}
		}
		
		data[offset+0] = (byte)count;
		++offset;
		
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive )
			{
				offset = listEnemies[i].serialize(data, offset);
			}
		}
	
		return offset;
	}
	
	public int deserialize(byte[] data, int offset)
	{	
		if( data != null )
		{
			for( int i=0; i<MAX_ENEMIES; ++i )
			{
				listEnemies[i].isActive = false;
			}
			
			int count = data[offset+0];
			++offset;
			
			for( int i=0; i<count; ++i )
			{
				offset = listEnemies[i].deserialize(data, offset);
			}
		
			return offset;
		}
				
		return 0;
	}	
	
	public int checkHit(int x1, int y1, int x2, int y2, Enemy[] result)
	{
		int resultCount = 0;
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemies[i];
			if( enemy.isActive && enemy.HP > 0 )
			{
				final int enemyX1 = enemy.x - enemy.properties.HalfWidth;
				final int enemyX2 = enemy.x + enemy.properties.HalfWidth;
				final int enemyY1 = enemy.y;
				final int enemyY2 = enemy.y + enemy.properties.Height;
				
				if( !( x1 > enemyX2 || x2 < enemyX1 || y1 > enemyY2 || y2 < enemyY1 ) )
				{
					result[resultCount] = enemy;
					++resultCount;
					if( resultCount >= result.length )
					{
						break;
					}
				}
			}
		}
				
		return resultCount;
	}
	
	public Enemy checkHit(int x1, int y1, int x2, int y2)
	{
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemies[i];
			if( enemy.isActive && enemy.HP > 0 )
			{
				final int enemyX1 = enemy.x - enemy.properties.HalfWidth;
				final int enemyX2 = enemy.x + enemy.properties.HalfWidth;
				final int enemyY1 = enemy.y;
				final int enemyY2 = enemy.y + enemy.properties.Height;
				
				if( !( x1 > enemyX2 || x2 < enemyX1 || y1 > enemyY2 || y2 < enemyY1 ) )
				{
					return enemy;
				}
			}
		}
		
		return null;
	}
	
	public Enemy checkHitBeforeWall(int x1, int y1, int x2, int y2)
	{
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemies[i];
			if( enemy.isActive && !enemy.isBehindWall && enemy.HP > 0 )
			{
				final int enemyX1 = enemy.x - enemy.properties.HalfWidth;
				final int enemyX2 = enemy.x + enemy.properties.HalfWidth;
				final int enemyY1 = enemy.y;
				final int enemyY2 = enemy.y + enemy.properties.Height;
				
				if( !( x1 > enemyX2 || x2 < enemyX1 || y1 > enemyY2 || y2 < enemyY1 ) )
				{
					return enemy;
				}
			}
		}
		
		return null;
	}
	
	public boolean checkHitWithClimbers(Enemy checker)
	{
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemies[i];
			if( enemy.isActive && enemy != checker && enemy.currentAnim == MeleeAnim.climb )
			{
				final int enemyX1 = enemy.x - enemy.properties.HalfWidth;
				final int enemyX2 = enemy.x + enemy.properties.HalfWidth;
				final int enemyY1 = enemy.y;
				final int enemyY2 = enemy.y + enemy.properties.Height;
				
				final int checkerX1 = checker.x - checker.properties.HalfWidth;
				final int checkerX2 = checker.x + checker.properties.HalfWidth;
				final int checkerY1 = checker.y;
				final int checkerY2 = checker.y + checker.properties.Height;
				
				if( !( checkerX1 > enemyX2 || checkerX2 < enemyX1 || checkerY1 > enemyY2 || checkerY2 < enemyY1 ) )
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public ASprites[] spritesPool;
	public byte[][] animFramesCount;
	
	private Enemy[] listEnemies;
	private Enemy[] listEnemiesToPaintBehindWall;
	private Enemy[] listEnemiesToPaintBeforeWall;
	
	public EnemyProperties[] EnemyPropertiesPool;
	public Random random;
	
	public int chasingFlyersCount;
}
