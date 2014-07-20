
#include "Game_Config.h"
package PACKAGE_NAME;

import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import java.io.*;

class SoundPlayer implements Runnable
{	
	private final static boolean k_sound_useCachedPlayers = false;
	private final static boolean k_sound_useThread = false;
	
	private final static int k_thread_wait				= 20;

	public final static int MIME_AUDIO_AMR				= 0;
	public final static int MIME_AUDIO_MIDI				= 1;
	public final static int MIME_AUDIO_WAVE				= 2;

    private final static String[] k_MIME = { "audio/amr", "audio/midi", "audio/x-wav" };	

	/// Sound commands.
	private final static int k_command_dummy 		= 0;
	private final static int k_command_prepare 		= k_command_dummy + 1;
	private final static int k_command_free			= k_command_prepare + 1;
	private final static int k_command_play 		= k_command_free + 1;
	private final static int k_command_stop 		= k_command_play + 1;
	private final static int k_command_pause 		= k_command_stop + 1;
	private final static int k_command_resume	 	= k_command_pause + 1;
	private final static int k_command_amount		= k_command_resume + 1;

	/// Sound states.
	private final static int k_state_unprepared		= 0;
	private final static int k_state_ready 			= k_state_unprepared + 1;
	private final static int k_state_playing		= k_state_ready + 1;
	private final static int k_state_paused			= k_state_playing + 1;

	/// Sound command queue data
	private final static int k_queue_command		= 0;
	private final static int k_queue_index			= k_queue_command + 1;
	private final static int k_queue_priority		= k_queue_index + 1;
	private final static int k_queue_volume			= k_queue_priority + 1;
	private final static int k_queue_loop			= k_queue_volume + 1;
	private final static int k_queue_size			= k_queue_loop + 1;

	/// Defines max number of commands to be queued
	private final static int k_max_queue_length		= 10;

	/// Sound priorities constant.
	final static int k_priority_highest				= 0;
	/// Sound priorities constant.
	final static int k_priority_normal				= 7;
	/// Sound priorities constant.
	final static int k_priority_lowest				= 15;

	/// Sound maximum slot count for this game. The maximum number of sounds that can be loaded into memory.
	private static int s_maxNbSoundSlot;
	private static int s_nbChannel;

	/// Sound slots
	private static byte[][] s_sndSlot;

	/// Sound types
	private static int[] s_sndType;

	
	/// Device's sound players.
	private static javax.microedition.media.Player[] s_Player;
	private static javax.microedition.media.Player[] s_PlayerSlot;

	private static int[] 		s_index;
	private static int[] 		s_priority;
	private static int[] 		s_state;
	private static int[] 		s_volume;
	private static int[] 		s_loop;
	private static Thread 		s_pThread;
	private static SoundPlayer	s_pSoundPlayerIns;

	private static int[]		s_queue;
	private static int[]		s_queue_pointer;
	private static int[]		s_queue_size;

	private static boolean		s_isSoundEngineInitialized = false;

