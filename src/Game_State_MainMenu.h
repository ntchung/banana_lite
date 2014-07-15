
private Image imgTitle;
private Image imgWall;
private GameMenu menuMain;

private int wallHeight;

private void initMainMenu()
{
	imgTitle = Util.loadImage( "/title.png" );	
	
	if( imgWall == null )
	{
		imgWall = Util.loadImage( "/wall.png" );	
		wallHeight = imgWall.getHeight() - 16;
	}

	menuMain = new GameMenu( 4, GameMenu.kBarTypeShort );
	menuMain.setItem( 0, STR_NEWGAME );
	menuMain.setItem( 1, STR_LEADERBOARD );
	menuMain.setItem( 2, STR_SOUND_ON );
	menuMain.setItem( 3, STR_HELP );
	menuMain.soundItem = 2;
	
	menuMain.init();
}

private void destroyMainMenu()
{
	menuMain = null;	
	imgTitle = null;
}

private void updateMainMenu()
{
	if( updateMenu( menuMain ) )
	{
	}
}

private void paintMainMenu()
{
	currentGraphics.setColor( 0x70c7ed );
	currentGraphics.fillRect( 0, 0, canvasWidth, canvasHeight - wallHeight );

	currentGraphics.drawImage( imgWall, 
		( canvasWidth - imgWall.getWidth() ) >> 1, 
		( canvasHeight - imgWall.getHeight() ), 0 );
		
	menuMain.paint( canvasWidth >> 1, canvasHeight >> 2 );
	
	currentGraphics.drawImage( imgTitle, 0, 0, 0 );	
}
