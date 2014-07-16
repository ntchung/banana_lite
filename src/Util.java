
#include "Game_Config.h"
package PACKAGE_NAME;

import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.ToneControl;
import java.io.*;
import java.util.*;

import java.io.IOException;
import java.io.InputStream;

public final class Util
{
	public static final String PLATFORM_MOTOROLA = "motorola";  
    public static final String PLATFORM_NOKIA = "nokia";  
    public static final String PLATFORM_SONY_ERICSSON = "SE";  
    public static final String PLATFORM_SIEMENS = "siemens";  
    public static final String PLATFORM_SAMSUNG = "samsung";  
    public static final String PLATFORM_LG = "LG";  
    public static final String PLATFORM_NOT_DEFINED = "NA";  

	public static Canvas adaptorCanvas;
	
	public static String PLATFORM_NAME;
	public static int SOFTKEY_LEFT;  
    public static int SOFTKEY_RIGHT;  
    public static int SOFTKEY_MIDDLE_INTERNET;  
    public static int SOFTKEY_DELETE;  
    public static int SOFTKEY_BACK;  
	
    // standard values for softkeys of different platforms used only in predefining 
    private static final int SOFT_KEY_LEFT_SE = -6;  
    private static final int SOFT_KEY_RIGHT_SE = -7;  
    private static final int DELETE_KEY_SE = -8;  
    private static final int INTERNET_KEY_SE = -10;  
    private static final int BACK_KEY_SE = -11;  
    private static final int SOFT_KEY_LEFT_SAMSUNG = -6;  
    private static final int SOFT_KEY_RIGHT_SAMSUNG = -7;  
    private static final int DELETE_KEY_SAMSUNG = -8;  
    private static final int SOFT_KEY_LEFT_SIEMENS = -1;  
    private static final int SOFT_KEY_RIGHT_SIEMENS = -4;  
    private static final int SOFT_KEY_LEFT_NOKIA = -6;  
    private static final int SOFT_KEY_RIGHT_NOKIA = -7;  
    private static final int DELETE_KEY_NOKIA = -8;  
    public static final int PENCIL_KEY_NOKIA = -50;  
    private static final int SOFT_KEY_LEFT_MOTOROLA = -21;  
    private static final int SOFT_KEY_RIGHT_MOTOROLA = -22;  
    private static final int SOFT_KEY_LEFT_MOTOROLA2 = -20;  
    private static final int SOFT_KEY_LEFT_MOTOROLA1 = 21;  
    private static final int SOFT_KEY_RIGHT_MOTOROLA1 = 22;  
    private static final int SOFT_KEY_MIDLE_MOTOROLA = -23;  
    private static final int SOFT_KEY_MIDLE_NOKIA = -5;  
  
    private static final String SOFT_WORD = "SOFT";  

	private static boolean isNokiaQwerty = false;
	private static boolean hasNokiaScancode = false;

	public static int getKeyScancode()
	{
		int keyScanCode = 0;
		if( hasNokiaScancode )
		{
			try 
			{
				keyScanCode = Integer.parseInt(System.getProperty("com.nokia.key.scancode"));
			}
			catch( NumberFormatException nfe ) 
			{
			}
		}

		return keyScanCode;
	}
	
