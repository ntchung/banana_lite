
#include "Game_Config.h"

package PACKAGE_NAME;

class EnemyProperties
{
	public EnemyProperties(int nMoveSpeed, int nClimbSpeed, int nWidth, int nHeight, int nMaxHP, int nAttackRange, int nAttackPower, int nMinDecisionCountdown, int nMaxDecisionCountdown, int nScore, int nTechLevel)
	{
		MoveSpeed = nMoveSpeed;
		ClimbSpeed = nClimbSpeed;
		Width = nWidth;
		Height = nHeight;
		MaxHP = nMaxHP;
		AttackRange = nAttackRange;
		AttackPower = nAttackPower;
		
		HalfWidth = Width >> 1;
		MinDecisionCountdown = nMinDecisionCountdown;
		MaxDecisionCountdown = nMaxDecisionCountdown;
		
		Score = nScore;
		TechLevel = nTechLevel;
	}

	public int MoveSpeed;
	public int ClimbSpeed;
	public int Width;
	public int Height;
	public int HalfWidth;
	public int AttackRange;
	public int AttackPower;
	
	public int MaxHP;
	public int MinDecisionCountdown;
	public int MaxDecisionCountdown;
	
	public int Score;
	public int TechLevel;
}
