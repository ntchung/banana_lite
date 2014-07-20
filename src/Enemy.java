
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
	#include "EnemyFlyer.h"

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
		HP = properties.MaxHP;
		
		currentAnim = 0;
		currentFrame = 0;
		currentFrameFraction = 0;
		flip = Sprite.TRANS_NONE;	
		
		redecide();
	}

	public boolean update()
	{		
		boolean res = true;
		
		switch( type )
		{
		case EnemiesManager.TYPE_GOBLIN:
		case EnemiesManager.TYPE_TROLL:
			res = updateMelee();
			break;
			
		case EnemiesManager.TYPE_RAVEN:
			res = updateFlyer();
			break;
		}
	
		++currentFrameFraction;
		if( currentFrameFraction > 1 )
		{
			currentFrame = (currentFrame + 1) % animFramesCount[currentAnim];		
			currentFrameFraction = 0;
		}		
		
		return res;
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
			currentFrameFraction = 0;
		}
	}
	
	public int deserialize(byte[] data, int offset)
	{		
		// serialized
		if( data != null )
		{
			redecide();
		
			type = data[offset];
			++offset;
			currentAnim = data[offset];
			++offset;
			currentFrame = data[offset];
			++offset;
			currentFrameFraction = data[offset];
			++offset;		
			x = Util.bytes2Short(data, offset);
			offset += 2;
			y = Util.bytes2Short(data, offset);
			offset += 2;
			flip = data[offset] == 0 ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
			++offset;
			isBehindWall = data[offset] == 0 ? false : true;			
			++offset;
			HP = data[offset];
			++offset;
			decisionCountdown = Util.bytes2Short(data, offset);
			offset += 2;
			
			isActive = true;			
			sprite = EnemiesManager.Instance.spritesPool[type];
			animFramesCount = EnemiesManager.Instance.animFramesCount[type];
			properties = EnemiesManager.Instance.EnemyPropertiesPool[type];			
			
			// new offset
			return offset;
		}
		
		return offset;
	}	
	
	public int serialize(byte[] data, int offset)
	{
		data[offset] = (byte)type;
		++offset;
		data[offset] = (byte)currentAnim;
		++offset;
		data[offset] = (byte)currentFrame;		
		++offset;
		data[offset] = (byte)currentFrameFraction;				
		++offset;
		Util.short2Bytes(data, offset, x);
		offset += 2;
		Util.short2Bytes(data, offset, y);
		offset += 2;
		data[offset] = (byte)(flip == Sprite.TRANS_NONE ? 0 : 1);
		++offset;
		data[offset] = (byte)(isBehindWall ? 1 : 0);		
		++offset;
		data[offset] = (byte)HP;
		++offset;
		Util.short2Bytes(data, offset, decisionCountdown);
		offset += 2;
		return offset;
	}	
	
	public void takeHit(int damage)
	{
		HP -= damage;
	}
	
	private void redecide()
	{
		decisionCountdown = properties.MinDecisionCountdown + (Math.abs(EnemiesManager.Instance.random.nextInt()) % (properties.MaxDecisionCountdown - properties.MinDecisionCountdown + 1));
		decisionX = (-(Game.halfCanvasWidth - 10) + (Math.abs(EnemiesManager.Instance.random.nextInt()) % (Game.canvasWidth - 20))) << 4;
	}
	
	public boolean isActive;	
	public boolean isBehindWall;
	
	private ASprites sprite;
	private byte[] animFramesCount;
	public int type;
	public int currentAnim;
	private int currentFrame;
	private int currentFrameFraction;
	
	public int x;
	public int y;
	public int flip;
	public EnemyProperties properties;
	public int HP;
	
	private int decisionCountdown;
	private int decisionX;
}
