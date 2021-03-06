
#include "Game_Config.h"

package PACKAGE_NAME;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.lcdui.game.Sprite;
import java.util.Vector;

class PlayerCharacter
{
	public static PlayerCharacter Instance = null;

	public PlayerCharacter()
	{
		Instance = this;
	
		sprite = null;
		HalfWidth = 15 << 4;
		HalfHeight = 20 << 4;
		AttackRange = 15 << 4;
		
		reset();
	}
	
	public void reset()
	{
		HP = 10;
		score = 0;
		
		x = 0;
		y = Game.wallHeight << 4;		
		currentAnim = KnightAnim.idle_bow;
		currentFrame = 0;
		currentFrameFraction = 0;
		accelerate = 0;
		flip = Sprite.TRANS_NONE;
	}

	public void loadAssets()
	{
		if( sprite == null )
		{
			sprite = new ASprites();
			sprite.loadSprite( "/knight.png", "/knight.dat" );	
			
			for( int i=0; i<7; ++i )
			{
				animFramesCount[i] = sprite.getAnimFrameCount(i);
			}
		}		
		
		//y = Game.wallHeight << 4;
	}

	public boolean update()
	{	
		if( currentAnim == KnightAnim.dying )
		{
			return updateDying();
		}
	
		if( HP <= 0 )
		{
			Game.Instance.clearGameData();
			Game.playSfx(Game.SFX_DIE);
			currentAnim = KnightAnim.dying;
			return true;
		}
	
		switch( currentAnim )
		{
			case KnightAnim.idle_bow:
				updateIdleBow();
			break;
			
			case KnightAnim.walk_bow:
				updateWalkBow();
			break;
			
			case KnightAnim.attack_bow:
				updateAttackBow();
			break;
			
			case KnightAnim.idle_sword:
				updateIdleSword();
			break;
			
			case KnightAnim.walk_sword:
				updateWalkSword();
			break;
			
			case KnightAnim.attack_sword:
				updateAttackSword();
			break;
		}
	
		++currentFrameFraction;
		if( currentFrameFraction > 1 )
		{
			currentFrame = (currentFrame + 1) % animFramesCount[currentAnim];		
			currentFrameFraction = 0;
		}
		
		return true;
	}
	
	public void paint(Graphics g)
	{
		sprite.drawSpriteAnimFrame(currentAnim, currentFrame, Game.halfCanvasWidth + (x >> 4), Game.canvasHeight - (y >> 4), flip);
	}
	
	private boolean updateDying()
	{
		++currentFrameFraction;
		if( currentFrameFraction > 1 )
		{
			++currentFrame;		
			currentFrameFraction = 0;
		}
		
		return ( currentFrame < animFramesCount[currentAnim]-1 );
	}
	
	private void updateIdleSword()
	{
		if( Game.isKeyDown( Game.KEY_LEFT ) || Game.isKeyDown( Game.KEY_4 ) )
		{
			changeAnim(KnightAnim.walk_sword);
			flip = Sprite.TRANS_MIRROR;
		}		
		else if( Game.isKeyDown( Game.KEY_RIGHT ) || Game.isKeyDown( Game.KEY_6 ) )
		{
			changeAnim(KnightAnim.walk_sword);
			flip = Sprite.TRANS_NONE;
		}
		else if( Game.isKeyDown( Game.KEY_8 ) || Game.isKeyDown( Game.KEY_DOWN ) )
		{
			Game.playSfx(Game.SFX_SHOOT);
			changeAnim(KnightAnim.attack_bow);
		}
		else if( Game.isKeyDown( Game.KEY_5 ) || Game.isKeyDown( Game.KEY_CENTER ) )
		{
			Game.playSfx(Game.SFX_SWORD);
			changeAnim(KnightAnim.attack_sword);
		}
	}
	
	private void updateIdleBow()
	{
		if( Game.isKeyDown( Game.KEY_LEFT ) || Game.isKeyDown( Game.KEY_4 ) )
		{
			changeAnim(KnightAnim.walk_bow);
			flip = Sprite.TRANS_MIRROR;
		}		
		else if( Game.isKeyDown( Game.KEY_RIGHT ) || Game.isKeyDown( Game.KEY_6 ) )
		{
			changeAnim(KnightAnim.walk_bow);
			flip = Sprite.TRANS_NONE;
		}
		else if( Game.isKeyDown( Game.KEY_8 ) || Game.isKeyDown( Game.KEY_DOWN ) )
		{
			Game.playSfx(Game.SFX_SHOOT);
			changeAnim(KnightAnim.attack_bow);
		}
		else if( Game.isKeyDown( Game.KEY_5 ) || Game.isKeyDown( Game.KEY_CENTER ) )
		{
			Game.playSfx(Game.SFX_SWORD);
			changeAnim(KnightAnim.attack_sword);
		}
	}
	
	private void updateAttackBow()
	{
		if( currentFrameFraction == 0 && currentFrame == (animFramesCount[currentAnim] >> 1) )
		{
			ProjectilesManager.Instance.create(ProjectilesAnim.ARROW, x, y);
		}
	
		if( Game.isKeyDown( Game.KEY_8 ) || Game.isKeyDown( Game.KEY_DOWN ) )
		{
			return;
		}
		
		if( currentFrame == animFramesCount[currentAnim] - 1 )	
		{
			changeAnim(KnightAnim.idle_bow);
		}
	}
	
