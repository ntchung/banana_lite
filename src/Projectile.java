
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

	public void update()
	{			
		++currentFrameFraction;
		if( currentFrameFraction > 1 )
		{
			currentFrame = (currentFrame + 1) % animFramesCount;		
			currentFrameFraction = 0;
		}
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
			// new offset
			return offset;
		}
		
		return offset;
	}	
	
	public int serialize(byte[] data, int offset)
	{
		return offset;
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
