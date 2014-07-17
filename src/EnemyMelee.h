
void updateMelee()
{
	switch( currentAnim )
	{
	case MeleeAnim.patrol:
		updateMeleePatrol();
	break;
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
}
