#include "Game_Config.h"
package PACKAGE_NAME;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Graphics.*;
import javax.microedition.lcdui.*;
import java.io.*;

class ASprites
{
	private final static int kLoadingBufferSize = 8 * 1024;
	private static byte[] data1 = new byte[kLoadingBufferSize];
	
	public static int spriteClipX1, spriteClipY1, spriteClipX2, spriteClipY2;
	public static Graphics currentGraphics;
	
	short modulesCount;
	short framesCount;
	short frameModulesCount;
	short animsCount;
	short animFramesCount;
	
	short modulesDataOffset;
	short frameModulesDataOffset;
	short framesDataOffset;
	short animFramesDataOffset;
	short animsDataOffset;	
	
	public byte[] data;
	short imageWidth;
	short imageHeight;
	
	public Image imageData;
	
	private static int transformedX;
	private static int transformedY;
	private final static void calculateTransformedXY( int transform, int x, int y, int w, int h )
	{
		switch( transform )
		{
		case Sprite.TRANS_ROT90:
			transformedX = -h-y;
			transformedY = x;
		break;
		
		case Sprite.TRANS_ROT180:
			transformedX = -w-x;
			transformedY = -h-y;
		break;
		
		case Sprite.TRANS_ROT270:
			transformedX = y;
			transformedY = -w-x;
		break;

		case Sprite.TRANS_MIRROR:
			transformedX = -w - x;
			transformedY = y;
			break;
		
		default:
			transformedX = x;
			transformedY = y;
		break;
		}
	}
	
	public void ASprites()
	{
	}
	
	public static void setSpriteClip( int x1, int y1, int x2, int y2 )
	{
		spriteClipX1 = x1;
		spriteClipY1 = y1;
		spriteClipX2 = x2;
		spriteClipY2 = y2;
	}
	
	public static void drawImageNormal( Image image, int x, int y, int moduleX, int moduleY, int moduleW, int moduleH, int flags )
	{
		int diff;
		
		if( flags == Sprite.TRANS_NONE )
		{
			// Clip X
			if( x < spriteClipX1 )
			{
				diff = spriteClipX1 - x;
				x = spriteClipX1;		
				moduleW -= diff;		
				moduleX += diff;
			}
			if( x + moduleW >= spriteClipX2 )
			{
				diff = x + moduleW - spriteClipX2;
				moduleW -= diff;		
			}
			if( moduleW <= 0 )
			{
				return;
			}
			else if( moduleW > ( spriteClipX2 - spriteClipX1 ) )
			{
				moduleW = spriteClipX2 - spriteClipX1;
			}
		}
		
		if( flags == Sprite.TRANS_NONE || flags == Sprite.TRANS_MIRROR )
		{
			// Clip Y
			if( y < spriteClipY1 )
			{
				diff = spriteClipY1 - y;
				moduleH -= diff;
				y = spriteClipY1;		
				moduleY += diff;
			}
			if( y + moduleH >= spriteClipY2 )
			{
				diff = y + moduleH - spriteClipY2;
				moduleH -= diff;
			}
			if( moduleH <= 0 )
			{
				return;
			}
			else if( moduleH > ( spriteClipY2 - spriteClipY1 ) )
			{
				moduleH = spriteClipY2 - spriteClipY1;
			}
		}
		
		currentGraphics.drawRegion( image, moduleX, moduleY, moduleW, moduleH, flags, x, y, 0 );
	}
	
	public void destroy()
	{
		data = null;
		imageData = null;
	}

	public void loadSprite(String imageFileName, String spriteFileName)
	{
		int size;
		int offset;
		int readSize;
		
		InputStream stream = null;
		imageData = null;
		try
		{
			stream = "".getClass().getResourceAsStream( imageFileName );
			imageData = Image.createImage(stream);		
		}
		catch( Exception ex )
		{
			Trace(ex);
			imageData = null;
		}
		finally
		{
			if( stream != null )
			{
				try
				{
					stream.close();
				}
				catch( Exception ex )
				{
					Trace(ex);
				}
			}
		}
		stream = null;
		
		try
		{
			stream = "".getClass().getResourceAsStream( spriteFileName );
			
			offset = 0;
			do
			{
				readSize = stream.read(data1, offset, 1024);
				if( readSize > 0 )
				{
					offset += readSize;
				}
				if( readSize < 1024 )
				{
					break;
				}
			}
			while( readSize > 0 );
			size = offset;
			
			data = new byte[size];
			System.arraycopy( data1, 0, data, 0, size );
		}
		catch( Exception ex )
		{
			Trace(ex);
			data = null;
		}
		finally
		{
			if( stream != null )
			{
				try
				{
					stream.close();
				}
				catch( Exception ex )
				{
					Trace(ex);
				}
			}
		}
		stream = null;
		
		if(data != null)
		{
			modulesCount = (short)(data[0] & 0xFF);
			frameModulesCount = (short)(data[1] & 0xFF);
			framesCount = (short)(data[2] & 0xFF);
			animFramesCount = (short)(data[3] & 0xFF);
			animsCount =(short)(data[4] & 0xFF);
			modulesDataOffset = 5;
			frameModulesDataOffset = (short)(modulesDataOffset + ( modulesCount << 3 ));
			framesDataOffset = (short)(frameModulesDataOffset + (frameModulesCount * 6));
			animFramesDataOffset = (short)(framesDataOffset + ( framesCount << 1 ));
			animsDataOffset = (short)(animFramesDataOffset + ( animFramesCount * 5 ));

		}		
	}
	
