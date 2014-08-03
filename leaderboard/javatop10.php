<?php
	
	$debug = 0;
	
	// Turn off errors reporting
	error_reporting(0);	
	
	$db = mysql_connect('localhost', 'kinoapte_cascore', 'pBhrgva5pGx5zZvW') or die('Could not connect: ' . mysql_error()); 
	mysql_select_db('kinoapte_castleattack') or die('Could not select database');	
	
	// Top 10
	$query = "SELECT username, score FROM `javascores` ORDER by `score` DESC LIMIT 10";
    $result = mysql_query($query) or die('Query failed: ' . mysql_error());
 
    $num_results = mysql_num_rows($result);  
    for($i = 0; $i < $num_results; $i++)
    {
         $row = mysql_fetch_array($result);
         echo $row['username'] . "\n" . $row['score'] . "\n";
    }
?>
