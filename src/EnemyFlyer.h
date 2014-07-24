
boolean updateFlyer()
{
	if( currentAnim == FlyerAnim.dying )
	{
		return updateFlyerDying();
	}

	if( HP <= 0 )
	{
		currentAnim = FlyerAnim.dying;
		PlayerCharacter.Instance.score += properties.Score;
		return true;
	}

	switch( currentAnim )
	{
	case FlyerAnim.patrol:
		updateFlyerPatrol();
	break;
	case FlyerAnim.chase:
		updateFlyerChase();
	break;
	case FlyerAnim.leave:
		updateFlyerLeave();
	break;
	case FlyerAnim.attack:
		updateFlyerAttack();
	break;	
	}
	
	if( decisionCountdown > 0 )
	{
		--decisionCountdown;
	}
	
	return true;
}

void updateFlyerAttack()
{
	final int targetX = PlayerCharacter.Instance.x;
	final int targetHalfWidth = PlayerCharacter.Instance.HalfWidth;
	flip = x < targetX ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;

	if( currentFrameFraction == 0 && currentFrame == (animFramesCount[currentAnim] >> 1) )
	{
		if( !( x + properties.HalfWidth + properties.AttackRange < targetX - targetHalfWidth || x - properties.HalfWidth - properties.AttackRange > targetX + targetHalfWidth ) )
		{
			final int targetHalfHeight = PlayerCharacter.Instance.HalfHeight + (PlayerCharacter.Instance.HalfHeight >> 1);
			final int fuzzyX = -(targetHalfWidth >> 2) + (Math.abs(EnemiesManager.Instance.random.nextInt()) % (targetHalfWidth >> 1));
			final int fuzzyY = -(targetHalfHeight >> 2) + (Math.abs(EnemiesManager.Instance.random.nextInt()) % (targetHalfHeight >> 1));
		
			Game.playSfx(Game.SFX_IMPACT1);
			ProjectilesManager.Instance.create(ProjectilesAnim.RAVEN_HIT, targetX + fuzzyX, PlayerCharacter.Instance.y + targetHalfHeight + fuzzyY);
			
			PlayerCharacter.Instance.takeHit(1);
		}		
	}
	
	if( currentFrame == animFramesCount[currentAnim] - 1 )	
	{
		changeAnim(FlyerAnim.leave);
	}
}

void updateFlyerChase()
{
	final int targetX = PlayerCharacter.Instance.x;
	final int targetHalfWidth = PlayerCharacter.Instance.HalfWidth;

	flip = x < targetX ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
	if( x + properties.HalfWidth + properties.AttackRange < targetX - targetHalfWidth )
	{
		x += properties.MoveSpeed;		
	}
	else if( x - properties.HalfWidth - properties.AttackRange > targetX + targetHalfWidth )
	{
		x -= properties.MoveSpeed;
	}
	else
	{
		changeAnim(FlyerAnim.attack);
	}
}

void updateFlyerLeave()
{
	boolean leave = false;

	// right
	if( flip == Sprite.TRANS_NONE )
	{
		x += properties.MoveSpeed;
		if( x > (EnemiesManager.WALL_MAX_X << 4) )
		{			
			leave = true;
		}
	}
	// left
	else
	{
		x -= properties.MoveSpeed;
		if( x < -(EnemiesManager.WALL_MAX_X << 4) )
		{			
			leave = true;
		}
	}
	
	if( leave )
	{
		y = (64 + Math.abs(EnemiesManager.Instance.random.nextInt()) % (Game.wallHeight-128)) << 4;
		changeAnim(FlyerAnim.patrol);
		redecide();		
	}
}

void updateFlyerPatrol()
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
	
	if( decisionCountdown <= 0 && EnemiesManager.Instance.chasingFlyersCount < 2 )
	{
		if( x < -(Game.halfCanvasWidth << 4) || x > (Game.halfCanvasWidth << 4) )
		{
			++EnemiesManager.Instance.chasingFlyersCount;
			changeAnim(FlyerAnim.chase);
			y = ((Game.wallHeight) << 4) + (PlayerCharacter.Instance.HalfHeight << 1);
		}		
	}
}

boolean updateFlyerDying()
{
	return ( currentFrame < animFramesCount[currentAnim]-1 );	
}
