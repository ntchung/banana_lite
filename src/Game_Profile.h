
private static final String	recordName = "Game";

private static final int recordOptionSound = 0;
private static final int recordOptionLanguage = recordOptionSound + 1;
private static final int recordHighScore = recordOptionLanguage + 1;
private static final int recordSize = recordHighScore + 4;

public static int optionSound;
public static int optionLanguage;
public static int highScore;

public static String optionUsername = "";
public static String optionPassword = "";
 
private void resetProfile()
{
	// Reset profile
	optionSound = 1;
	optionLanguage = 0;
	highScore = 0;
	
	optionUsername = "";
	optionPassword = "";
	saveProfile();
}

private void validateProfile()
{	
	if( optionSound != 0 )
	{
		optionSound = 1;
	}
	
	if( optionLanguage != 0 )
	{
		optionLanguage = 1;
	}
	
	if( highScore < 0 )
	{
		highScore = 0;
	}
}
 
private boolean loadProfile()
{	
	try
	{
		// NTChung - TO REMOVE	
		//resetProfile();
		
		byte[] data = rmsRead( recordName, 1 );
		if( data == null )
		{
			// Reset profile
			resetProfile();			
			return false;
		}
		else
		{
			optionLanguage = data[recordOptionLanguage];
			optionSound = data[recordOptionSound];
			highScore = Util.bytes2Int(data, recordHighScore);
			
			int offset = recordSize;
			int dataUsernameLength = data[offset];
			++offset;
			int dataPasswordLength = data[offset];			
			++offset;
			byte[] dataUsername = new byte[dataUsernameLength];
			byte[] dataPassword = new byte[dataPasswordLength];
			System.arraycopy(data, offset, dataUsername, 0, dataUsernameLength);
			offset += dataUsernameLength;
			System.arraycopy(data, offset, dataPassword, 0, dataPasswordLength);
			
			optionUsername = new String(dataUsername);
			optionPassword = new String(dataPassword);
			
			validateProfile();
			data = null;
		}	
	
	}
	catch( Exception ex )
	{
		resetProfile();
		return false;
	}
	
	return true;
}

public void saveProfile()
{
	byte[] dataUsername = optionUsername.getBytes();
	byte[] dataPassword = optionPassword.getBytes();

	byte[] data = new byte[recordSize + 2 + dataUsername.length + dataPassword.length ];
	
	data[recordOptionLanguage] = (byte)optionLanguage;
	data[recordOptionSound] = (byte)optionSound;	
	Util.int2Bytes(data, recordHighScore, highScore);
	
	int offset = recordSize;
	data[offset] = (byte)dataUsername.length;
	++offset;
	data[offset] = (byte)dataPassword.length;
	++offset;
	System.arraycopy(dataUsername, 0, data, offset, dataUsername.length);
	offset += dataUsername.length;
	System.arraycopy(dataPassword, 0, data, offset, dataPassword.length);
	
	rmsWrite( recordName, 1, data );
}

private boolean loadGameData()
{
	try
	{
		byte[] data = rmsRead( recordName, 2 );
		if( data == null )
		{
			LoadGame(null, 0);
			return false;
		}
		else
		{
			LoadGame(data, 0);
			data = null;
		}	
	
	}
	catch( Exception ex )
	{
		LoadGame(null, 0);
		return false;
	}
	
	return true;
}

public void clearGameData()
{
	byte[] data = new byte[1];
	data[0] = 0;
	rmsWrite( recordName, 2, data );
	data = null;
}

public void saveGameData()
{
	byte[] data = new byte[2048];
	SaveGame(data, 0);
	rmsWrite( recordName, 2, data );
	data = null;
}

private static RecordStore s_rs;

private static void rmsOpen( String strName ) throws RecordStoreException
{
	// open own recordstore
	s_rs = RecordStore.openRecordStore( strName, true );
}

private static void rmsClose()
{
	// it's already closed?
	if( s_rs == null )
	{
		return;
	}
	
	// closing recordstore...
	try
	{
		s_rs.closeRecordStore();
	}
	catch( RecordStoreException e )
	{
	}

	s_rs = null;
}

static byte[] rmsRead( String strName, int id )
{
	byte[] data = null;

	try
	{
		// open the recordstore
		rmsOpen(strName);
		
		// get the specified record
		if( s_rs.getNumRecords() >= id )
		{
			data = s_rs.getRecord(id);
		}
	}
	catch( RecordStoreException e )
	{
		data = null;
	}
	
	// close recordstore in any case
	rmsClose();
	
	// return the data
	return data;
}

static void rmsWrite( String strName, int id, byte[] data )
{
	try
	{
		// open the recordstore
		rmsOpen(strName);

		// save the specified record
		if(s_rs.getNumRecords() >= id)
		{
			s_rs.setRecord(id, data, 0, data.length);
		}
		else
		{
			s_rs.addRecord(data, 0, data.length);
		}
	}
	catch (RecordStoreException e)
	{
	}
	
	// close recordstore in any case
	rmsClose();
}

private int getRecordsNum( String strName )
{
	int num = 0;

	try
	{
		// open the recordstore
		rmsOpen(strName);
		
		// get the specified record
		num = s_rs.getNumRecords();
	}
	catch( RecordStoreException e )
	{
	}
	
	// close recordstore in any case
	rmsClose();
	
	// return the data
	return num;
}
