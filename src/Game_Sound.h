
public static final int SFX_MENU_HIT		= 0;
public static final int SFX_SHOOT			= 1;
public static final int SFX_SWORD			= 2;
public static final int SFX_EXPLODE			= 3;
public static final int SFX_IMPACT1			= 4;
public static final int SFX_IMPACT2			= 5;
public static final int SFX_DIE				= 6;
public static final int SOUND_NUM			= 7;

public static final int SFX_CHANNEL0		= 0;
public static final int SFX_CHANNEL1		= 1;
public static final int SFX_CHANNEL2		= 2;
public static final int SFX_CHANNEL3		= 3;
public static final int MUSIC_CHANNEL		= 0;

private static boolean isSfxLoaded = false;
private static int currentSfxChannel = 0;

private static boolean[] isSoundItemPlayed = new boolean[SOUND_NUM];
private static int numChannelsUsedThisFrame = 0;
private static int waitNextSoundFrame = 0;

private static final void updateSounds()
{
	SoundPlayer.Update();
	
	for( int i=0; i<SOUND_NUM; i++ )
	{
		isSoundItemPlayed[i] = false;
	}	
	numChannelsUsedThisFrame = 0;
	if( waitNextSoundFrame > 0 )
	{
		waitNextSoundFrame--;
	}
}

private static final void loadMainMenuSounds()
{
}

private static final void unloadMainMenuSounds()
{
}

private static final void loadAPSfx()
{
	if( !isSfxLoaded )
	{
		isSfxLoaded = true;
		
		SoundPlayer.LoadSound( "/menu.wav", 3000, SFX_MENU_HIT, SoundPlayer.MIME_AUDIO_WAVE, true );			
		SoundPlayer.LoadSound( "/shoot.wav", 3400, SFX_SHOOT, SoundPlayer.MIME_AUDIO_WAVE, true );
		SoundPlayer.LoadSound( "/sword.wav", 2700, SFX_SWORD, SoundPlayer.MIME_AUDIO_WAVE, true );
		SoundPlayer.LoadSound( "/explode.wav", 11890 , SFX_EXPLODE, SoundPlayer.MIME_AUDIO_WAVE, true );
		SoundPlayer.LoadSound( "/impact1.wav", 3100, SFX_IMPACT1, SoundPlayer.MIME_AUDIO_WAVE, true );
		SoundPlayer.LoadSound( "/impact2.wav", 2600, SFX_IMPACT2, SoundPlayer.MIME_AUDIO_WAVE, true );
		SoundPlayer.LoadSound( "/die.wav", 6400, SFX_DIE, SoundPlayer.MIME_AUDIO_WAVE, true );
		
		waitNextSoundFrame = 0;
		numChannelsUsedThisFrame = 0;
		currentSfxChannel = 0;
	}	
}

private static final void unloadAPSfx()
{
	if( isSfxLoaded )
	{
		isSfxLoaded = false;
		
		for( int i=0; i<SOUND_NUM; ++i )
		{
			SoundPlayer.UnloadSound( i );
		}
	}
}

private static final void playMainMenuBGM()
{
}

private static final void stopMainMenuBGM()
{		
}

private static final void unloadAllSounds()
{
	SoundPlayer.StopAllSounds();	
	SoundPlayer.FreeAllChannels();	
	
	unloadMainMenuSounds();
	unloadAPSfx();	
}

public static final void playSfx( int index )
{
	if( optionSound == 0 )	
	{
		return;
	}
	
	loadAPSfx();	
	if( !isSoundItemPlayed[index] ) // && numChannelsUsedThisFrame < NUM_SOUND_CHANNELS_PER_FRAME )
	{
		isSoundItemPlayed[index] = true;
	
		SoundPlayer.Play( currentSfxChannel, index, 1, 100, 0 );
		
		// switch channel
		currentSfxChannel++;
		if( currentSfxChannel >= NUM_SOUND_CHANNELS )
		{
			waitNextSoundFrame = 10;
			currentSfxChannel = 0;
		}		
		numChannelsUsedThisFrame++;
	}
}
