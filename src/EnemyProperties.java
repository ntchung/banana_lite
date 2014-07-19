
#include "Game_Config.h"

package PACKAGE_NAME;

class EnemyProperties
{
	public EnemyProperties(int nMoveSpeed, int nClimbSpeed, int nWidth, int nHeight, int nMaxHP, int nAttackRange, int nMinDecisionCountdown, int nMaxDecisionCountdown)
	{
		MoveSpeed = nMoveSpeed;
		ClimbSpeed = nClimbSpeed;
		Width = nWidth;
		Height = nHeight;
		MaxHP = nMaxHP;
		AttackRange = nAttackRange;
		
		HalfWidth = Width >> 1;
		MinDecisionCountdown = nMinDecisionCountdown;
		MaxDecisionCountdown = nMaxDecisionCountdown;
	}

	public int MoveSpeed;
	public int ClimbSpeed;
	public int Width;
	public int Height;
	public int HalfWidth;
	public int AttackRange;
	
	public int MaxHP;
	public int MinDecisionCountdown;
	public int MaxDecisionCountdown;
}
