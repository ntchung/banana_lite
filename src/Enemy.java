
#include "Game_Config.h"

package PACKAGE_NAME;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.lcdui.game.Sprite;
import java.util.Vector;

class Enemy
{
	#include "EnemyMelee.h"

	public Enemy()
	{
		isActive = false;
	}
	
	public void reset(int ntype)
	{
		isActive = true;	
		type = ntype;		
		
		sprite = EnemiesManager.Instance.spritesPool[type];
		animFramesCount = EnemiesManager.Instance.animFramesCount[type];
		properties = EnemiesManager.Instance.EnemyPropertiesPool[type];
		
		x = 0;
		y = 0;			
		isBehindWall = false;
		
		currentAnim = 0;
		currentFrame = 0;
		currentFrameFraction = 0;
		flip = Sprite.TRANS_NONE;	
	}

	public void update()
	{	
		switch( type )
		{
		case EnemiesManager.TYPE_GOBLIN:
		case EnemiesManager.TYPE_TROLL:
			updateMelee();
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
		// serialized
		if( data != null )
		{
			type = data[offset+0];
			reset(type);
			
			x = Util.bytes2Short(data, offset+1);
			y = Util.bytes2Short(data, offset+3);
			flip = data[offset+5] == 0 ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
			isBehindWall = data[offset+6] == 0 ? false : true;			
			
			// new offset
			return offset + 7;
		}
		
		return offset;
	}	
	
	public int serialize(byte[] data, int offset)
	{
		data[offset+0] = (byte)type;
		Util.short2Bytes(data, offset+1, x);
		Util.short2Bytes(data, offset+3, y);
		data[offset+5] = (byte)(flip == Sprite.TRANS_NONE ? 0 : 1);
		data[offset+6] = (byte)(isBehindWall ? 1 : 0);		
		return offset + 7;
	}	
	
	public boolean isActive;	
	public boolean isBehindWall;
	
	private ASprites sprite;
	private byte[] animFramesCount;
	private int type;
	private int	currentAnim;
	private int currentFrame;
	private int currentFrameFraction;
	public int x;
	public int y;
	public int flip;
	public EnemyProperties properties;
}