	static int moduleX, moduleY, moduleW, moduleH;
	private final void retrieveModuleInfo( int id )
	{
		short tmp = (short)((modulesDataOffset + (id << 3) ));
		
		moduleX = ((data[tmp] & 0xFF) | ((data[tmp + 1])<<8));
		moduleY = ((data[tmp + 2] & 0xFF) | ((data[tmp + 3])<<8));
		moduleW = (data[tmp + 4] & 0xFF) | (((data[tmp + 5])<<8));
		moduleH = (data[tmp + 6] & 0xFF) | (((data[tmp + 7])<<8));
	}
	
	public void drawSpriteModule( int x, int y, int flags)
	{		
		drawImageNormal(imageData, x, y, moduleX, moduleY, moduleW, moduleH, flags);
	}

	public void drawModule( int id, int x, int y, int flags )
	{
		retrieveModuleInfo( id );
		drawSpriteModule( x, y, flags );
	}
	
	public void drawSpriteFrame(int id,int x, int y)
	{
		int start, end;
		short frameModuleX, frameModuleY;
		short frame = (short)(framesDataOffset + (( id << 1 )));	
		short frameModule;
		
		start = (data[frame] & 0xFF);
		end = start + (data[frame + 1] & 0xFF);
		
		while( start < end )
		{		
			frameModule = (short)((frameModulesDataOffset + ( start * 6 )));
			frameModuleX = (short)(( (data[frameModule + 2] & 0xFF) | (( (data[frameModule + 3]) << 8 ))));
			frameModuleY = (short)(( (data[frameModule + 4] & 0xFF) | (( (data[frameModule + 5]) << 8 ))));
			
			retrieveModuleInfo( (int)data[frameModule] & 0xFF );
			drawSpriteModule( x + (int)frameModuleX, (int)(y + frameModuleY), 0 );
			start++;
		}
	}
	
	public void drawSpriteFrame(int id,int x, int y, int flag)
	{
		int start, end;
		short frameModuleX, frameModuleY;
		short frame = (short)(framesDataOffset + (( id << 1 )));	
		short frameModule;
		
		start = (data[frame] & 0xFF);
		end = start + (data[frame + 1] & 0xFF);
		
		while( start < end )
		{		
			frameModule = (short)((frameModulesDataOffset + ( start * 6 )));
			frameModuleX = (short)(( (data[frameModule + 2] & 0xFF) | (( (data[frameModule + 3]) << 8 ))));
			frameModuleY = (short)(( (data[frameModule + 4] & 0xFF) | (( (data[frameModule + 5]) << 8 ))));
			
			retrieveModuleInfo( (int)data[frameModule] & 0xFF );
			calculateTransformedXY( flag, (int)frameModuleX, (int)frameModuleY, moduleW, moduleH );			
			drawSpriteModule( x + transformedX, y + transformedY, flag );
			start++;
		}
	}
	
	void drawSpriteAnimFrame(int id, int frame, int x, int y)
	{
		short animFrameX, animFrameY;
		short anim = (short)(animsDataOffset + ( id << 1 ) );	
		short animFrame = (short)(animFramesDataOffset + ( ( (data[anim] & 0xFF) + frame ) * 5 ));
		animFrameX = (short)( ((data[animFrame + 1] & 0xFF) | ( (data[animFrame + 2]) << 8 ) )); 
		animFrameY = (short)( ((data[animFrame + 3] & 0xFF) | ( (data[animFrame + 4]) << 8 ) )); 
		drawSpriteFrame( data[animFrame], x + animFrameX, y + animFrameY );
	}
	
	void drawSpriteAnimFrame(int id, int frame, int x, int y, int flag)
	{
		short animFrameX, animFrameY;
		short anim = (short)(animsDataOffset + ( id << 1 ) );	
		short animFrame = (short)(animFramesDataOffset + ( ( (data[anim] & 0xFF) + frame ) * 5 ));
		animFrameX = (short)( ((data[animFrame + 1] & 0xFF) | ( (data[animFrame + 2]) << 8 ) )); 
		animFrameY = (short)( ((data[animFrame + 3] & 0xFF) | ( (data[animFrame + 4]) << 8 ) )); 
		drawSpriteFrame( data[animFrame], x + animFrameX, y + animFrameY, flag );
	}
	
	int getAnimFrameCount(int index)
	{
		return (data[animsDataOffset + ( index << 1 ) + 1] & 0xFF);
	}

	public int getMaxModuleHeight()
	{
		int maxH = 0;
		for( int i=0; i<modulesCount; i++ )
		{
			short tmp = (short)((modulesDataOffset + (i << 3) ));
			moduleH = (data[tmp + 6] & 0xFF) | (((data[tmp + 7])<<8));
			if( moduleH > maxH )
			{
				maxH = moduleH;
			}
		}
		return maxH;
	}
	
