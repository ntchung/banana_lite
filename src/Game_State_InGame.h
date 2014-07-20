
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
	
	// paint action buttons
	if( hasTouch )
	{
		int x, y;
	
		x = canvasWidth - (iconWidth >> 1);
		y = canvasHeight - (iconWidth >> 1) - 8;
		if( Game.isKeyDown( Game.KEY_8 ) || Game.isKeyDown( Game.KEY_DOWN ) )
		{			
			sprButtons.drawSpriteFrame( Buttons.COMMAND_BOW_DOWN, x, y );			
		}
		else
		{
			sprButtons.drawSpriteFrame( Buttons.COMMAND_BOW_NORMAL, x, y );			
		}
		
		y -= iconWidth + 16;
		if( Game.isKeyDown( Game.KEY_5 ) || Game.isKeyDown( Game.KEY_CENTER ) )
		{			
			sprButtons.drawSpriteFrame( Buttons.COMMAND_SWORD_DOWN, x, y );			
		}
		else
		{
			sprButtons.drawSpriteFrame( Buttons.COMMAND_SWORD_NORMAL, x, y );			
		}
	}	
}

public final void SaveGame(byte[] data, int offset)
{
	offset = playerCharacter.serialize(data, offset);
	offset = enemiesManager.serialize(data, offset);
	offset = projectilesManager.serialize(data, offset);
}

public final void LoadGame(byte[] data, int offset)
{
	try
	{
		offset = playerCharacter.deserialize(data, offset);
		offset = enemiesManager.deserialize(data, offset);
		offset = projectilesManager.deserialize(data, offset);
	}
	catch( Exception ex )
	{		
	}
}
