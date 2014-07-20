
#include "Game_Config.h"

package PACKAGE_NAME;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.lcdui.game.Sprite;
import java.util.Vector;

class Projectile
{
	#include "ProjectileArrow.h"
	#include "ProjectileExplosion.h"

	public Projectile()
	{
	}
	
	public void reset(int ntype, int nx, int ny)
	{	
		type = ntype;		
		
		animFramesCount = ProjectilesManager.Instance.animFramesCount[type];
		
		x = nx;
		y = ny;			
		
		currentAnim = type;
		currentFrame = 0;
		currentFrameFraction = 0;
		flip = Sprite.TRANS_NONE;	
	}

	public boolean update()
	{			
		boolean res = true;
	
		switch( type )
		{
			case ProjectilesAnim.ARROW:
				res = updateArrow();
			break;
			
			case ProjectilesAnim.ARROW_HIT:
			case ProjectilesAnim.SMALL_HIT:
			case ProjectilesAnim.SLASH_HIT:
			case ProjectilesAnim.RAVEN_HIT:
			case ProjectilesAnim.BIG_HIT:
				res = updateExplosion();
			break;
		}
	
		++currentFrameFraction;
		if( currentFrameFraction > 1 )
		{
			currentFrame = (currentFrame + 1) % animFramesCount;		
			currentFrameFraction = 0;
		}
		
		return res;
	}
	
	public void paint(Graphics g)
	{
		final ASprites sprite = ProjectilesManager.Instance.mainSprite;
		sprite.drawSpriteAnimFrame(currentAnim, currentFrame, Game.halfCanvasWidth + (x >> 4), Game.canvasHeight - (y >> 4), flip);
	}
	
	public int deserialize(byte[] data, int offset)
	{		
		// serialized
		if( data != null )
		{
			type = data[offset];
			++offset;						
			currentFrame = data[offset];
			++offset;						
			x = Util.bytes2Short(data, offset);
			offset += 2;
			y = Util.bytes2Short(data, offset);
			offset += 2;
			flip = data[offset] == 0 ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
			++offset;
						
			currentAnim = type;
			animFramesCount = ProjectilesManager.Instance.animFramesCount[type];
			currentFrameFraction = 0;
		
			// new offset
			return offset;
		}
		
		return offset;
	}	
	
	public int serialize(byte[] data, int offset)
	{
		data[offset] = (byte)type; 
		++offset;
		data[offset] = (byte)currentFrame;
		++offset;
		Util.short2Bytes(data, offset, x);
		offset += 2;
		Util.short2Bytes(data, offset, y);		
		offset += 2;
		data[offset] = (byte)(flip == Sprite.TRANS_NONE ? 0 : 1);
		++offset;
		
		return offset;
	}	
	
	public void Copy(Projectile other)
	{
		this.type = other.type;
		this.animFramesCount = other.animFramesCount;
		this.currentAnim = other.currentAnim;
		this.currentFrame = other.currentFrame;
		this.currentFrameFraction = other.currentFrameFraction;
		this.x = other.x;
		this.y = other.y;
		this.flip = other.flip;
	}
	
	private int type;
	private int animFramesCount;
	private int	currentAnim;
	private int currentFrame;
	private int currentFrameFraction;
	
	public int x;
	public int y;
	public int flip;
}
