
private boolean needLoadGameData = true;

private void initInGame()
{	
	if( needLoadGameData )
	{
		loadGameData();
		needLoadGameData = false;
	}

	setLeftSoftkey(kSoftkeyIGM);
	setRightSoftkey(kSoftkeyNone);
	
	playerCharacter.loadAssets();
}

private void destroyInGame()
{
}

private void updateInGame()
{
	playerCharacter.update();
	
	if( isLeftSK )
	{
		saveGameData();
		changeState( k_State_MainMenu );
	}	
}

private void paintInGame()
{	
	currentGraphics.setColor( 0x70c7ed );
	currentGraphics.fillRect( 0, 0, canvasWidth, canvasHeight - wallHeight );

	playerCharacter.paint(currentGraphics);
	
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

public final void SaveGame(byte[] data, int offset)
{
	offset = playerCharacter.serialize(data, offset);
}

public final void LoadGame(byte[] data, int offset)
{
	offset = playerCharacter.deserialize(data, offset);
}