	public int getMaxModuleWidth()
	{
		int maxW = 0;
		for( int i=0; i<modulesCount; i++ )
		{
			short tmp = (short)((modulesDataOffset + (i << 3) ));
			moduleW = (data[tmp + 4] & 0xFF) | (((data[tmp + 5])<<8));
			if( moduleW > maxW )
			{
				maxW = moduleW;
			}
		}
		return maxW;
	}
	
	public final int getModuleWidth( int id )
	{
		short tmp = (short)((modulesDataOffset + (id << 3) ));
		return (data[tmp + 4] & 0xFF) | (((data[tmp + 5])<<8));
	}

	public final int getModuleHeight( int id )
	{
		short tmp = (short)((modulesDataOffset + (id << 3) ));
		return (data[tmp + 6] & 0xFF) | (((data[tmp + 7])<<8));
	}

	public final int getFrameModuleX( int frId, int mdId )
	{
		final short frame = (short)(framesDataOffset + (( frId << 1 )));	
		final int start = (data[frame] & 0xFF) + mdId;
		final short frameModule = (short)((frameModulesDataOffset + ( start * 6 )));
		return (short)(( (data[frameModule + 2] & 0xFF) | (( (data[frameModule + 3]) << 8 ))));
	}

	public final int getFrameModuleY( int frId, int mdId )
	{
		short frame = (short)(framesDataOffset + (( frId << 1 )));			
		final int start = (data[frame] & 0xFF) + mdId;
		final short frameModule = (short)((frameModulesDataOffset + ( start * 6 )));
		return (short)(( (data[frameModule + 4] & 0xFF) | (( (data[frameModule + 5]) << 8 ))));		
	}
	
	public final int getFrameModuleWidth( int frId, int mdId )
	{
		int start, end;
		short frameModuleX, frameModuleY;
		short frame = (short)(framesDataOffset + (( frId << 1 )));	
		short frameModule;
		
		start = (data[frame] & 0xFF);
		end = start + (data[frame + 1] & 0xFF);
		
		int count = 0;
		while( start < end )
		{		
			frameModule = (short)((frameModulesDataOffset + ( start * 6 )));
			frameModuleX = (short)(( (data[frameModule + 2] & 0xFF) | (( (data[frameModule + 3]) << 8 ))));
			frameModuleY = (short)(( (data[frameModule + 4] & 0xFF) | (( (data[frameModule + 5]) << 8 ))));
						
			if( count == mdId )
			{
				retrieveModuleInfo( (int)data[frameModule] & 0xFF );			
				return moduleW;
			}
			start++;
			count++;
		}
		
		return 0;
	}

	public final int getFrameModuleHeight( int frId, int mdId )
	{
		int start, end;
		short frameModuleX, frameModuleY;
		short frame = (short)(framesDataOffset + (( frId << 1 )));	
		short frameModule;
		
		start = (data[frame] & 0xFF);
		end = start + (data[frame + 1] & 0xFF);
		
		int count = 0;
		while( start < end )
		{		
			frameModule = (short)((frameModulesDataOffset + ( start * 6 )));
			frameModuleX = (short)(( (data[frameModule + 2] & 0xFF) | (( (data[frameModule + 3]) << 8 ))));
			frameModuleY = (short)(( (data[frameModule + 4] & 0xFF) | (( (data[frameModule + 5]) << 8 ))));
						
			if( count == mdId )
			{
				retrieveModuleInfo( (int)data[frameModule] & 0xFF );			
				return moduleH;
			}
			start++;
			count++;
		}
		
		return 0;
	}

	public final int getSpriteAnimFrameModuleWidth( int id, int frame, int module )
	{
		short animFrameX, animFrameY;
		short anim = (short)(animsDataOffset + ( id << 1 ) );	
		short animFrame = (short)(animFramesDataOffset + ( ( (data[anim] & 0xFF) + frame ) * 5 ));
		animFrameX = (short)( ((data[animFrame + 1] & 0xFF) | ( (data[animFrame + 2]) << 8 ) )); 
		animFrameY = (short)( ((data[animFrame + 3] & 0xFF) | ( (data[animFrame + 4]) << 8 ) )); 

		return getFrameModuleWidth( data[animFrame], module );
	}

	public final int getSpriteAnimFrameModuleHeight( int id, int frame, int module )
	{
		short animFrameX, animFrameY;
		short anim = (short)(animsDataOffset + ( id << 1 ) );	
		short animFrame = (short)(animFramesDataOffset + ( ( (data[anim] & 0xFF) + frame ) * 5 ));
		animFrameX = (short)( ((data[animFrame + 1] & 0xFF) | ( (data[animFrame + 2]) << 8 ) )); 
		animFrameY = (short)( ((data[animFrame + 3] & 0xFF) | ( (data[animFrame + 4]) << 8 ) )); 

		return getFrameModuleHeight( data[animFrame], module );
	}

	public final void retrieveFrameAnimCounts( int[] list )
	{
		for( int i=0; i<animsCount; i++ )
		{
			list[i] = getAnimFrameCount(i);
		}
	}
}