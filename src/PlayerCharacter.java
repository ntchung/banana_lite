
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
	public PlayerCharacter()
	{
		sprite = null;
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
	}
	
	private void updateAttackBow()
	{
		if( Game.isKeyDown( Game.KEY_8 ) || Game.isKeyDown( Game.KEY_DOWN ) )
		{
			return;
		}
		
		if( currentFrame == animFramesCount[currentAnim] - 1 )	
		{
			changeAnim(KnightAnim.idle_bow);
		}
	}
	
	private void updateWalkBow()
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
			changeAnim(KnightAnim.idle_bow);
		}
		
		if( x < -(Game.halfCanvasWidth << 4) )
		{
			x = -(Game.halfCanvasWidth << 4);
		}
		else if( x > (Game.halfCanvasWidth << 4) )
		{
			x = (Game.halfCanvasWidth << 4);
		}
	}
	
	private void changeAnim(int id)
	{
		if( currentAnim != id )
		{
			currentAnim = id;
			currentFrame = 0;
		}
	}
	
	public int deserialize(byte[] data, int offset)
	{
		// default
		y = Game.wallHeight << 4;		
		currentAnim = KnightAnim.idle_bow;
		currentFrame = 0;
		currentFrameFraction = 0;
		accelerate = 0;

		// serialized
		if( data != null )
		{
			x = Util.bytes2Short(data, offset+0);
			flip = data[offset+2] == 0 ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
		
			// new offset
			return offset + 3;
		}
		
		// empty
		currentAnim = KnightAnim.idle_bow;
		x = 0;
		flip = Sprite.TRANS_NONE;
		
		return 0;
	}	
	
	public int serialize(byte[] data, int offset)
	{
		Util.short2Bytes(data, offset+0, x);
		data[offset+2] = (byte)(flip == Sprite.TRANS_NONE ? 0 : 1);
		return offset + 3;
	}
	
	private int[] animFramesCount = new int[7]; 
	
	private ASprites sprite;
	private int	currentAnim;
	private int currentFrame;
	private int currentFrameFraction;
	private int x;
	private int y;
	private int flip;
	private int accelerate;
}
