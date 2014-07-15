
// KEY	
public static final int SOFT_KEY_LEFT = 1 << 0;  
public static final int SOFT_KEY_RIGHT = 1 << 1;  
public static final int SOFT_KEY_MIDDLE_INTERNET = 1 << 2;  

// Nokia S60
public static final int PENCIL_KEY = 1 << 3;  
public static final int DELETE_KEY = 1 << 4;  
public static final int BACK_KEY = 1 << 5;

public static final int KEY_1 = 1 << 6;  
public static final int KEY_2 = 1 << 7;  
public static final int KEY_3 = 1 << 8;  
public static final int KEY_4 = 1 << 9;  
public static final int KEY_5 = 1 << 10;  
public static final int KEY_6 = 1 << 11;  
public static final int KEY_7 = 1 << 12;  
public static final int KEY_8 = 1 << 13;  
public static final int KEY_9 = 1 << 14;  
public static final int KEY_0 = 1 << 15;  
public static final int KEY_POUND = 1 << 16;  
public static final int KEY_STAR = 1 << 17;  

// Joystick
public static final int KEY_UP = 1 << 18;  
public static final int KEY_DOWN = 1 << 19;  
public static final int KEY_LEFT = 1 << 20;  
public static final int KEY_RIGHT = 1 << 21;  
public static final int KEY_CENTER = 1 << 22;  

public static final int NOT_DEFINED_KEY = 1 << 30;  

private static int lastKeyState;
private static int keyState;

private static int keyHoldFrame;

public static boolean isTouchDown;
public static boolean isTouchUp;
public static int touchX;
public static int touchY;
public static int touchDx;
public static int touchDy;
public static int lastTouchX;
public static int lastTouchY;

public static int lastKeyCode;

private static int convertKeyCode( int keycode )
{	
	lastKeyCode = keycode;
	switch( keycode ) 
	{  
		case Canvas.KEY_NUM0:  
		case Canvas.GAME_A:
		case 109:
		case 10:
			return KEY_0;  
		case Canvas.KEY_NUM1:  
		case 114:
		case 113:
			return KEY_1;  
		case Canvas.KEY_NUM2:  
		case 116:
		case 119:
			return KEY_2;  
		case Canvas.KEY_NUM3:  
		case 121:
		case 101:
			return KEY_3;  
		case Canvas.KEY_NUM4:  
		case 102:
		case 97:
			return KEY_4;  
		case Canvas.KEY_NUM5:  
		case 103:
		case 115:
		case 108:
			return KEY_5;  
		case Canvas.KEY_NUM6:  
		case 104:
		case 100:
			return KEY_6;  
		case Canvas.KEY_NUM7:  
		case 118:
		case 122:
			return KEY_7;  
		case Canvas.KEY_NUM8:  
		case 98:
		case 120:
			return KEY_8;  
		case Canvas.KEY_NUM9:  
		case 110:
		case 99:
			return KEY_9;  
		case Canvas.KEY_STAR:  
		case Canvas.GAME_C:
		case 117:
		case 112:
			return KEY_STAR;  
		case Canvas.GAME_D:
		case Canvas.KEY_POUND:  
		case 106:
		case 8:
			return KEY_POUND;  
			
		default:  
			if (keycode == Util.SOFTKEY_LEFT) 
			{  
				return SOFT_KEY_LEFT;  
			} 
			else if (keycode == Util.SOFTKEY_RIGHT) 
			{  
				return SOFT_KEY_RIGHT;  
			} 
			else if (keycode == Util.SOFTKEY_DELETE) 
			{  
				return DELETE_KEY;  
			} 
			else if (keycode == Util.SOFTKEY_BACK) 
			{  
				return BACK_KEY;  
			} 
			else if (keycode == Util.SOFTKEY_MIDDLE_INTERNET) 
			{  
				return SOFT_KEY_MIDDLE_INTERNET;  
			} 
			else if (keycode == Util.PENCIL_KEY_NOKIA) 
			{  
				return PENCIL_KEY;  
			} 
			else 
			{  
				try 
				{  
					final int gameAction;  
					gameAction = Util.adaptorCanvas.getGameAction( keycode );
					if (gameAction == Canvas.UP) 
					{  
						return KEY_UP;  
					} 
					else if (gameAction == Canvas.DOWN) 
					{  
						return KEY_DOWN;  
					} 
					else if (gameAction == Canvas.LEFT) 
					{  
						return KEY_LEFT;  
					} 
					else if (gameAction == Canvas.RIGHT) 
					{  
						return KEY_RIGHT;  
					} 
					else if (gameAction == Canvas.FIRE) 
					{  
						return KEY_CENTER;  
					}  
				} 
				catch (IllegalArgumentException ex) 
				{  
					Trace(ex);
				}  
			}  
			
			// try scan codes
			/*final int scanCode = Util.getKeyScancode();
			
			switch( scanCode )
			{
			case 131:
				return KEY_1;  
			case 132:
				return KEY_2; 
			case 133:
				return KEY_3; 
			case 147:
				return KEY_4;
			case 148:
				return KEY_5;
			case 149:
				return KEY_6;  
			case 163:
				return KEY_7; 
			case 164:
				return KEY_8; 
			case 165:
				return KEY_9; 
			case 166:
				return KEY_0; 
			case 134:
				return KEY_POUND; 
			case 150:
				return KEY_STAR; 
			}
			*/
			break;  
	}  
	
	return NOT_DEFINED_KEY;  
}

