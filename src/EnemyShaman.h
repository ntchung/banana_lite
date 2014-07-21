
boolean updateShaman()
{
	if( currentAnim == RangedAnim.dying )
	{
		return updateMeeleDying();
	}

	if( HP <= 0 )
	{
		currentAnim = RangedAnim.dying;		
		return true;
	}

	switch( currentAnim )
	{
	case RangedAnim.patrol:
		updateRangedPatrol();
	break;
	case RangedAnim.attack:
		updateShamanAttack();
	break;	
	}
	
	if( decisionCountdown > 0 )
	{
		--decisionCountdown;
	}
	
	return true;
}

void updateShamanAttack()
{
	flip = x < PlayerCharacter.Instance.x ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
	
	if( currentFrameFraction == 0 && currentFrame == (animFramesCount[currentAnim] >> 1) )
	{
		int sy = y + properties.Height;
		Projectile prj = ProjectilesManager.Instance.create(ProjectilesAnim.DART, x, sy);
		
		int dy = ((PlayerCharacter.Instance.y + PlayerCharacter.Instance.HalfHeight) - sy) / 4;		
		if( dy < 1 )
		{
			dy = 1;
		}
		prj.dx = ((PlayerCharacter.Instance.x - x) << 4)/ dy;
	}
	
	if( currentFrame == animFramesCount[currentAnim] - 1 )	
	{
		redecide();
		changeAnim(RangedAnim.patrol);
	}
}
