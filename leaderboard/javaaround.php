<?php
	
	$debug = 0;
	
	// Turn off errors reporting
	error_reporting(0);	
	
	$db = mysql_connect('localhost', 'kinoapte_cascore', 'pBhrgva5pGx5zZvW') or die('Could not connect: ' . mysql_error()); 
	mysql_select_db('kinoapte_castleattack') or die('Could not select database');	
		
	$username = mysql_real_escape_string($_GET['username'], $db);
	
	// My ranking
	$query = "SELECT score, position FROM(
		SELECT username, score, @rownum:=@rownum+1 position
		FROM `javascores`, (SELECT @rownum:=0) r
		ORDER BY `score` DESC ) AS position WHERE `username` = '$username'";
		
	$result = mysql_query($query) or die('Query failed: ' . mysql_error());	
	$num_results = mysql_num_rows($result);  
	if( $num_results <= 0 )
	{		
		die("E");
	}
	
	echo "O\n";
	
	$row = mysql_fetch_array($result);
	echo $row['position'] . "\t" . $row['score'] . "\n";
	
	$score = intval($row['score']);
	
	// Lower 5
	$query = "SELECT username, score FROM `javascores` WHERE score < '$score' ORDER by `score` DESC LIMIT 10";
    $result = mysql_query($query) or die('Query failed: ' . mysql_error());
	
	$num_results = mysql_num_rows($result);  
    for($i = 0; $i < $num_results; $i++)
    {
         $row = mysql_fetch_array($result);
         echo $row['username'] . "\t" . $row['score'] . "\t";
    }
	echo "\n";

	// Higher 5
	$query = "SELECT username, score FROM `javascores` WHERE score > '$score' ORDER by `score` ASC LIMIT 10";
    $result = mysql_query($query) or die('Query failed: ' . mysql_error());
	
	$num_results = mysql_num_rows($result);  	
    for($i = 0; $i < $num_results; $i++)
    {
         $row = mysql_fetch_array($result);
         echo $row['username'] . "\t" . $row['score'] . "\t";
    }		
?>
