<?php
	$debug = 0;
	
	// Turn off errors reporting
	error_reporting(0);	
	
	$db = mysql_connect('localhost', 'kinoapte_cascore', 'pBhrgva5pGx5zZvW') or die('Could not connect: ' . mysql_error()); 
	mysql_select_db('kinoapte_castleattack') or die('Could not select database');	
	
	$username = mysql_real_escape_string($_GET['username'], $db);
	$password = mysql_real_escape_string($_GET['password'], $db);
	$score = mysql_real_escape_string($_GET['score'], $db);
	$check = mysql_real_escape_string($_GET['check'], $db);
	
	$combine = urlencode($username) . $password . $score;
	$myCheck = md5($combine);
	
	if( $myCheck != $check )
	{
		die('Wrong');
	}
	
	$query = "SELECT username, password, score FROM `javascores` WHERE username='$username'";
    $result = mysql_query($query) or die('Query failed: ' . mysql_error());
 
    $num_results = mysql_num_rows($result);  
    if ($num_results > 0)
    {
		$row = mysql_fetch_array($result);
		if( $password == $row['password'] )
		{
			$query = "UPDATE javascores SET score=$score WHERE username='$username'"; 		
			mysql_query($query) or die('Query failed: ' . mysql_error());
			echo '0';
		}
		else
		{
			echo '1';
		}        
    }
	else
	{
		$query = "insert into javascores(username, password, score) values ('$username', '$password', '$score')"; 		
		mysql_query($query) or die('Query failed: ' . mysql_error());
		echo '0';
	}	
?>
