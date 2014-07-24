
boolean updateCrawler()
{
	if( currentAnim == MeleeAnim.dying )
	{
		return updateMeeleDying();
	}

	if( HP <= 0 )
	{
		currentAnim = MeleeAnim.dying;
		isBehindWall = false;
		PlayerCharacter.Instance.score += properties.Score;
		return true;
	}

	switch( currentAnim )
	{
	case MeleeAnim.patrol:
		updateMeleePatrol();
	break;
	case MeleeAnim.climb:
		updateCrawlerClimb();
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

void updateCrawlerClimb()
{
	if( decisionX < 0 && y > (64 << 4) )
	{
		if( flip == Sprite.TRANS_NONE )
		{
			x += properties.MoveSpeed;
			
			if( x >= (Game.halfCanvasWidth << 4) - properties.MoveSpeed )
			{
				flip = Sprite.TRANS_MIRROR;
			}
		}
		else
		{
			x -= properties.MoveSpeed;
			
			if( x <= -(Game.halfCanvasWidth << 4) + properties.MoveSpeed )
			{
				flip = Sprite.TRANS_NONE;
			}
		}
	}
	else
	{
		y += properties.ClimbSpeed;
		
		if( y + properties.Height >= (Game.wallHeight << 4) )
		{
			changeAnim(MeleeAnim.pushup);
		}
	}
	
	if( decisionCountdown <= 0 )
	{
		if( decisionX < 0 )
		{
			decisionCountdown = 30 + (Math.abs(EnemiesManager.Instance.random.nextInt()) % 20);
		}
		else
		{
			decisionCountdown = 80 + (Math.abs(EnemiesManager.Instance.random.nextInt()) % 50);
		}
		decisionX = -decisionX;
		
		flip = x < 0 ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
	}
	else
	{
		--decisionCountdown;
	}
}
