
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
		HP = 10;
		HalfWidth = 15 << 4;
		HalfHeight = 20 << 4;
		AttackRange = 15 << 4;
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
	}

	public void update()
	{	
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
	}
	
	public void paint(Graphics g)
	{
		sprite.drawSpriteAnimFrame(currentAnim, currentFrame, Game.halfCanvasWidth + (x >> 4), Game.canvasHeight - (y >> 4), flip);
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
			changeAnim(KnightAnim.attack_bow);
		}
		else if( Game.isKeyDown( Game.KEY_5 ) || Game.isKeyDown( Game.KEY_CENTER ) )
		{
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
			changeAnim(KnightAnim.attack_bow);
		}
		else if( Game.isKeyDown( Game.KEY_5 ) || Game.isKeyDown( Game.KEY_CENTER ) )
		{
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
			if( accelerate > -16 )
			{
				accelerate = -16;				
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
			if( accelerate < 16 )
			{
				accelerate = 16;				
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
		// default
		x = 0;
		y = Game.wallHeight << 4;		
		currentAnim = KnightAnim.idle_bow;
		currentFrame = 0;
		currentFrameFraction = 0;
		accelerate = 0;
		flip = Sprite.TRANS_NONE;

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
	private int HP;
	
	private Enemy[] hitResult = new Enemy[8];
}
