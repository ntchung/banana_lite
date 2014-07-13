#include "Game_Config.h"
package PACKAGE_NAME;

import javax.microedition.lcdui.game.Sprite;
import java.io.*;

class AFont
{
	private short stringLen[];
	private byte string[];
	private int stringOffset[];
	private byte[] temp;
	public int fontHeight;
	
	private int charSpace;
	private int charBackslash;
	private int charN;
	private int charV;
	private int[] numericChars; // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -
	private byte[] versionString = new byte[8];
	private int versionStringLength;

	private ASprites sprite;	
	
	#include "stringdef.h"
	#include "fonttable.h"
	
	public final static int	kAlignLeft = 0;
	public final static int kAlignRight = 1;
	public final static int kAlignCenter = 2;
	
	public AFont()
	{
		stringLen = new short[STR_TOTAL];
		stringOffset = new int[STR_TOTAL];
		string = new byte[STR_TOTAL*64];
		temp = new byte[32];
		numericChars = new int[11];
		
		sprite = new ASprites();
	}
	
	public void destroy()
	{
		sprite.destroy();
	}

	public void createVersionNumber( String str )
	{
		if( str != null )
		{
			createGameString( versionString, str );
			versionStringLength = str.length();
		}
	}
	
	public void loadFont( String imageName, String spriteName )
	{
		destroy();
				
		sprite.loadSprite( imageName, spriteName );
		fontHeight = sprite.getMaxModuleHeight() + 1;
	}
	
