
private boolean needLoadGameData = true;
private int dyingCounter = 0;

private void initInGame()
{	
	setLeftSoftkey(kSoftkeyIGM);
	setRightSoftkey(kSoftkeyNone);
	
	playerCharacter.loadAssets();
	enemiesManager.loadAssets();
	projectilesManager.loadAssets();
	
	if( needLoadGameData )
	{
		resetGame();
		loadGameData();
		needLoadGameData = false;
	}
}

public void resetGame()
{
	playerCharacter.reset();
	enemiesManager.reset();
	projectilesManager.reset();
}

private void destroyInGame()
{
}

private void updateInGame()
{
	if( !playerCharacter.update() )
	{
		resetGame();
		changeState( k_State_MainMenu );
		
		++dyingCounter;
		if( dyingCounter >= 5 )
		{
			dyingCounter = 0;
			
			gameCanvas.midlet.showMidAds();
		}
	}
	
	if( playerCharacter.HP <= 0 )
	{
		return;
	}
	
	enemiesManager.update();
	projectilesManager.update();
	
	if( highScore < playerCharacter.score )
	{
		highScore = playerCharacter.score;
	}
	
	if( isLeftSK )
	{
		saveProfile();
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
	
	// score
	font.drawNumber(PlayerCharacter.Instance.score, halfCanvasWidth, 0, AFont.kAlignCenter);
	
	// Health
	sprButtons.drawSpriteFrame( Buttons.HEART, canvasWidth - 30, 2 );
	font.drawNumber(PlayerCharacter.Instance.HP, canvasWidth - 28, 6, AFont.kAlignLeft);
}

public final void SaveGame(byte[] data, int offset)
{
	Util.int2Bytes(data, offset, playerCharacter.score);
	offset += 4;

	offset = playerCharacter.serialize(data, offset);
	offset = enemiesManager.serialize(data, offset);
	offset = projectilesManager.serialize(data, offset);
}

public final void LoadGame(byte[] data, int offset)
{
	if( data == null || data.length < 4 )
	{
		resetGame();
		return;
	}

	try
	{
		playerCharacter.score = Util.bytes2Int(data, offset);
		offset += 4;
	
		offset = playerCharacter.deserialize(data, offset);
		offset = enemiesManager.deserialize(data, offset);
		offset = projectilesManager.deserialize(data, offset);
	}
	catch( Exception ex )
	{		
		ex.printStackTrace();
	}
}
