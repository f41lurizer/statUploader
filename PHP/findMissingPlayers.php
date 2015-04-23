<?php
include("dbConnect.php");
include("debug.php");
echo "usage: finds if there are any player ID's in player to roster that do not exist in player table by player id";

$playerIDQuery = $mysqli->prepare("SELECT player_id, roster_id FROM  player_roster");
$playerIDQuery->execute();
$playerIDQuery->store_result();
$playerIDQuery->bind_result($playerID, $rosterID);
$missingCount = 0;
$count = 0;
while($playerIDQuery->fetch())
{
	$playerExistsQuery = $mysqli->prepare("SELECT alias FROM  player WHERE id = ?");
	$playerExistsQuery->bind_param('i', $playerID);
	$playerExistsQuery->execute();
	$playerExistsQuery->store_result();
	$playerExistsQuery->bind_result($resultExists);
	$exists = 0;
	while($playerExistsQuery->fetch())
	{
		$count++;
		$exists = 1;
	}
	if($exists == 0)
	{
		$missingCount++;
		echo 'player not found with ID: ' . $playerID . ' with rosterID: ' . $rosterID . '<br>';
	}


}
echo "missing players: " . $missingCount . "<br>" . "total players" . $count;
?>
