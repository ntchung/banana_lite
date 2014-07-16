
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
		sprite.drawSpriteAnimFrame(currentAnim, currentFrame, x, y, flip);
	}
	
	private void updateIdleBow()
	{
		if( Game.isKeyDown( Game.KEY_LEFT ) || Game.isKeyDown( Game.KEY_4 ) )
		{
			changeAnim(KnightAnim.walk_bow);
			flip = Sprite.TRANS_MIRROR;
			--x;
		}		
		else if( Game.isKeyDown( Game.KEY_RIGHT ) || Game.isKeyDown( Game.KEY_6 ) )
		{
			changeAnim(KnightAnim.walk_bow);
			flip = Sprite.TRANS_NONE;
			++x;
		}
	}
	
	private void updateWalkBow()
	{
		if( Game.isKeyDown( Game.KEY_LEFT ) || Game.isKeyDown( Game.KEY_4 ) )
		{
			if( accelerate > -10 )
			{
				accelerate = -10;				
			}
			else
			{
				accelerate -= 3;
				if( accelerate < -30 )
				{
					accelerate = -30;
				}
			}
			
			flip = Sprite.TRANS_MIRROR;
			x += accelerate / 10;			
		}		
		else if( Game.isKeyDown( Game.KEY_RIGHT ) || Game.isKeyDown( Game.KEY_6 ) )
		{
			if( accelerate < 10 )
			{
				accelerate = 10;				
			}
			else
			{
				accelerate += 3;				
				if( accelerate > 30 )
				{
					accelerate = 30;
				}
			}
			
			flip = Sprite.TRANS_NONE;
			x += accelerate / 10;
		}
		else
		{
			accelerate = 0;
			changeAnim(KnightAnim.idle_bow);
		}
		
		if( x < 0 )
		{
			x = 0;
		}
		else if( x > Game.canvasWidth )
		{
			x = Game.canvasWidth;
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
		y = Game.canvasHeight - Game.wallHeight;		
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
		x = Game.canvasWidth >> 1;
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