public static void onKeyPressed( int keycode )
{
	keyHoldFrame = 0;
	lastKeyState = keyState;
	keyState |= convertKeyCode( keycode );	
}

public static void onKeyReleased( int keycode )
{
	keyHoldFrame = 0;
	lastKeyState = keyState;
	keyState &= ~( convertKeyCode( keycode ) );
}

public static void onGameKeyPressed( int keycode )
{
	keyHoldFrame = 0;
	lastKeyState = keyState;
	keyState |= keycode;	
}

public static void onGameKeyReleased( int keycode )
{
	if( isKeyDown( keycode ) )
	{
		keyHoldFrame = 0;
		lastKeyState = keyState;
		keyState &= ~( keycode );
	}
}

public static boolean isKeyDown( int n )
{
	return ( keyState & ( n ) ) == ( n );
}

public static boolean isKeyPressed( int n )
{
	return ( ( lastKeyState & ( n ) ) == ( n ) ) && ( ( keyState & ( n ) ) == 0 );
}

public static boolean isAnyKeyDown()
{
	return keyState != 0;
}

public static boolean isAnyKeyPressed()
{
	return lastKeyState != 0;
}

public static void resetKeyState()
{
	lastKeyState = 0;
	isTouchUp = false;
}

public static void resetAllKeys()
{
	keyState = 0;
	lastKeyState = 0;
	keyHoldFrame = 0;
	isTouchDown = false;
	isTouchUp = false;
	lastTouchX = 0;
	lastTouchY = 0;
	wasTouchDownInState = false;
}

private final static int softkeyTouchWidth = 42;

public void onPointerPressed( int x, int y )
{
	boolean isHandled = false;
	
	Dbg( "onPointerPressed" );
	
	if( y > canvasHeight - 42 )
	{
		if( x < softkeyTouchWidth )
		{
			onGameKeyPressed( SOFT_KEY_LEFT );
			isHandled = true;
		}
		else if( x > canvasWidth - softkeyTouchWidth )
		{
			onGameKeyPressed( SOFT_KEY_RIGHT );
			isHandled = true;
		}
	}
	
	// TODO
	
	/*
	if( !isHandled )
	{
		isHandled = checkTouchSelectLevelButtons( true, x, y );
	}
	
	if( !isHandled )
	{
		isHandled = checkTouchIGM( true, x, y );
	}
		
	if( !isHandled && gameState == k_State_InGame && ( isIGM == 0 ) )	
	{
		isHandled = chessBoard.onPointerPressed( x, y );		
	}	
	*/
	
	if( !isHandled )	
	{
		isTouchDown = true;
		isTouchUp = false;
		touchX = x;	
		touchY = y;
		lastTouchX = x;
		lastTouchY = y;
		touchDx = 0;
		touchDy = 0;
	}
}

public void onPointerDragged( int x, int y )
{
	boolean isHandled = false;
	
	if( y > canvasHeight - 42 )
	{
		if( x < softkeyTouchWidth )
		{
			onGameKeyPressed( SOFT_KEY_LEFT );
			isHandled = true;
		}
		else if( x > ( canvasWidth - softkeyTouchWidth ) )
		{
			onGameKeyPressed( SOFT_KEY_RIGHT );
			isHandled = true;
		}
		else		
		{
			onGameKeyReleased( SOFT_KEY_LEFT );
			onGameKeyReleased( SOFT_KEY_RIGHT );
		}
	}
	
	/*if( !isHandled )	
	{
		onGameKeyReleased( SOFT_KEY_LEFT );
		onGameKeyReleased( SOFT_KEY_RIGHT );
		
		isHandled = checkTouchSelectLevelButtons( true, x, y );
	}*/
	
	// TODO
	
	/*if( !isHandled )
	{
		isHandled = checkTouchIGM( true, x, y );
	}
	
	if( !isHandled && gameState == k_State_InGame && ( isIGM == 0 ) )	
	{
		isHandled = chessBoard.onPointerDragged( x, y );		
	}	
	*/
	
	if( !isHandled )
	{
		onGameKeyReleased( SOFT_KEY_LEFT );
		onGameKeyReleased( SOFT_KEY_RIGHT );
		
		isTouchDown = true;
		isTouchUp = false;
		touchX = x;	
		touchY = y;		
		
		if( touchX != lastTouchX && touchY != lastTouchY )
		{
			touchDx = touchX - lastTouchX;
			touchDy = touchY - lastTouchY;
		}
	}
}

public void onPointerReleased( int x, int y )
{
	onGameKeyReleased( SOFT_KEY_LEFT );
	onGameKeyReleased( SOFT_KEY_RIGHT );
		
	//untouchSelectLevelButtons( x, y );
	//untouchIGM( x, y );
	
	// TODO
	
	/*if( gameState == k_State_InGame && ( isIGM == 0 ) )	
	{
		chessBoard.untouch( x, y );		
	}*/	
	
	isTouchDown = false;
	isTouchUp = true;
	touchX = x;	
	touchY = y;
	lastTouchX = x;
	lastTouchY = y;
	touchDx = 0;
	touchDy = 0;
}

private final boolean isKeyMenuConfirm()
{
	return isLeftSK || isKeyPressed( KEY_5 ) || isKeyPressed( KEY_CENTER );
}

private final boolean isKeyMenuPrior()
{
	return isKeyPressed( KEY_2 ) || isKeyPressed( KEY_UP );
}

private final boolean isKeyMenuNext()
{
	return isKeyPressed( KEY_8 ) || isKeyPressed( KEY_DOWN );
}
