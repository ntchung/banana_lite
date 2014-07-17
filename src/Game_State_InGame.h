
private boolean needLoadGameData = true;

private void initInGame()
{	
	setLeftSoftkey(kSoftkeyIGM);
	setRightSoftkey(kSoftkeyNone);
	
	playerCharacter.loadAssets();
	enemiesManager.loadAssets();
	projectilesManager.loadAssets();
	
	if( needLoadGameData )
	{
		loadGameData();
		needLoadGameData = false;
	}
}

private void destroyInGame()
{
}

private void updateInGame()
{
	playerCharacter.update();
	enemiesManager.update();
	projectilesManager.update();
	
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
	enemiesManager.paintBehindWall(currentGraphics);
	
	currentGraphics.drawImage( imgWall, 
		( canvasWidth - imgWall.getWidth() ) >> 1, 
		( canvasHeight - imgWall.getHeight() ), 0 );			
		
	enemiesManager.paintBeforeWall(currentGraphics);
	projectilesManager.paint(currentGraphics);
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
	offset = enemiesManager.serialize(data, offset);
}

public final void LoadGame(byte[] data, int offset)
{
	try
	{
		offset = playerCharacter.deserialize(data, offset);
		offset = enemiesManager.deserialize(data, offset);
	}
	catch( Exception ex )
	{		
	}
}
