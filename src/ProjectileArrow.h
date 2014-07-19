
boolean updateArrow()
{
	y -= 256;
	
	if( y < -(64 << 4) )
	{
		return false;
	}
	
	if( y < 0 )
	{
		return true;
	}
	
	Enemy hitEnemy = EnemiesManager.Instance.checkHit(x, y, x, y + 256);
	if( hitEnemy != null )
	{
		hitEnemy.takeHit(1);		
		
		ProjectilesManager.Instance.create(ProjectilesAnim.ARROW_HIT, x, y);
		return false;
	}
	
	return true;
}