	private void updateAttackSword()
	{
		if( currentFrameFraction == 0 && currentFrame == (animFramesCount[currentAnim] >> 1) )
		{
			int x1, x2;
			if( flip == Sprite.TRANS_NONE )
			{
				x1 = x;
				x2 = x + HalfWidth + AttackRange;
			}
			else
			{
				x1 = x - HalfWidth - AttackRange;
				x2 = x;
			}
			
			int hitCount = EnemiesManager.Instance.checkHit(x1, y, x2, y + (HalfHeight << 1), hitResult);
			for( int i=0; i<hitCount; ++i )
			{
				Enemy hitEnemy = hitResult[i];
				
				hitEnemy.takeHit(1);
				
				final int targetHalfWidth = hitEnemy.properties.HalfWidth;
				final int targetHalfHeight = hitEnemy.properties.Height >> 1;
				
				final int fuzzyX = -(targetHalfWidth >> 2) + (Math.abs(EnemiesManager.Instance.random.nextInt()) % (targetHalfWidth >> 1));
				final int fuzzyY = -(targetHalfHeight >> 2) + (Math.abs(EnemiesManager.Instance.random.nextInt()) % (targetHalfHeight >> 1));
		
				Projectile prj = ProjectilesManager.Instance.create(ProjectilesAnim.SLASH_HIT, hitEnemy.x + fuzzyX, hitEnemy.y + targetHalfHeight + fuzzyY);
				prj.flip = this.flip;
			}
			
			if( hitCount > 0 )
			{
				Game.playSfx(Game.SFX_IMPACT1);
			}
		}
	
		if( Game.isKeyDown( Game.KEY_5 ) || Game.isKeyDown( Game.KEY_CENTER ) )
		{
			return;
		}
		
		if( currentFrame == animFramesCount[currentAnim] - 1 )	
		{
			changeAnim(KnightAnim.idle_sword);
		}
	}
	
	private void updateWalkBow()
	{
		if( !updateWalk() )
		{
			changeAnim(KnightAnim.idle_bow);
		}
	}
	
	private void updateWalkSword()
	{
		if( !updateWalk() )
		{
			changeAnim(KnightAnim.idle_sword);
		}
	}
	
	private boolean updateWalk()
	{
		if( Game.isKeyDown( Game.KEY_LEFT ) || Game.isKeyDown( Game.KEY_4 ) )
		{
			if( accelerate > -24 )
			{
				accelerate = -24;				
			}
			else
			{
				accelerate -= 3;
				if( accelerate < -48 )
				{
					accelerate = -48;
				}
			}
			
			flip = Sprite.TRANS_MIRROR;
			x += accelerate;			
		}		
		else if( Game.isKeyDown( Game.KEY_RIGHT ) || Game.isKeyDown( Game.KEY_6 ) )
		{
			if( accelerate < 24 )
			{
				accelerate = 24;				
			}
			else
			{
				accelerate += 3;				
				if( accelerate > 48 )
				{
					accelerate = 48;
				}
			}
			
			flip = Sprite.TRANS_NONE;
			x += accelerate;
		}
		else
		{
			accelerate = 0;
			return false;
		}
		
		if( x < -(Game.halfCanvasWidth << 4) )
		{
			x = -(Game.halfCanvasWidth << 4);
		}
		else if( x > (Game.halfCanvasWidth << 4) )
		{
			x = (Game.halfCanvasWidth << 4);
		}
		
		return true;
	}
	
	private void changeAnim(int id)
	{
		if( currentAnim != id )
		{
			currentAnim = id;
			currentFrame = 0;
			currentFrameFraction = 0;
		}
	}
	
	public int deserialize(byte[] data, int offset)
	{
		// serialized
		if( data != null )
		{
			x = Util.bytes2Short(data, offset);
			offset += 2;
			flip = data[offset] == 0 ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
			++offset;
			HP = data[offset];
			++offset;
		
			// new offset
			return offset;
		}
		
		// empty
		return 0;
	}	
	
	public int serialize(byte[] data, int offset)
	{
		Util.short2Bytes(data, offset, x);
		offset += 2;
		data[offset] = (byte)(flip == Sprite.TRANS_NONE ? 0 : 1);
		++offset;
		data[offset] = (byte)HP;
		++offset;
		return offset;
	}
	
	public void takeHit(int damage)
	{
		HP -= damage;
	}
	
	public boolean checkHit(int x1, int y1, int x2, int y2)
	{
		final int enemyX1 = x - HalfWidth;
		final int enemyX2 = x + HalfWidth;
		final int enemyY1 = y;
		final int enemyY2 = y + (HalfHeight << 1);
		
		return( !( x1 > enemyX2 || x2 < enemyX1 || y1 > enemyY2 || y2 < enemyY1 ) );		
	}
	
	private int[] animFramesCount = new int[7]; 
	
	private ASprites sprite;
	private int	currentAnim;
	private int currentFrame;
	private int currentFrameFraction;
	
	public int HalfWidth;
	public int HalfHeight;
	public int AttackRange;
		
	public int x;
	public int y;
	private int flip;
	private int accelerate;
	public int HP;
	
	public int score;
	
	private Enemy[] hitResult = new Enemy[8];
}