	public void loadString(String packName)
	{
		int size = 0;
		int offset = 0;
		int readSize = 0;
		
		InputStream stream = null;
		try
		{
			int soffset = 0;
			stream = "".getClass().getResourceAsStream( packName );			
			
			for ( int i = 0; i < STR_TOTAL; i++ )
			{
				// read char count
				readSize = stream.read( temp, 0, 2 );
				stringLen[i] = (short)(((temp[1] & 0xFF) << 8) | (temp[0] & 0xFF));
				offset += readSize;
				
				// read string
				readSize = stream.read( string, soffset, stringLen[i]);
				stringOffset[i] = soffset;
				soffset += stringLen[i];
				offset += readSize;
			}
			
			charSpace = charToFrameID( ' ' );
			charBackslash = charToFrameID( '\\' );
			charN = charToFrameID( 'n' );
			charV = charToFrameID( 'v' );
			numericChars[0] = charToFrameID( '0' );
			numericChars[1] = charToFrameID( '1' );
			numericChars[2] = charToFrameID( '2' );
			numericChars[3] = charToFrameID( '3' );
			numericChars[4] = charToFrameID( '4' );
			numericChars[5] = charToFrameID( '5' );
			numericChars[6] = charToFrameID( '6' );
			numericChars[7] = charToFrameID( '7' );
			numericChars[8] = charToFrameID( '8' );
			numericChars[9] = charToFrameID( '9' );
			numericChars[10] = charToFrameID( '-' );		
		}
		catch( Exception ex )
		{
			Trace(ex);
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
	}
	
	public void drawString( int strID, int x, int y, int flags )
	{
		if ( strID < 0 || strID > STR_TOTAL )
			return;
			
		final int soffset = stringOffset[strID];
		drawString( string, soffset, x, y, flags );
	}

	public void drawString( byte[] buffer, int offset, int x, int y, int flags )
	{
		int width = 0;
		int c = 0;
		int i = 0;
			
		int soffset = offset;
		
		// check flags
		if ( (flags & kAlignRight) != 0 )
		{
			c = buffer[soffset+i] & 0xFF;
			
			// calculate width
			while ( c != 255 )
			{
				width += g_charAdvance[c];
				i++;
				c = buffer[soffset+i] & 0xFF;
			}
			x -= width;
		}
		else if ( (flags & kAlignCenter) != 0 )
		{
			c = buffer[soffset+i] & 0xFF;
			
			// calculate width
			while ( c != 255 )
			{
				width += g_charAdvance[c];
				i++;
				c = buffer[soffset+i] & 0xFF;
			}
			x -= (width >> 1);
		}

		// draw
		i = 0;
		c = 0;
		c = buffer[soffset+i] & 0xFF;
		while ( c != 255 )
		{
			sprite.drawSpriteFrame( c, x, y );
			x += g_charAdvance[c];
			i++;
			c = buffer[soffset+i] & 0xFF;
		}
	}
	
	public int measureWordWidth( byte[] buffer, int soffset )
	{
		int w = 0;
		int c = 255;
		int i = 0;
		
		do
		{
			c = buffer[soffset+i] & 0xFF;	
			if( c != 255 )
			{
				if( c == charSpace || c == charBackslash )
				{
					w += g_charAdvance[c];
					break;
				}
				w += g_charAdvance[c];
				i++;
			}
		}
		while( c != 255 );
		
		return w;
	}

	public int countStringWrap( int strID, int x, int y, int width )
	{
		if ( strID < 0 || strID > STR_TOTAL )
			return 0;
		
		final int soffset = stringOffset[strID];		
		return countStringWrap( string, soffset, x, y, width );
	}

	public int countStringWrap( byte[] buffer, int offset, int x, int y, int width )
	{
		boolean cr = false; // carriage - return
		int w;
		int x0;
		int c;
		int i;
		int soffset = offset;
		int count = 1;
		
		x0 = x;
		i = 0;
		c = 255;
		
		do
		{	
			cr = false;
			do
			{
				c = buffer[soffset+i] & 0xFF;	
				if( c != 255 )
				{												
					if( c == charSpace )
					{
						x0 += g_charAdvance[c];
						i++;
						break;
					}
					else if( c == charBackslash )
					{
						i++;
						c = buffer[soffset+i] & 0xFF;	
						if( c == charN )
						{
							i++;
							cr = true;
						}
						else if( c == charV )
						{
							// TODO...
							i++;
						}
						break;
					}
					
					x0 += g_charAdvance[c];
					
					i++;
				}					
			}
			while( c != 255 );
			
			w = measureWordWidth( buffer, soffset + i );			
			if( x0 + w > x + width || cr )
			{
				x0 = x;
				count++;
			}
		}
		while( c != 255 );

		return count;
	}
	
	public void drawStringWrap( int strID, int x, int y, int width )
	{
		if ( strID < 0 || strID > STR_TOTAL )
			return;
		
		boolean cr = false; // carriage - return
		int w;
		int x0;
		int c;
		int i;
		int soffset = stringOffset[strID];
		
		x0 = x;
		i = 0;
		c = 255;
		
		do
		{	
			cr = false;
			do
			{
				c = string[soffset+i] & 0xFF;	
				if( c != 255 )
				{												
					if( c == charSpace )
					{
						x0 += g_charAdvance[c];
						i++;
						break;
					}
					else if( c == charBackslash )
					{
						i++;
						c = string[soffset+i] & 0xFF;	
						if( c == charN )
						{
							i++;
							cr = true;
						}
						else if( c == charV )
						{
							// TODO...
							i++;
						}
						break;
					}
					
					sprite.drawSpriteFrame( c, x0, y );
					x0 += g_charAdvance[c];
					
					i++;
				}					
			}
			while( c != 255 );
			
			w = measureWordWidth( string, soffset + i );			
			if( x0 + w > x + width || cr )
			{
				x0 = x;
				y += fontHeight;
			}
		}
		while( c != 255 );
	}
	
	public int drawStringWrapCenter( int strID, int x, int y, int width )
	{
		if ( strID < 0 || strID > STR_TOTAL )
			return y;		
		final int soffset = stringOffset[strID];
		return drawStringWrapCenter( string, soffset, x, y, width );
	}

	public int drawStringWrapCenter( byte[] buffer, int offset, int x, int y, int width )
	{
		boolean cr = false; // carriage - return
		int w;
		int x0;
		int c;
		int i;
		int j;
		int x1;
		int soffset = offset;
		
		x0 = x;
		i = 0;
		j = 0;
		c = 255;
			
		do
		{			
			cr = false;
			do
			{
				c = buffer[soffset+i] & 0xFF;	
				if( c != 255 )
				{						
					if( c == charSpace )
					{
						x0 += g_charAdvance[c];
						i++;
						break;
					}
					else if( c == charBackslash )
					{
						i++;
						c = buffer[soffset+i] & 0xFF;	
						if( c == charN )
						{
							i++;
							cr = true;
						}
						else if( c == charV )
						{
							x0 += versionStringLength * g_charAdvance[c];
							i++;
						}
						break;
					}
					
					x0 += g_charAdvance[c];
					i++;
				}					
			}
			while( c != 255 );
			
			w = measureWordWidth( buffer, soffset + i );			
			if( x0 + w > ( x + width ) || cr )
			{
				int nn = i;
				if( cr ) 
				{
					cr = false;				
					nn -= 2;
				}				
				x1 = x + ( ( width - ( x0 - x ) ) >> 1 );
				for( int k=j; k<nn; k++ )
				{
					c = buffer[soffset+k] & 0xFF;	
					if( c != 255 )
					{
						if( c == charBackslash )
						{
							k++;
							c = buffer[soffset+k] & 0xFF;
							if( c == charV )
							{
								drawString( versionString, 0, x1, y, 0 );
							}
							else if( c == charBackslash )
							{
								sprite.drawSpriteFrame( c, x1, y );
								x1 += g_charAdvance[c];
							}
						}
						else
						{
							sprite.drawSpriteFrame( c, x1, y );
							x1 += g_charAdvance[c];
						}
					}	
				}
				
				j = i;
				x0 = x;
				y += fontHeight;

				if( y > ASprites.spriteClipY2 )
				{
					return y;
				}
			}
		}
		while( c != 255 );

		x1 = x + ( ( width - ( x0 - x ) ) >> 1 );
		for( int k=j; k<i; k++ )
		{
			c = buffer[soffset+k] & 0xFF;	
			if( c != 255 )
			{
				if( c == charBackslash )
				{
					k++;
					c = buffer[soffset+k] & 0xFF;
					if( c == charV )
					{
						drawString( versionString, 0, x1, y, 0 );
					}
					else if( c == charBackslash )
					{
						sprite.drawSpriteFrame( c, x1, y );
						x1 += g_charAdvance[c];
					}
				}
				else
				{
					sprite.drawSpriteFrame( c, x1, y );
					x1 += g_charAdvance[c];
				}	
			}	
		}

		return y;
	}
	
	private int charToFrameID(char value)
	{
		int start = 0;
		int end = g_fontTable.length - 1;
		int pos = (start + end) >> 1;
		
		while (start < end)
		{
			if (g_fontTable[pos] == value)
				return pos;

			if (g_fontTable[pos] < value)
				start = pos;

			if (g_fontTable[pos] > value)
				end = pos;

			pos = (start + end) >> 1;
		}
		if (g_fontTable[start] == value)
			return start;

		return -1;
	}

	public void createGameString( byte[] buffer, String source )
	{
		int len = source.length();
		final int bufferLength = buffer.length - 2;
		if( len > bufferLength )
		{
			len = bufferLength;
		}

		int i;
		for( i=0; i<len; i++ )
		{
			buffer[i] = (byte)charToFrameID(source.charAt(i));
		}
		buffer[i] = -1;
	}
	
	public void drawNumber(int number, int x, int y, int flags)
	{
		int character;
		short digitCount = 0;
		boolean isNegative = false;
		
		if (number < 0)
		{
			number = -number;
			isNegative = true;
		}

		if( number == 0 )
		{
			temp[0] = (byte)(0);
			digitCount++;
		}
		else
		{
			while (number > 0)
			{
				temp[digitCount++] = (byte)((number % 10));
				number /= 10;
			}
		}
			
		if (isNegative)
			temp[digitCount++] = 10; // '-'

		int j;
		int w = 0;
		switch (flags)
		{
		case kAlignCenter:
		{
			j = digitCount;
			while (j > 0)
			{
				j--;
				character = numericChars[(int)(temp[j])];
				w += g_charAdvance[character];
			}
			
			x -= ( w >> 1 );
			while (digitCount > 0)
			{
				digitCount--;
				character = numericChars[(int)(temp[digitCount])];
				sprite.drawSpriteFrame(character, x, y, 0);
				x += g_charAdvance[character];
			}
		}
		break;
		case kAlignLeft:
			while (digitCount > 0)
			{
				digitCount--;
				character = numericChars[(int)(temp[digitCount])];
				sprite.drawSpriteFrame(character, x, y, 0);
				x += g_charAdvance[character];
			}
			break;
		case kAlignRight:
			{
				char i = 0;
				while (i < digitCount)
				{
					character = numericChars[(int)(temp[i])];
					x -= g_charAdvance[character];
					sprite.drawSpriteFrame(character, x, y, 0);
					i++;
				}
			}
			break;
		}
	}
	
}