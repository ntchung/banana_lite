
boolean updateDart()
{
	y += 64;	
	x += dx;	
		
	if( PlayerCharacter.Instance.checkHit( x - (4<<4), y-64, x + (4<<4), y ) )
	{
		PlayerCharacter.Instance.takeHit(1);		
		
		Game.playSfx(Game.SFX_IMPACT2);
		ProjectilesManager.Instance.create(ProjectilesAnim.DART_HIT, x, y);
		return false;
	}
	
	if( y > (Game.canvasHeight << 4) + (32 << 4) )
	{
		return false;
	}
	
	return true;
}