	public static void getPlatform( Canvas canvas )
	{  
		adaptorCanvas = canvas;

		try
		{
			final String type = System.getProperty("com.nokia.keyboard.type");
			if( type != null )
			{
				if( type.equals( "HalfKeyboard" ) || type.equals( "FullKeyboard" ) ||
					type.equals( "LimitedKeyboard4x10" ) || type.equals( "LimitedKeyboard3x11" ) )
				{
					isNokiaQwerty = true;					
				}
				else
				{
					isNokiaQwerty = false;
				}
			}

			final String test = System.getProperty("com.nokia.key.scancode");
			if( test != null )
			{
				hasNokiaScancode = true;
			}
			else
			{						
				hasNokiaScancode = false;
			}
		}
		catch( Throwable ex )
		{
			isNokiaQwerty = false;
			hasNokiaScancode = false;
		}
		
		// detecting NOKIA or SonyEricsson  
		try 
		{  
			final String currentPlatform = System.getProperty("microedition.platform"); 
			if (currentPlatform.indexOf("Nokia") != -1) 
			{  
				PLATFORM_NAME = PLATFORM_NOKIA;  
				return;
			} 
			else if (currentPlatform.indexOf("SonyEricsson") != -1) 
			{  
				PLATFORM_NAME = PLATFORM_SONY_ERICSSON;  
				return;
			}  
		} 
		catch (Throwable ex) 
		{  
		}  
		
		// detecting SAMSUNG  
		try 
		{  
			Class.forName("com.samsung.util.Vibration");  
			PLATFORM_NAME = PLATFORM_SAMSUNG;  
			return;
		} 
		catch (Throwable ex) 
		{  
		}  
		
		// detecting MOTOROLA  
		try {  
			Class.forName("com.motorola.multimedia.Vibrator");  
			PLATFORM_NAME = PLATFORM_MOTOROLA;  
			return;
		} 
		catch (Throwable ex) 
		{  
			try 
			{  
				Class.forName("com.motorola.graphics.j3d.Effect3D");  
				PLATFORM_NAME = PLATFORM_MOTOROLA;  
				return;
			} 
			catch (Throwable ex2) 
			{  
				try 
				{  
					Class.forName("com.motorola.multimedia.Lighting");  
					PLATFORM_NAME = PLATFORM_MOTOROLA;  
					return;
				} 
				catch (Throwable ex3) 
				{  
					try 
					{  
						Class.forName("com.motorola.multimedia.FunLight");  
						PLATFORM_NAME = PLATFORM_MOTOROLA;  
						return;
					} 
					catch (Throwable ex4) 
					{  
					}  
				}  
			}  
		}  
		try 
		{  
			if (adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA).toUpperCase().indexOf(SOFT_WORD) > -1) 
			{  
				PLATFORM_NAME = PLATFORM_MOTOROLA;  
				return;
			}  
		} 
		catch (Throwable e) 
		{  
			try 
			{  
				if (adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase().indexOf(SOFT_WORD) > -1) 
				{  
					PLATFORM_NAME = PLATFORM_MOTOROLA;  
					return;
				}  
			} 
			catch (Throwable e1) 
			{  
				try 
				{  
					if (adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA2).toUpperCase().indexOf(SOFT_WORD) > -1) 
					{  
						PLATFORM_NAME = PLATFORM_MOTOROLA;  
						return;
					}  
				} 
				catch (Throwable e2) 
				{  
				}  
			}  
		}  
		
		// detecting SIEMENS  
		try 
		{  
			Class.forName("com.siemens.mp.io.File");  
			PLATFORM_NAME = PLATFORM_SIEMENS;  
			return;
		} 
		catch (Throwable ex) 
		{  
		}  
		
		// detecting LG  
		try 
		{  
			Class.forName("mmpp.media.MediaPlayer");  
			PLATFORM_NAME = PLATFORM_LG;  
			return;
		} 
		catch (Throwable ex) 
		{  
			try 
			{  
				Class.forName("mmpp.phone.Phone");  
				PLATFORM_NAME = PLATFORM_LG;  
				return;				
			} 
			catch (Throwable ex1) 
			{  
				try 
				{  
					Class.forName("mmpp.lang.MathFP");  
					PLATFORM_NAME = PLATFORM_LG;  
					return;
				} 
				catch (Throwable ex2) 
				{  
					try 
					{  
						Class.forName("mmpp.media.BackLight");  
						PLATFORM_NAME = PLATFORM_LG;
						return;
					} 
					catch (Throwable ex3) 
					{  
					}  
				}  
			}  
		}  
		
		PLATFORM_NAME = PLATFORM_NOT_DEFINED;  
		
		 
    }
	public static void initKeyAdaptor( Canvas canvas )
	{
		getPlatform(canvas);
		SOFTKEY_LEFT = getLeftSoftkeyCode();  
        SOFTKEY_RIGHT = getRightSoftkeyCode();  
        SOFTKEY_MIDDLE_INTERNET = getMidleORInternetSoftkeyCode();  
        SOFTKEY_DELETE = getDeleteKeyCode();  
        SOFTKEY_BACK = getBackKeyCode(); 
	}
	private static int getLeftSoftkeyCode() 
	{  
        int keyCode = 0;  
        try 
		{  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) 
			{  
                String softkeyLeftMoto = "";  
                try 
				{  
                    softkeyLeftMoto = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA).toUpperCase();  
                } 
				catch (IllegalArgumentException ilae) 
				{  
                }  
                String softkeyLeftMoto1 = "";  
                try 
				{  
                    softkeyLeftMoto1 = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase();  
                } 
				catch (IllegalArgumentException ilae) 
				{  
                }  
                String softkeyLeftMoto2 = "";  
                try 
				{  
                    softkeyLeftMoto2 = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA2).toUpperCase();  
                } 
				catch (IllegalArgumentException ilae) 
				{  
                }  
                if (softkeyLeftMoto.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto.indexOf("1") >= 0) 
				{  
                    return SOFT_KEY_LEFT_MOTOROLA;  
                } 
				else if (softkeyLeftMoto1.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto1.indexOf("1") >= 0) 
				{  
                    return SOFT_KEY_LEFT_MOTOROLA1;  
                } 
				else if (softkeyLeftMoto2.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto2.indexOf("1") >= 0) 
				{  
                    return SOFT_KEY_LEFT_MOTOROLA2;  
                } 
				else if (softkeyLeftMoto.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto.indexOf("LEFT") >= 0) 
				{  
                    return SOFT_KEY_LEFT_MOTOROLA;  
                } 
				else if (softkeyLeftMoto1.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto1.indexOf("LEFT") >= 0) 
				{  
                    return SOFT_KEY_LEFT_MOTOROLA1;  
                } 
				else if (softkeyLeftMoto2.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto2.indexOf("LEFT") >= 0) 
				{  
                    return SOFT_KEY_LEFT_MOTOROLA2;  
                }  
  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) 
			{  
                return SOFT_KEY_LEFT_NOKIA;  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) 
			{  
                return SOFT_KEY_LEFT_SAMSUNG;  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) 
			{  
                String leftKeySiemensName = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_SIEMENS).toUpperCase();  
                if (leftKeySiemensName.indexOf(SOFT_WORD) >= 0) 
				{  
                    if (leftKeySiemensName.indexOf("1") >= 0) 
					{  
                        return SOFT_KEY_LEFT_SIEMENS;  
                    } 
					else if (leftKeySiemensName.indexOf("LEFT") >= 0) 
					{  
                        return SOFT_KEY_LEFT_SIEMENS;  
                    }  
                }  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) 
			{  
                return SOFT_KEY_LEFT_SE;  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_NOT_DEFINED)) 
			{  
                for (int i = -125; i <= 125; i++) 
				{  
                    if (i == 0) 
					{  
                        i++;  
                    }  
					
                    final String s = adaptorCanvas.getKeyName(i).toUpperCase();  
                    if (s.indexOf(SOFT_WORD) >= 0) 
					{  
                        if (s.indexOf("1") >= 0) 
						{  
                            keyCode = i;  
                            break;  
                        }  
                        if (s.indexOf("LEFT") >= 0) 
						{  
                            keyCode = i;  
                            break;  
                        }  
                    }  
                }  
            }  
            if (keyCode == 0) 
			{  
                return SOFT_KEY_LEFT_NOKIA;  
            }  
        } catch (Throwable iaEx) 
		{  
            return SOFT_KEY_LEFT_NOKIA;  
        }  
        return keyCode;  
    }  
  
    private static int getRightSoftkeyCode() 
	{  
        int keyCode = 0;  
        try 
		{  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) 
			{  
  
                String rightSoftMoto1 = "";  
                try 
				{  
                    rightSoftMoto1 = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase();  
                } 
				catch (IllegalArgumentException ilae) 
				{  
                }  
                String rightSoftMoto = "";  
                try 
				{  
                    rightSoftMoto = adaptorCanvas.getKeyName(SOFT_KEY_RIGHT_MOTOROLA).toUpperCase();  
                } 
				catch (IllegalArgumentException ilae) 
				{  
                }  
                String rightSoftMoto2 = "";  
                try 
				{  
                    rightSoftMoto2 = adaptorCanvas.getKeyName(SOFT_KEY_RIGHT_MOTOROLA1).toUpperCase();  
                } 
				catch (IllegalArgumentException ilae) 
				{  
                }  
                if (rightSoftMoto.indexOf(SOFT_WORD) >= 0 && rightSoftMoto.indexOf("2") >= 0) 
				{  
                    return SOFT_KEY_RIGHT_MOTOROLA;  
                } 
				else if (rightSoftMoto1.indexOf(SOFT_WORD) >= 0 && rightSoftMoto1.indexOf("2") >= 0) 
				{  
                    return SOFT_KEY_RIGHT_MOTOROLA;  
                } 
				else if (rightSoftMoto2.indexOf(SOFT_WORD) >= 0 && rightSoftMoto2.indexOf("2") >= 0) 
				{  
                    return SOFT_KEY_RIGHT_MOTOROLA1;  
                } 
				else if (rightSoftMoto.indexOf(SOFT_WORD) >= 0 && rightSoftMoto.indexOf("RIGHT") >= 0) 
				{  
                    return SOFT_KEY_LEFT_MOTOROLA;  
                } 
				else if (rightSoftMoto1.indexOf(SOFT_WORD) >= 0 && rightSoftMoto1.indexOf("RIGHT") >= 0) 
				{  
                    return SOFT_KEY_RIGHT_MOTOROLA1;  
                } 
				else if (rightSoftMoto2.indexOf(SOFT_WORD) >= 0 && rightSoftMoto2.indexOf("RIGHT") >= 0) 
				{  
                    return SOFT_KEY_RIGHT_MOTOROLA;  
                }  
  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) 
			{  
                return SOFT_KEY_RIGHT_NOKIA;  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) 
			{
                return SOFT_KEY_RIGHT_SAMSUNG;  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) 
			{  
                String rightSoftSiemens = adaptorCanvas.getKeyName(SOFT_KEY_RIGHT_SIEMENS).toUpperCase();  
                if (rightSoftSiemens.indexOf(SOFT_WORD) >= 0) 
				{  
                    if (rightSoftSiemens.indexOf("4") >= 0) 
					{  
                        return SOFT_KEY_RIGHT_SIEMENS;  
                    } 
					else if (rightSoftSiemens.indexOf("RIGHT") >= 0) 
					{  
                        return SOFT_KEY_RIGHT_SIEMENS;  
                    }  
                }  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) 
			{  
                return SOFT_KEY_RIGHT_SE;  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_NOT_DEFINED)) 
			{  
                for (int i = -125; i <= 125; i++) 
				{  
                    if (i == 0) 
					{  
                        i++;  
                    }  
                    String keyName = adaptorCanvas.getKeyName(i).toUpperCase();  
                    if (keyName.indexOf(SOFT_WORD) >= 0) 
					{  
                        if (keyName.indexOf("2") >= 0) 
						{  
                            keyCode = i;  
                            break;  
                        } 
						else if (keyName.indexOf("4") >= 0) 
						{  
                            keyCode = i;  
                            break;  
                        } 
						else if (keyName.indexOf("RIGHT") >= 0) 
						{  
                            keyCode = i;  
                            break;  
                        }  
                    }  
                }  
            }  
        } 
		catch (Throwable iaEx) 
		{  
            return SOFT_KEY_RIGHT_NOKIA;  
        }  
        return keyCode;  
    }  
  
    private static int getMidleORInternetSoftkeyCode() 
	{  
        try 
		{  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) 
			{  
                if (adaptorCanvas.getKeyName(SOFT_KEY_MIDLE_MOTOROLA).toUpperCase().indexOf("SOFT") >= 0) 
				{  
                    return SOFT_KEY_MIDLE_MOTOROLA;  
                }  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) 
			{  
                if (adaptorCanvas.getKeyName(SOFT_KEY_MIDLE_NOKIA).toUpperCase().indexOf("SOFT") >= 0) 
				{  
                    return SOFT_KEY_MIDLE_NOKIA;  
                }  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) 
			{  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) 
			{  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) 
			{  
                return INTERNET_KEY_SE;  
            }  
        } 
		catch (Throwable e) 
		{  
        }  
        return 0;  
    }  
  
    private static int getDeleteKeyCode() 
	{  
        try 
		{  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) 
			{  
  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) 
			{  
                if (adaptorCanvas.getKeyName(DELETE_KEY_SE).toUpperCase().indexOf("CLEAR") >= 0) 
				{  
                    return DELETE_KEY_NOKIA;  
                } 
				else 
				{  
                    return DELETE_KEY_NOKIA;  
                }  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) 
			{  
                if (adaptorCanvas.getKeyName(DELETE_KEY_SAMSUNG).toUpperCase().indexOf("CLEAR") >= 0) 
				{  
                    return DELETE_KEY_SAMSUNG;  
                }  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) 
			{  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) 
			{  
                if (adaptorCanvas.getKeyName(DELETE_KEY_SE).toUpperCase().indexOf("CLEAR") >= 0) 
				{  
                    return DELETE_KEY_SE;  
                } 
				else if (adaptorCanvas.getKeyName(DELETE_KEY_SE).toUpperCase().indexOf("C") >= 0) 
				{  
                    return DELETE_KEY_SE;  
                } 
				else 
				{  
                    return DELETE_KEY_SE;  
                }  
            }  
        } 
		catch (Throwable e) 
		{  
            return DELETE_KEY_SE;  
        }  
        return 0;  
    }  
  
    private static int getBackKeyCode() 
	{  
        try 
		{  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) 
			{  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) 
			{  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) 
			{  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) 
			{  
            } 
			else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) 
			{  
                return BACK_KEY_SE;  
            }  
        } 
		catch (Throwable e) 
		{  
        }  
        return 0;  
    }  
	
	// Bits and Bytes	
	public static final int bytes2Short( byte[] bytes, int offset )
	{
		int result;
		
		result = (
				  	((bytes[offset+0] & 0xff) << 8) | 
				  	( bytes[offset+1] & 0xff)
				  );
		
		return result;
	}
	
	public static final void short2Bytes( byte result[], int offset, int integer )
	{	
		result[offset+0] = (byte)(0xff & (integer >> 8));
		result[offset+1] = (byte)(0xff & integer);
	}
	
	public static final int bytes2Int( byte[] bytes, int offset )
	{
		int result;
		
		result = (
					((bytes[offset+0] & 0xff) << 24) |
		 			((bytes[offset+1] & 0xff) << 16) |
				  	((bytes[offset+2] & 0xff) << 8) | 
				  	( bytes[offset+3] & 0xff)
				  );
		
		return result;
	}
	
	public static final void int2Bytes( byte result[], int offset, int integer )
	{	
		result[offset+0] = (byte)(0xff & (integer >> 24));
		result[offset+1] = (byte)(0xff & (integer >> 16));
		result[offset+2] = (byte)(0xff & (integer >> 8));
		result[offset+3] = (byte)(0xff & integer);
	}
	
	public static final long bytes2Long( byte[] bytes, int offset ) 
	{
		long result;
		result = (
					((bytes[offset+0] & 0xff) << 56) |
					((bytes[offset+1] & 0xff) << 48) |
					((bytes[offset+2] & 0xff) << 40) |
					((bytes[offset+3] & 0xff) << 32) |
					((bytes[offset+4] & 0xff) << 24) |
					((bytes[offset+5] & 0xff) << 16) |
				  	((bytes[offset+6] & 0xff) << 8) |
				  	(bytes[offset+7] & 0xff)
				  );
		return result;
	}
	
	public static final void long2Bytes( byte result[], int offset, long input ) 
	{
		result[offset+0] = (byte)(0xff & (input >> 56));
		result[offset+1] = (byte)(0xff & (input >> 48));
		result[offset+2] = (byte)(0xff & (input >> 40));
		result[offset+3] = (byte)(0xff & (input >> 32));
		result[offset+4] = (byte)(0xff & (input >> 24));
		result[offset+5] = (byte)(0xff & (input >> 16));
		result[offset+6] = (byte)(0xff & (input >> 8));
		result[offset+7] = (byte)(0xff & input);
	}	

	// Random number
	static long rndSeed;
	static long rndX;
	static long rndY;
	static long rndZ;
	static long rndW;
	
	public static void setRNGSeed( long state )
	{
		rndSeed = rndX = state;
		rndY = 362436069;
		rndZ = 521288629;
		rndW = 88675123;
	}
		
	public static void setRNGSeedFromSystemTime()
    {
        setRNGSeed( System.currentTimeMillis() );
	}
	
	public static long getRNGLong()
	{
		long t = (rndX^(rndX<<11)); 
		rndX = rndY;
		rndY = rndZ;
		rndZ = rndW;
		rndW=(rndW^(rndW>>19))^(t^(t>>8));
		return (rndW);
	}
	
	public static int getRNGInt( int min, int max )
	{
		long t = getRNGLong() % ( max - min + 1 );
		return min + (int)t;
	}
	
	// approx distance
	public final static int getApproxDistance(int dx, int dy)
	{
		int min, max;
		if ( dx < 0 )
		{
			dx = -dx;
		}
		if ( dy < 0 )
		{
			dy = -dy;
		}
		
		if (dx < dy)
		{
			min = dx;
			max = dy;
		}
		else
		{
			min = dy;
			max = dx;
		}
		
		int res =	((( max << 8 ) + ( max << 3 ) - ( max << 4 ) - ( max << 1 ) +
				( min << 7 ) - ( min << 5 ) + ( min << 3 ) - ( min << 1 )) >> 8 );

		if( res < 1 )
		{
			res = 1;
		}

		return res;
	}

	public final static int getApproxDistanceHigh( int dx, int dy )
	{
		int min, max, approx;

		if( dx < 0 ) 
		{
		   dx = -dx;
		}
		if( dy < 0 )
		{
		   dy = -dy;
		}

		if( dx < dy )
		{
		  min = dx;
		  max = dy;
		} 
		else 
		{
		  min = dy;
		  max = dx;
		}

		approx = ( max * 1007 ) + ( min * 441 );
		if( max < ( min << 4 ) )
		{
		  approx -= ( max * 40 );
		}

		// add 512 for proper rounding
		int res = ( ( approx + 512 ) >> 10 );
		if( res < 1 )
		{
			res = 1;
		}
		return res;
	} 

	// three points are counter clockwise if returns > 0.
	public static final int calcCcwSign( int x1, int y1, int x2, int y2, int x3, int y3 )
	{
		return (x2 - x1)*(y3 - y1) - (y2 - y1)*(x3 - x1);
	}

	public final static int getIntSign( int val )
	{
		return ( ( -( val >>> 31 ) ) << 1 ) + 1;
	}
	
	// DECORS
	public static void drawShadowFont( Graphics g, String s, int x, int y, int anchor, int color1, int color2 )
	{
		g.setColor( color1 );
		g.drawString( s, x, y, anchor );
		g.setColor( color2 );		
		g.drawString( s, x-1, y-1, anchor );
	}
	
	public static void drawMenu( Graphics g, int x, int y, int w, int h, int bgcolor, int index, Font f, String[] items, int spacing, int fcolor1, int fcolor2 )
	{
		g.setColor( bgcolor );
		g.fillRect( x - w / 2, y, w, h );
		
		int i;
		
		y += f.getHeight();
		
		for( i=0; i<items.length; i++ )
		{
			if( index == i )
			{
				g.setColor( fcolor2 );
			}
			else
			{
				g.setColor( fcolor1 );
			}
			
			g.drawString( items[i], x, y, Graphics.HCENTER | Graphics.TOP );
			y += f.getHeight() + spacing;
		}
	}
	
	public static int wrapToLines( String[] lines, String text, Font f, int maxWidth ) 
	{
		int linesCount = 0;
        final boolean paragraphFormat = true;		

        if (text != null) 
		{            
			if (f.stringWidth(text) < maxWidth) 
			{
				lines[0] = text;
				return 1;
			} 
			else 
			{

				char chars[] = text.toCharArray();
				int len = chars.length;
				int count = 0;
				int charWidth = 0;
				int curLinePosStart = 0;
				int lastSpacePosition = -1;
				while (count < len) {
					if ((charWidth += f.charWidth(chars[count])) > (maxWidth - 4) || count == len - 1) // wrap to next line
					{
						if (paragraphFormat) 
						{
							String l = new String(chars, curLinePosStart, (count == len - 1) ? count - curLinePosStart + 1 : (lastSpacePosition != -1) ? lastSpacePosition + 1 - curLinePosStart : count - curLinePosStart);
							lines[linesCount] = l;
							linesCount++;
							if( linesCount == lines.length )
							{
								return linesCount;
							}
							curLinePosStart = (lastSpacePosition != -1) ? lastSpacePosition + 1: count;
							lastSpacePosition = -1;
						} 
						else 
						{
							lines[linesCount] = new String(chars, curLinePosStart, count - curLinePosStart);
							linesCount++;
							if( linesCount == lines.length )
							{
								return linesCount;
							}
							curLinePosStart = count;
						}
						charWidth = 0;
					}
					else if (chars[count] == ' ')
					{
						lastSpacePosition = count;
					}
					count++;
				}
			}
		}

		return linesCount;
    }
	
	// LOADER
	public static Image loadImage( String path )
	{
		Image image;
		InputStream stream = null;
		
		boolean isOpened = false;
		
		try
		{
			stream = "".getClass().getResourceAsStream( path );
			isOpened = true;
			
			image = Image.createImage( stream );			
		}
		catch( Exception ex )
		{
			image = null;
		}
		finally
		{
			if( stream != null )
			{
				if( isOpened )
				{
					try
					{
						stream.close();
					}
					catch( Exception ex )
					{
					}					
				}
				stream = null;
			}
		}
		
		return image;
	}
	
	public static void Paint_GradientRect( Graphics g, int x, int y, int w, int h, int colorA, int colorB )
	{
		int v0, v1, lerp, re, gr, bl;	 

		for( int i=h; i>=0; i--)
		{
			lerp = (i<<8)/h;

			v0 = ((colorB & 0x00FF0000)>>16);
			v1 = ((colorA & 0x00FF0000)>>16);
			re = v1 + (((v0 - v1) * lerp)>>8);

			v0 = ((colorB & 0x0000FF00)>>8);
			v1 = ((colorA & 0x0000FF00)>>8);
			gr = v1 + (((v0 - v1) * lerp)>>8);

			v0 = ((colorB & 0x000000FF));
			v1 = ((colorA & 0x000000FF));
			bl = v1 + (((v0 - v1) * lerp)>>8);
			
			g.setColor( 0xFF000000 | (re<<16) | (gr<<8) | bl );        
			g.drawLine( x, y + i, x + w, y + i );
		}
	}
	
	private static void SetAlphaRectColor(int[] rgbBuffer, int color, int alpha)
	{
		int newColor = ((( color ) & 0x00FFFFFF) | (((  alpha ) & 0xFF) << 24)) ;

		int len = rgbBuffer.length;
		for(int i = len-1; i >= 0; --i)
		{
			rgbBuffer[i] = newColor;
		}

	}
	
	public static void DrawAlphaRect(Graphics gfx, int x, int y, int w, int h, int color, int alpha )
	{
		DrawAlphaRect( gfx, x, y, w, h, color, alpha, false );
	}

	public static void DrawAlphaRect(Graphics gfx, int x, int y, int w, int h, int color, int alpha, boolean setClip)
	{
		final int AlphaRectBufferWidth	= (240 >>1);
		final int AlphaRectBufferHeight	= (320 >>1) ;
		int [] s_alphaRectBuffer = new int[AlphaRectBufferWidth * AlphaRectBufferHeight];

		if ( setClip )
		{
			gfx.setClip( x, y, w, h );
		}
		//Degenerate case #1: you want a fillrect (no alpha)...
		if (alpha >= 0xFF)
		{
			gfx.setColor(color);
			gfx.fillRect(x, y, w, h);
			return;
		}
		//Degenerate case #2: you want nothing (completly invisible)...
		else if (alpha <= 0)
		{
			return;
		}
		SetAlphaRectColor(s_alphaRectBuffer, color, alpha);
		int curX = x;
		int curY = y;
		int remainingW = w; 
		int remainingH = h;
		while( remainingW > 0)
		{
			curY = y;
			remainingH = h;
			while( remainingH > 0)
			{
				gfx.drawRGB( 
				s_alphaRectBuffer, 
				0, 
				AlphaRectBufferWidth, 
				curX, 
				curY, 	
				(remainingW < AlphaRectBufferWidth) ? remainingW : AlphaRectBufferWidth,
				(remainingH < AlphaRectBufferHeight) ? remainingH : AlphaRectBufferHeight,
				true);
				curY += AlphaRectBufferHeight;
				remainingH -= AlphaRectBufferHeight;
			}
			curX += AlphaRectBufferWidth;
			remainingW -= AlphaRectBufferWidth;
		}

		s_alphaRectBuffer = null;
	}

	// RMS use
	public static void intToByteArray(int value, byte[] b, int p) 
	{
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[p+i] = (byte) ((value >>> offset) & 0xFF);
		}
	}

	public static int byteArrayToInt(byte[] b, int offset) 
	{
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}	
	
	public static void fillArray( int[] arr, int val )
	{
		final int len = arr.length;
		for( int i=0; i<len; i++ )
		{
			arr[i] = val;
		}
	}

	
	//////////////////////////////////////////////////////////////////////////////
	// Chess engine
	//////////////////////////////////////////////////////////////////////////////
	public static int MIN_MAX(int min, int mid, int max) {
		return mid < min ? min : mid > max ? max : mid;
	}

	private static byte[] POP_COUNT_16 = new byte[65536];

	static {
		for (int i = 0; i < 65536; i ++) {
			int n = ((i >> 1) & 0x5555) + (i & 0x5555);
			n = ((n >> 2) & 0x3333) + (n & 0x3333);
			n = ((n >> 4) & 0x0f0f) + (n & 0x0f0f);
			POP_COUNT_16[i] = (byte) ((n >> 8) + (n & 0x00ff));
		}
	}

	public static int POP_COUNT_16(int data) {
		return POP_COUNT_16[data];
	}

	public static int readShort(InputStream in) throws IOException {
		int b0 = in.read();
		int b1 = in.read();
		if (b0 == -1 || b1 == -1) {
			throw new IOException();
		}
		return b0 | (b1 << 8);
	}

	public static int readInt(InputStream in) throws IOException {
		int b0 = in.read();
		int b1 = in.read();
		int b2 = in.read();
		int b3 = in.read();
		if (b0 == -1 || b1 == -1 || b2 == -1 || b3 == -1) {
			throw new IOException();
		}
		return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
	}

	public static class RC4 {
		public int[] state = new int[256];
		public int x, y;

		public void swap(int i, int j) {
			int t = state[i];
			state[i] = state[j];
			state[j] = t;
		}

		public RC4(byte[] key) {
			x = 0;
			y = 0;
			for (int i = 0; i < 256; i ++) {
				state[i] = i;
			}
			int j = 0;
			for (int i = 0; i < 256; i ++) {
				j = (j + state[i] + key[i % key.length]) & 0xff;
				swap(i, j);
			}
		}

		public int nextByte() {
			x = (x + 1) & 0xff;
			y = (y + state[x]) & 0xff;
			swap(x, y);
			int t = (state[x] + state[y]) & 0xff;
			return state[t];
		}

		public int nextLong() {
			int n0, n1, n2, n3;
			n0 = nextByte();
			n1 = nextByte();
			n2 = nextByte();
			n3 = nextByte();
			return n0 + (n1 << 8) + (n2 << 16) + (n3 << 24);
		}
	}

	public static int binarySearch(int vl, int[] vls, int from, int to) {
		int low = from;
		int high = to - 1;
		while (low <= high) {
			int mid = (low + high) / 2;
			if (vls[mid] < vl) {
				low = mid + 1;
			} else if (vls[mid] > vl) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -1;
	}

	private static final int[] SHELL_STEP = {0, 1, 4, 13, 40, 121, 364, 1093};

	public static void shellSort(int[] mvs, int[] vls, int from, int to) {
		int stepLevel = 1;
		while (SHELL_STEP[stepLevel] < to - from) {
			stepLevel ++;
		}
		stepLevel --;
		while (stepLevel > 0) {
			int step = SHELL_STEP[stepLevel];
			for (int i = from + step; i < to; i ++) {
				int mvBest = mvs[i];
				int vlBest = vls[i];
				int j = i - step;
				while (j >= from && vlBest > vls[j]) {
					mvs[j + step] = mvs[j];
					vls[j + step] = vls[j];
					j -= step;
				}
				mvs[j + step] = mvBest;
				vls[j + step] = vlBest;
			}
			stepLevel --;
		}
	}
	
	// Unique ID	
	public static String getIMEI() 
	{
		String out = "";
		try {
			out = System.getProperty("com.imei");
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("phone.imei");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.nokia.IMEI");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.nokia.mid.imei");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.sonyericsson.imei");
			}
	 
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("IMEI");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.motorola.IMEI");
			}
	 
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.samsung.imei");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.siemens.imei");
			}
	 
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("imei");
			}
	 
		} catch (Exception e) {
			return out == null ? "" : out;
		}
		return out == null ? "" : out;
	}
	
	//code for getting IMSI of the phone
	public static String getIMSI() 
	{
		String out = "";
		try {
			out = System.getProperty("IMSI");
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("phone.imsi");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.nokia.mid.mobinfo.IMSI");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.nokia.mid.imsi");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("IMSI");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("imsi");
			}
		} catch (Exception e) {
			return out == null ? "" : out;
		}
		return out == null ? "" : out;
	}

	// Others
	public static final boolean isPtInRect( int x, int y, int x1, int y1, int x2, int y2 )
	{
		return !( x < x1 || x > x2 || y < y1 || y > y2 );
	}	
}