	//--------------------------------------------------------------------------------------------------------------------
	/// Transforms command queue index according to the queue size
	/// @param index Index to transform
	/// @return correct Index in the command queue
	//--------------------------------------------------------------------------------------------------------------------
	private static int SndQueue_NormalizeIndex (int index)
	{
		while(index >= k_max_queue_length)
		{
			index -= k_max_queue_length;
		}

		while(index < 0)
		{
			index += k_max_queue_length;
		}

		return index;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Get queue index
	/// @param channel Channel index
	/// @param index Index in the channel queue
	//--------------------------------------------------------------------------------------------------------------------
	private static int SndQueue_GetIndex (int channel, int index)
	{
		return (channel * k_max_queue_length * k_queue_size + index * k_queue_size);
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Get queue data from current queue pointer
	/// @param channel Command queue index
	/// @param index Data index in queue (k_queue_index, k_queue_priority, etc.)
	//--------------------------------------------------------------------------------------------------------------------
	private static int SndQueue_GetData (int channel, int index)
	{
		return s_queue[SndQueue_GetIndex(channel, s_queue_pointer[channel]) + index];
	}


	//--------------------------------------------------------------------------------------------------------------------
	/// Adds a sound request in the queue
	/// @param channel Channel on which the command will be performed
	/// @param command Command to perform
	/// @param index Sound index
	/// @param priority Sound priority (0:biggest - 15:lowest)
	/// @param volume Sound volume (0-100) - used only for k_command_play
	/// @param loop Number of time to play sound (0:infinite) - used only for k_command_play
	//--------------------------------------------------------------------------------------------------------------------
	private static void SndQueue_Push (int channel, int command, int index, int priority, int volume, int loop)
	{
		int start = s_queue_pointer[channel];
		int size = s_queue_size[channel];
		int end = SndQueue_NormalizeIndex(start + size);
		int idx;

		// remove duplicated previous requests, or previous requests with lower priority
		for(int i = 0; i < size; i++)
		{
			// get queue starting index
			idx = SndQueue_NormalizeIndex(end - i - 1);
			idx = SndQueue_GetIndex(channel, idx);

			// duplicated request found?
			if(s_queue[idx + k_queue_command] == command)
			{
				//perform priority check

				// if request is play or request is prepare
				// and
				// previous request is with higher priority
				// . do not skip previous request
				if(		((command == k_command_play) || (command == k_command_prepare))
					&& 	(s_queue[idx + k_queue_priority] < priority)
					)
				{
					continue;
				}

				// skip duplicate previous sound request
				s_queue[idx + k_queue_command] = k_command_dummy;
			}
		}


		// add current request
		idx = SndQueue_GetIndex(channel, end);
		s_queue[idx + k_queue_command] = command;
		s_queue[idx + k_queue_index] = index;
		s_queue[idx + k_queue_priority] = priority;
		s_queue[idx + k_queue_volume] = volume;
		s_queue[idx + k_queue_loop] = loop;

		s_queue_size[channel]++;
	}


	//--------------------------------------------------------------------------------------------------------------------
	/// Adds a sound request in the queue (used for simple requests)
	/// @param channel Channel on which the command will be performed
	/// @param command Command to perform
	//--------------------------------------------------------------------------------------------------------------------
	private static void SndQueue_Push (int channel, int command)
	{
		SndQueue_Push(channel, command, -1, -1, -1, -1);
	}


	//--------------------------------------------------------------------------------------------------------------------
	/// Removes a sound request from the queue
	/// @param channel Channel queue from which request will be removed
	//--------------------------------------------------------------------------------------------------------------------
	private static void SndQueue_Pop (int channel)
	{
		s_queue_pointer[channel] = SndQueue_NormalizeIndex(s_queue_pointer[channel] + 1);
		s_queue_size[channel]--;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Allocate sound player and resources. Init sound container.
	/// @param nbSoundSlot number of sounds file you want to have in memory.
	/// @note This call does not load the sound files (mid,wav,mp3...) in memory. 
	/// @note Also this is not the number of sound your phone can play at once ( Channels ).
	//--------------------------------------------------------------------------------------------------------------------
	static void Init( int nbSoundSlot, int numChannels )
	{
		if( s_isSoundEngineInitialized )
		{
			return;
		}

		int i;
		s_nbChannel = numChannels;

		// allocate snd channel
		s_Player = new javax.microedition.media.Player[s_nbChannel];

		// allocate players for caching
		if(k_sound_useCachedPlayers)
		{
			s_PlayerSlot = new javax.microedition.media.Player[nbSoundSlot];
		}

		s_index = new int[s_nbChannel];
		s_priority = new int[s_nbChannel];
		s_state = new int[s_nbChannel];
		s_volume = new int[s_nbChannel];
		s_loop = new int[s_nbChannel];

		// init command queue
		s_queue = new int[s_nbChannel * k_max_queue_length * k_queue_size];
		s_queue_pointer = new int[s_nbChannel];
		s_queue_size = new int[s_nbChannel];

		for( i = 0; i < s_nbChannel; i++)
		{
			s_index[i] = -1;
			s_queue_pointer[i] = 0;
			s_queue_size[i] = 0;
		}

		// set nb of slot to allocate
		s_maxNbSoundSlot 	= nbSoundSlot;

		// allocate snd slot
		s_sndSlot = new byte[s_maxNbSoundSlot][];

		// alloacate array to store the sound type
		s_sndType = new int[s_maxNbSoundSlot];		

		s_isSoundEngineInitialized = true;

		// start the thread
		s_pSoundPlayerIns = new SoundPlayer();
		
		if( k_sound_useThread )
		{
			s_pThread = new Thread(s_pSoundPlayerIns);
			s_pThread.start();		
		}
		else
		{
			s_pThread = null;
		}
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Deallocate all sound ressources and players.
	/// @note All sound loaded with loadSound are going to be freed from memory.
	//--------------------------------------------------------------------------------------------------------------------
	static void Quit()
	{
		if( !s_isSoundEngineInitialized )
		{
			return;
		}
		StopAllSounds();

		// free sound channel
		for (int i = 0; i < s_nbChannel; i++)
		{
		    FreeChannelExec(i);
		}

		s_Player = null;
		
		s_isSoundEngineInitialized = false;
		

		// free sound slots
		for (int i = 0; i < s_maxNbSoundSlot; i++)
		{
			s_sndSlot[i] = null;;
		}

		s_sndSlot = null;;
		s_sndType = null;;
		
		// free sound slots
		if(k_sound_useCachedPlayers)
		{
			for (int i = 0; i < s_maxNbSoundSlot; i++)
			{
				s_PlayerSlot[i] = null;;
			}
		}

		s_index = null;;
		s_priority = null;;
		s_state = null;;
		s_volume = null;;
		s_loop = null;;
		s_pThread = null;;

		//free command queue
		s_queue = null;;
		s_queue_pointer = null;;
		s_queue_size = null;;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Load a sound file.
	//--------------------------------------------------------------------------------------------------------------------
	static void LoadSound( String dataFileName, int dataSize, int index, int nMIME, boolean bCacheThisSound )
	{		
		try
		{		
			// prepare memory
			if( s_sndSlot[index] != null )
			{
				if( s_sndSlot[index].length < dataSize )
				{
					s_sndSlot[index] = null;
					s_sndSlot[index] = new byte[dataSize];
				}
			}
			else
			{
				s_sndSlot[index] = new byte[dataSize];
			}

			// load sound data	
			InputStream stream = "".getClass().getResourceAsStream( dataFileName );					
			int readSize;
			int offset = 0;
			do
			{
				readSize = stream.read(s_sndSlot[index], offset, 128);	
				if( readSize > 0 )
				{
					offset += readSize;
				}
				if( readSize < 128 )
				{
					break;
				}
			}
			while( readSize > 0 );

			// get sound type
			s_sndType[index] = nMIME;
			
			// keep sound players allocated ?
			if( k_sound_useCachedPlayers && bCacheThisSound )
			{			
				s_PlayerSlot[index] = javax.microedition.media.Manager.createPlayer( new java.io.ByteArrayInputStream(s_sndSlot[index]), k_MIME[s_sndType[index]]);

				// keep sound players realized
				s_PlayerSlot[index].realize();

				// keep sound players prefetched
				s_PlayerSlot[index].prefetch();

				s_sndSlot[index] = null;
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			s_PlayerSlot[index] = null;
		}
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Unload a sound resource from memory.
	/// No exception will thrown if the sound is allready unloaded or if the index is invalid.
	/// @param index Index of sound to unload.
	//--------------------------------------------------------------------------------------------------------------------
	static void UnloadSound( int index )
	{
		if( k_sound_useCachedPlayers )
		{
			if( s_PlayerSlot[index] != null )
			{
				try
				{
					if(s_PlayerSlot[index].getState() == Player.STARTED) 
					{
						s_PlayerSlot[index].stop();				
					}

					if(s_PlayerSlot[index].getState() == Player.PREFETCHED) 
					{
						s_PlayerSlot[index].deallocate();
					}
					if(s_PlayerSlot[index].getState() == Player.REALIZED ||  s_PlayerSlot[index].getState() == Player.UNREALIZED) 
					{
						s_PlayerSlot[index].close();
					}
				}
				catch( Exception ex )
				{
					Trace( ex );
				}
			}			
			s_PlayerSlot[index] = null;
		}
		
		s_sndSlot[index] = null;
	}


	//--------------------------------------------------------------------------------------------------------------------
	///  Free sound channel from the player asigned to it
	/// @param channel Channel to prepare the sound on.
	//--------------------------------------------------------------------------------------------------------------------
	final static void FreeChannel( int channel )
	{
		// put free request in queue
		SndQueue_Push(channel, k_command_free, -1, -1, -1, -1);
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Prepare a sound to be played on a channel.
	/// If there is allready a sound prepared, this call will be ignored if the new priority is lower than the current priority.
	/// If the new priority is higher of equal, this new request will be executed.
	/// @param channel Channel to prepare the sound on.
	/// @param index Index of the sound to be played.
	/// @param priority Priority of this request. (0:highest - 15:lowest)
	//--------------------------------------------------------------------------------------------------------------------
	static void PrepareSound (int channel, int index, int priority)
	{
		// put prepare request in queue
		SndQueue_Push(channel, k_command_prepare, index, priority, -1, -1);
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Play a sound on a channel. If the sound was not prepared it will be done before playing.
	/// @param channel Channel to play the sound on.
	/// @param index Index of BGM to play.
	/// @param loop Number of time to play this sound. (0:infinite)
	/// @param volume Volume of BGM (0-100).
	/// @param priority Priority of this sound. (0:biggest - 15:lowest)
	/// \sa prepareSound
	//--------------------------------------------------------------------------------------------------------------------
	static void Play( int channel, int index, int loop, int volume, int priority )
	{
		// put play request in queue
		SndQueue_Push(channel, k_command_play, index, priority, volume, loop);
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Stop sound on a channel. If this channel is not currently playing a sound, nothing will be done.
	/// @param channel Channel of the sound to stop.
	//--------------------------------------------------------------------------------------------------------------------
	final static void Stop (int channel)
	{		
		// put stop request in queue
		SndQueue_Push(channel, k_command_stop);
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Pause sound on a channel. If this channel is not currently playing a sound, nothing will be done.
	/// @param channel Channel of the sound to pause.
	//--------------------------------------------------------------------------------------------------------------------
	final static void Pause (int channel)
	{
		// put pause request in queue
		SndQueue_Push(channel, k_command_pause);
	}

	final static void PauseAll()
	{
		for( int i=0; i<s_nbChannel; i++ )
		{
			Pause( i );
		}
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Resume currently paused sound on a channel. If there is no paused sound, nothing will be done.
	/// @param channel Channel of the sound to resume.
	//--------------------------------------------------------------------------------------------------------------------
	final static void Resume (int channel)
	{
		// put resume request in queue
		SndQueue_Push(channel, k_command_resume);
	}

	final static void ResumeAll()
	{
		for( int i=0; i<s_nbChannel; i++ )
		{
			Resume( i );
		}
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Prepare a sound to be played on a channel.
	/// @param channel Channel to prepare the sound on.
	/// \sa Update_Exec
	//--------------------------------------------------------------------------------------------------------------------
	private static void PrepareExec( int channel )
	{		
		// prepare sound
		int index = SndQueue_GetData(channel, k_queue_index);
		int priority = SndQueue_GetData(channel, k_queue_priority);

		// if a sound is playing
		// and this sound has lower priority
		if( (s_state[channel] == k_state_playing) && (s_priority[channel] < priority ) )
		{
			return;
		}

		// if a sound is prepared (is ready or playing or paused)
		// and this is the same sound
		if( (s_index[channel] == index) && (s_state[channel] != k_state_unprepared) )
		{
			return;
		}

		// stop previous sound on this channel
		FreeChannelExec(channel);
		
		// create player
		try
		{
			if( k_sound_useCachedPlayers )
			{
				s_Player[channel] = s_PlayerSlot[index];
				
				if(s_Player[channel] == null)
				{				
					s_Player[channel] = javax.microedition.media.Manager.createPlayer( new java.io.ByteArrayInputStream(s_sndSlot[index]), k_MIME[s_sndType[index]] );
				}
			}
			else
			{
				s_Player[channel] = javax.microedition.media.Manager.createPlayer( new java.io.ByteArrayInputStream(s_sndSlot[index]), k_MIME[s_sndType[index]] );

				// realize player
				//s_Player[channel].realize();

				// prefetch player
				//s_Player[channel].prefetch();
			}
		}
		catch( Exception ex )
		{
			s_Player[channel] = null;
		}

		// set sound in channel as ready
		s_state[channel] = k_state_ready;
		s_index[channel] = index;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Play a sound on a channel.
	/// @param channel Channel to prepare the sound on.
	/// \sa Update_Exec
	//--------------------------------------------------------------------------------------------------------------------
	private static void PlayExec( int channel )
	{				
		// prepare current sound on this channel
		PrepareExec(channel);

		if((s_state[channel] != k_state_ready) || (s_Player[channel] == null))
		{
			return;
		}

		int index = SndQueue_GetData(channel, k_queue_index);
		int priority = SndQueue_GetData(channel, k_queue_priority);
		int loop = SndQueue_GetData(channel, k_queue_loop);
		int volume = SndQueue_GetData(channel, k_queue_volume);

		// set loop
		if(loop == 0)
		{
			s_Player[channel].setLoopCount(-1);
		}
		else
		{
			s_Player[channel].setLoopCount(loop);
		}

		// set volume			
		//((javax.microedition.media.control.VolumeControl)
		//    (s_Player[channel].getControl("VolumeControl")))
		//        .setLevel(((volume * s_masterVolume * k_max_volume) / (100 * 100)));

		// play sound		
		try
		{
			s_Player[channel].start();
		}
		catch( Exception ex )
		{
			s_Player[channel] = null;
		}

		s_state[channel] = k_state_playing;
		s_volume[channel] = volume;
		s_loop[channel] = loop;
		s_priority[channel] = priority;
		s_index[channel] = index;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Change master volume value.
	/// @param volume The new volume value. (0 - 100)
	/// @note It may not be used until the next play.
	//--------------------------------------------------------------------------------------------------------------------
	//static void SetMasterVolume(int volume) throws Exception
	//{		
	//    // set new master volume
	//    s_masterVolume = volume;

	//    try
	//    {
	//        for (int channel = 0; channel < s_nbChannel; channel++)
	//        {
	//            if(s_Player[channel] == null)
	//            {
	//                continue;
	//            }

	//            ((javax.microedition.media.control.VolumeControl)(s_Player[channel].getControl("VolumeControl"))).setLevel(((s_volume[channel] * s_masterVolume * k_max_volume) / (100 * 100)));
	//        }
	//    }
	//    catch (Exception e)
	//    {
	//    }
	//}

	//--------------------------------------------------------------------------------------------------------------------
	/// Stop sound on a channel.
	/// @param channel Channel of the sound to stop.
	/// \sa Update_Exec
	//--------------------------------------------------------------------------------------------------------------------
	private static void StopExec( int channel )
	{
		if (s_Player[channel] == null)
		{
			return;
		}

		try
		{
			s_Player[channel].stop();
		}
		catch( Exception ex )
		{
			s_Player[channel] = null;
		}
		
		s_state[channel] = k_state_ready;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Pause sound on a channel.
	/// @param channel Channel of the sound to pause.
	/// \sa Update_Exec
	//--------------------------------------------------------------------------------------------------------------------
	private static void PauseExec (int channel)
	{
		if (s_state[channel] != k_state_playing)
		{
			return;
		}

		if(s_Player[channel] == null)
		{
			return;
		}

		try
		{
			s_Player[channel].stop();
		}
		catch( Exception ex )
		{
			s_Player[channel] = null;
		}

		s_state[channel] = k_state_paused;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Resume currently paused sound on a channel.
	/// @param channel Channel of the sound to resume.
	/// \sa Update_Exec
	//--------------------------------------------------------------------------------------------------------------------
	private static void ResumeExec( int channel )
	{
		if (s_state[channel] != k_state_paused)
		{
			return;
		}

		if(s_Player[channel] == null)
		{
			return;
		}

		try
		{
			s_Player[channel].start();			
		}
		catch( Exception ex )
		{
			s_Player[channel] = null;
		}		

		s_state[channel] = k_state_playing;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Update sound engine status all sound ressource. This call <b>HAS</b> to be called at every game loop.
	/// @note If your device is using the Threaded system, this call will do nothing. It is a good idea to keep calling it any
	///       way to prevent errors/bugs with unthreaded devices.
	//--------------------------------------------------------------------------------------------------------------------
	static void Update()
	{
		if(s_pThread != null)
		{
			if( s_pThread.isAlive() == false )
			{
				s_pThread.start();
			}
		}
		else
		{
			Update_Exec();
		}
	}


	//--------------------------------------------------------------------------------------------------------------------
	/// Update sound engine status all sound ressource. This function will be called by \sa update or by the Thread Loop.
	//--------------------------------------------------------------------------------------------------------------------
	private static void Update_Exec()
	{
		if(!s_isSoundEngineInitialized)
		{
			return;
		}

		for (int channel = 0; channel < s_nbChannel; channel++)
		{
			// update playing state on current channel
			if(s_queue_size[channel] > 0 && s_state[channel] == k_state_playing)
			{
				boolean isPlaying;
				try
				{
					isPlaying = IsPlaying(channel);
				}
				catch(Exception e)
				{
					isPlaying = false;
				}

				if(!isPlaying)
				{
					s_state[channel] = k_state_ready;
				}
			}

			while(s_queue_size[channel] > 0)
			{
				try
				{
					int command = SndQueue_GetData(channel, k_queue_command);
					switch (command)
					{
						case k_command_prepare:
							PrepareExec(channel);
							break;
							
						case k_command_free:
							FreeChannelExec(channel);
							break;

						case k_command_play:
							PlayExec(channel);
							break;

						case k_command_stop:
							StopExec(channel);
							break;

						case k_command_pause:
							PauseExec(channel);
							break;

						case k_command_resume:
							ResumeExec(channel);
							break;
					}
				}
				catch (Exception e)
				{					
				}

				// command executed . remove from queue
				SndQueue_Pop(channel);
			}
		}
	}


	//--------------------------------------------------------------------------------------------------------------------
	/// Implementation of the Runnable interface. This function will be used only when the GLLibPlayer is used to play sounds.
	/// When the GLLibPlayer is instanciated to play anymations, this functions is doing nothing.
	//--------------------------------------------------------------------------------------------------------------------
	public void run()
	{
		while(s_pThread != null)
		{
			Update_Exec();

			try
			{
				Thread.sleep( k_thread_wait );
			}
			catch(Exception e)
			{
			}
		}
	}


	//--------------------------------------------------------------------------------------------------------------------
	/// Return true if a sound is currently playing on channel.
	/// @param channel Channel where the sound is suposed to play.
	/// @note dummy sound are always NOT playing.
	//--------------------------------------------------------------------------------------------------------------------
	protected static boolean IsPlaying (int channel)
	{
		// if channel is null
		if (s_Player[channel] == null)
		{
			return false;
		}

		if (s_Player[channel].getState() != javax.microedition.media.Player.STARTED)
		{
			return false;
		}

		return true;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Free a channel from all it's ressources. Nothing will be done if the channel is allready free.
	/// @param channel Channel to free.
	/// \sa Update_Exec
	//--------------------------------------------------------------------------------------------------------------------
	private static void FreeChannelExec (int channel)
	{
		if (s_Player[channel] != null)
		{
			try
			{
				if(s_Player[channel].getState() == Player.STARTED) 
				{
					s_Player[channel].stop();				
				}

				if( !k_sound_useCachedPlayers )
				{
					if(s_Player[channel].getState() == Player.PREFETCHED) 
					{
						s_Player[channel].deallocate();
					}
					if(s_Player[channel].getState() == Player.REALIZED ||  s_Player[channel].getState() == Player.UNREALIZED) 
					{
						s_Player[channel].close();
					}
				}
				else
				{
					//close only if not cached
					int index = SndQueue_GetData(channel, k_queue_index);
					if( s_PlayerSlot[index] == null )
					{
						if(s_Player[channel].getState() == Player.PREFETCHED) 
						{
							s_Player[channel].deallocate();
						}
						if(s_Player[channel].getState() == Player.REALIZED ||  s_Player[channel].getState() == Player.UNREALIZED) 
						{
							s_Player[channel].close();
						}
					}
				}
			}
			catch( Exception ex )
			{
			}

			s_Player[channel] = null;
		}
		s_state[channel] = k_state_unprepared;
	}

	//--------------------------------------------------------------------------------------------------------------------
	/// Stop all sounds on all channel.
	//--------------------------------------------------------------------------------------------------------------------
	static void StopAllSounds ()// throws Exception
	{
		for (int i = 0; i < s_nbChannel; i++)
		{
			Stop(i);
		}

		if( s_pThread == null )
		{
			Update();
		}
	}
	
	static void FreeAllChannels()// throws Exception
	{
		for (int i = 0; i < s_nbChannel; i++)
		{
			FreeChannel(i);
		}

		if( s_pThread == null )
		{
			Update();
		}
	}
}
