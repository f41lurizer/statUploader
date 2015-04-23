
<?php
//link teamIDs to rosterIDs in roster_to_event so it doesn't have to be done manually
include("dbConnect.php");
$rosterQuery = $mysqli->prepare("SELECT roster_id FROM roster_to_event GROUP BY roster_id");
$rosterQuery->execute();
$rosterQuery->store_result();
$rosterQuery->bind_result($rosterID);
while($rosterQuery->fetch())
{
	$teamIDQuery = $mysqli->prepare("SELECT team_id FROM roster WHERE id = ?");
	$teamIDQuery->bind_param('i', $rosterID);
	$teamIDQuery->execute();
	$teamIDQuery->store_result();
	$teamIDQuery->bind_result($teamID);
	$teamIDQuery->fetch();

	$update = $mysqli->prepare("UPDATE roster_to_event SET team_id = ? WHERE roster_id = ?");
	$update->bind_param('ii', $teamID, $rosterID);
	$update->execute();
	echo 'updated records for: ' . $teamID . "rosterID: " . $rosterID . '</br>';
}
echo 'all records updated';
?>