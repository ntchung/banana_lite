
boolean updateGrenade()
{
	y += 96;	
		
	if( PlayerCharacter.Instance.checkHit( x - (4<<4), y-96, x + (4<<4), y ) )
	{
		PlayerCharacter.Instance.takeHit(1);		
		
		Game.playSfx(Game.SFX_EXPLODE);
		ProjectilesManager.Instance.create(ProjectilesAnim.GRENADE_HIT, x, y);
		return false;
	}
	
	if( y > (Game.wallHeight << 4) + PlayerCharacter.Instance.HalfHeight )
	{
		Game.playSfx(Game.SFX_EXPLODE);
		ProjectilesManager.Instance.create(ProjectilesAnim.GRENADE_HIT, x, y);
		return false;
	}
	
	return true;
}
