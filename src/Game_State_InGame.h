
private void initInGame()
{	
	setLeftSoftkey(kSoftkeyIGM);
	setRightSoftkey(kSoftkeyNone);
}

private void destroyInGame()
{
}

private void updateInGame()
{
	if( isLeftSK )
	{
		SaveGame();
		changeState( k_State_MainMenu );
	}
}

private void paintInGame()
{	
	currentGraphics.setColor( 0x70c7ed );
	currentGraphics.fillRect( 0, 0, canvasWidth, canvasHeight - wallHeight );

	currentGraphics.drawImage( imgWall, 
		( canvasWidth - imgWall.getWidth() ) >> 1, 
		( canvasHeight - imgWall.getHeight() ), 0 );			
}

private final boolean checkTouchIGM( boolean isDown, int x, int y )
{
	return false;
}

private final void untouchIGM( int x, int y )
{
	
}

public final void SaveGame()
{
}

public final void LoadGame(byte[] data)
{
}
