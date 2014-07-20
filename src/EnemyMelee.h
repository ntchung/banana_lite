
boolean updateMelee()
{
	if( currentAnim == MeleeAnim.dying )
	{
		return updateMeeleDying();
	}

	if( HP <= 0 )
	{
		currentAnim = MeleeAnim.dying;
		isBehindWall = false;
		return true;
	}

	switch( currentAnim )
	{
	case MeleeAnim.patrol:
		updateMeleePatrol();
	break;
	case MeleeAnim.climb:
		updateMeleeClimb();
	break;
	case MeleeAnim.pushup:
		updateMeleePushUp();
	break;
	case MeleeAnim.idle:
		updateMeleeIdle();
	break;
	case MeleeAnim.chase:
		updateMeleeChase();
	break;
	case MeleeAnim.attack:
		updateMeleeAttack();
	break;
	}
	
	if( decisionCountdown > 0 )
	{
		--decisionCountdown;
	}
	
	return true;
}

void updateMeleeAttack()
{
	final int targetX = PlayerCharacter.Instance.x;
	final int targetHalfWidth = PlayerCharacter.Instance.HalfWidth;
	flip = x < targetX ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;

	if( currentFrameFraction == 0 && currentFrame == (animFramesCount[currentAnim] >> 1) )
	{
		if( !( x + properties.HalfWidth + properties.AttackRange < targetX - targetHalfWidth || x - properties.HalfWidth - properties.AttackRange > targetX + targetHalfWidth ) )
		{
			final int targetHalfHeight = PlayerCharacter.Instance.HalfHeight;
			final int fuzzyX = -(targetHalfWidth >> 2) + (Math.abs(EnemiesManager.Instance.random.nextInt()) % (targetHalfWidth >> 1));
			final int fuzzyY = -(targetHalfHeight >> 2) + (Math.abs(EnemiesManager.Instance.random.nextInt()) % (targetHalfHeight >> 1));
		
			Game.playSfx(properties.AttackPower < 2 ? Game.SFX_IMPACT1 : Game.SFX_IMPACT2);					
			ProjectilesManager.Instance.create(properties.AttackPower < 2 ? ProjectilesAnim.SMALL_HIT : ProjectilesAnim.BIG_HIT, targetX + fuzzyX, PlayerCharacter.Instance.y + targetHalfHeight + fuzzyY);
		}		
	}
	
	if( currentFrame == animFramesCount[currentAnim] - 1 )	
	{
		changeAnim(MeleeAnim.chase);
	}
}

void updateMeleeChase()
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
		changeAnim(MeleeAnim.attack);
	}
}

void updateMeleeIdle()
{
	changeAnim(MeleeAnim.chase);
}

void updateMeleeClimb()
{
	y += properties.ClimbSpeed;
	
	if( y + properties.Height >= (Game.wallHeight << 4) )
	{
		changeAnim(MeleeAnim.pushup);
	}
}

void updateMeleePushUp()
{	
	if( currentFrame == animFramesCount[currentAnim]-1 )
	{	
		currentFrameFraction = 0;
		
		isBehindWall = true;
		y -= (2 << 4);
		if( y <= (Game.wallHeight << 4) )
		{
			y = (Game.wallHeight << 4);
			changeAnim(MeleeAnim.idle);
		}
	}
	else
	{
		y += (2 << 4);
	}
}

void updateMeleePatrol()
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
			if( !EnemiesManager.Instance.checkHitWithClimbers(this) )
			{
				changeAnim(MeleeAnim.climb);
			}
		}
	}
}

boolean updateMeeleDying()
{
	y -= 64;
	if( y < -(64 << 4) )
	{
		return false;
	}

	if( currentFrame == animFramesCount[currentAnim]-1 )
	{	
		flip = flip == Sprite.TRANS_NONE ? Sprite.TRANS_MIRROR : Sprite.TRANS_NONE;
	}
	return true;
}
