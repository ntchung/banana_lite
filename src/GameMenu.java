
#include "Game_Config.h"
package PACKAGE_NAME;

import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.ToneControl;
import java.io.*;
import java.util.*;

import java.io.IOException;
import java.io.InputStream;


class GameMenu
{
	public final static int kBarTypeShort = 0;
	public final static int kBarTypeMedium = 1;
	public final static int kBarTypeSaveGame = 2;
	public final static int kBarTypeTiny = 3;

	public int barType;
	public int[] stringId;
	public int count;

	public int x;
	public int y;
	public int select;

	public int soundItem;

	private static boolean wasTouchDown;
	private static boolean wasTouchInScope;

	public static ASprites sprButtons;
	public static AFont font;

	public static final int BUTTON_WIDTH  = 80;
	public static final int ITEM_HEIGHT = 48;

	private Font bigFont;
	private Calendar calendar;

	public GameMenu( int num, int nbarType )
	{
		barType = nbarType;
		
		stringId = new int[num];
		count = num;

		soundItem = -1;
		select = 0;
		init();		

		bigFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM );
		calendar = Calendar.getInstance();
	}

	public void init()
	{
		wasTouchDown = false;
		select = 0;
	}

	private final void checkTouch( int touchX, int touchY )
	{
		wasTouchInScope = false;
	
		final int startY = y - (ITEM_HEIGHT >> 1);
		if(((x-BUTTON_WIDTH) < touchX) && (touchX < (x + (BUTTON_WIDTH))) && (startY<touchY))
		{
			int k = ( touchY - startY ) / ITEM_HEIGHT;
			final int next = k < count-1 ? k : count-1;
			for( int i=0; i<=next; i++ )
			{
				if( stringId[i] < 0 )
				{
					k++;
				}
			}		
			
			if( k >= 0 && k < count )
			{
				select = k;
				wasTouchInScope = true;
			}
		}				
	}

	public boolean update( boolean isOk, boolean isPrior, boolean isNext, boolean isTouchDown, int touchX, int touchY )
	{
		boolean isConfirmed = isOk;

		if( isTouchDown )	
		{
			checkTouch( touchX, touchY );
			wasTouchDown = true;			
		}
		else if( wasTouchDown )
		{			
			checkTouch( touchX, touchY );			
			if( wasTouchInScope )
			{
				isConfirmed = true;
			}
			wasTouchDown = false;
		}

		if( !isConfirmed )
		{
		   if( isPrior )
		   {
		       if( select > 0 )
		       {
		           select--;
		       }
				
		       if( stringId[select] < 0 )
		       {
		           if( select > 0 )
		           {
		               select--;
		           }
		           else 
		           {
		               select++;
		           }
		       }
		   }
			
		   if( isNext )
		   {
		       if( select < count-1 )
		       {
		           select++;
		       }

		       if( stringId[select] < 0 )
		       {
		           if( select < count-1 )
		           {
		               select++;
		           }
		           else
		           {
		               select--;
		           }
		       }
		   }
		}

		return isConfirmed;
	}

	private final void drawItem( int n, int x, int y )
	{
		sprButtons.drawSpriteFrame( select == n ? ((wasTouchDown && wasTouchInScope) ? Buttons.RECT_BUTTON_DOWN : Buttons.RECT_BUTTON_HOVER) : Buttons.RECT_BUTTON_NORMAL, x, y );		

		final int halfFontHeight = Game.font.fontHeight >> 1;
		
		if( soundItem == n )
		{
			if( Game.optionSound == 1 )
			{
				font.drawString( stringId[n], x, y - halfFontHeight, AFont.kAlignCenter );		
			}
			else
			{
				font.drawString( stringId[n] + 1, x, y - halfFontHeight, AFont.kAlignCenter );		
			}
		}			
		else
		{
			font.drawString( stringId[n], x, y - halfFontHeight, AFont.kAlignCenter );		
		}
	}

	public void paint( int nx, int ny )
	{
		x = nx;
		y = ny;

		int y0 = y;
		for( int i=0; i<count; i++ )
		{
			if( stringId[i] >= 0 )
			{
				drawItem( i, x, y0 );
				y0 += ITEM_HEIGHT;
			}
		}
	}

	public void setItem( int n, int nstringId )
	{
		stringId[n] = nstringId;
	}
}

