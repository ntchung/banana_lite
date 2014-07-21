
boolean updateThrower()
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
		updateThrowerAttack();
	break;	
	}
	
	if( decisionCountdown > 0 )
	{
		--decisionCountdown;
	}
	
	return true;
}

void updateThrowerAttack()
{
	if( currentFrameFraction == 0 && currentFrame == ((animFramesCount[currentAnim] << 1) / 3) )
	{
		ProjectilesManager.Instance.create(ProjectilesAnim.GRENADE, x, y + properties.Height);
	}
	
	if( currentFrame == animFramesCount[currentAnim] - 1 )	
	{
		redecide();
		changeAnim(RangedAnim.patrol);
	}
}

void updateRangedPatrol()
{
	// right
	if( flip == Sprite.TRANS_NONE )
	{
		x += properties.MoveSpeed;
		if( x > (EnemiesManager.WALL_MAX_X << 4) )
		{
			flip = Sprite.TRANS_MIRROR;
		}
	}
	// left
	else
	{
		x -= properties.MoveSpeed;
		if( x < -(EnemiesManager.WALL_MAX_X << 4) )
		{
			flip = Sprite.TRANS_NONE;
		}
	}
	
	if( decisionCountdown <= 0 )
	{
		// right
		if( x >= decisionX - (properties.MoveSpeed << 1) && x <= decisionX + (properties.MoveSpeed << 1) )
		{
			changeAnim(RangedAnim.attack);			
		}
	}
}
