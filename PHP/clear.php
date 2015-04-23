<?php
include("dbConnect.php");
//include("debug.php");
echo "usage: to clear one event, add ?eID=x to the URL, where x is the event ID. <br>to clear all stats, add ?ClearAll=1 to the URL <br>this removes all the matches, games, and modes/mode_player records from the database for either a specific event or all events";

if(isset($_GET[eID]))
{
	$matchesRemoved = 0;
	$matchesQuery = $mysqli->prepare("SELECT id FROM match_table WHERE event_id = ?");
	$matchesQuery->bind_param('i', $_GET[eID]);
	$matchesQuery->execute();
	$matchesQuery->store_result();
	$matchesQuery->bind_result($matchID);
	while($matchesQuery->fetch())
	{
		$matchDelete = $mysqli->prepare("DELETE FROM match_table WHERE id = ?");
		$matchDelete->bind_param('i', $matchID);
		$matchDelete->execute();

		$gameIDQuery = $mysqli->prepare("SELECT id FROM game WHERE match_id = ?");
		$gameIDQuery->bind_param('i', $matchID);
		$gameIDQuery->execute();
		$gameIDQuery->store_result();
		$gameIDQuery->bind_result($gameID);
		while($gameIDQuery->fetch())
		{
			$gameDelete = $mysqli->prepare("DELETE FROM game WHERE id = ?");
			$gameDelete->bind_param('i', $gameID);
			$gameDelete->execute();
			//hp
			$hpIDQuery = $mysqli->prepare("SELECT id FROM hp WHERE game_id = ?");
			$hpIDQuery->bind_param('i', $gameID);
			$hpIDQuery->execute();
			$hpIDQuery->store_result();
			$hpIDQuery->bind_result($hpID);
			while($hpIDQuery->fetch())
			{
				$hpDelete = $mysqli->prepare("DELETE FROM hp WHERE id = ?");
				$hpDelete->bind_param('i', $hpID);
				$hpDelete->execute();

				$hpPlayerDelete = $mysqli->prepare("DELETE FROM hp_player WHERE hp_id = ?");
				$hpPlayerDelete->bind_param('i', $hpID);
				$hpPlayerDelete->execute();
			}
			//ctf
			$ctfIDQuery = $mysqli->prepare("SELECT id FROM ctf WHERE game_id = ?");
			$ctfIDQuery->bind_param('i', $gameID);
			$ctfIDQuery->execute();
			$ctfIDQuery->store_result();
			$ctfIDQuery->bind_result($ctfID);
			while($ctfIDQuery->fetch())
			{
				$ctfDelete = $mysqli->prepare("DELETE FROM ctf WHERE id = ?");
				$ctfDelete->bind_param('i', $ctfID);
				$ctfDelete->execute();

				$ctfPlayerDelete = $mysqli->prepare("DELETE FROM ctf_player WHERE ctf_id = ?");
				$ctfPlayerDelete->bind_param('i', $ctfID);
				$ctfPlayerDelete->execute();
			}
			//snd
			$sndIDQuery = $mysqli->prepare("SELECT id FROM snd WHERE game_id = ?");
			$sndIDQuery->bind_param('i', $gameID);
			$sndIDQuery->execute();
			$sndIDQuery->store_result();
			$sndIDQuery->bind_result($sndID);
			while($sndIDQuery->fetch())
			{
				$sndDelete = $mysqli->prepare("DELETE FROM snd WHERE id = ?");
				$sndDelete->bind_param('i', $sndID);
				$sndDelete->execute();

				$sndPlayerDelete = $mysqli->prepare("DELETE FROM snd_player WHERE snd_id = ?");
				$sndPlayerDelete->bind_param('i', $sndID);
				$sndPlayerDelete->execute();
			}
			//uplink
			$uplinkIDQuery = $mysqli->prepare("SELECT id FROM uplink WHERE game_id = ?");
			$uplinkIDQuery->bind_param('i', $gameID);
			$uplinkIDQuery->execute();
			$uplinkIDQuery->store_result();
			$uplinkIDQuery->bind_result($uplinkID);
			while($uplinkIDQuery->fetch())
			{
				$uplinkDelete = $mysqli->prepare("DELETE FROM uplink WHERE id = ?");
				$uplinkDelete->bind_param('i', $uplinkID);
				$uplinkDelete->execute();

				$uplinkPlayerDelete = $mysqli->prepare("DELETE FROM uplink_player WHERE uplink_id = ?");
				$uplinkPlayerDelete->bind_param('i', $uplinkID);
				$uplinkPlayerDelete->execute();
			}
		}
		$matchesRemoved++;
	}
	echo 'removed ' . $matchesRemoved . ' matches for event with ID ' . $_GET[eID];
}
if(isset($_GET[ClearAll]) && $_GET[ClearAll] == 1)
{
	$hp = $mysqli->prepare("TRUNCATE hp");
	$hp->execute();
	$hpP = $mysqli->prepare("TRUNCATE hp_player");
	$hpP->execute();

	$ctf = $mysqli->prepare("TRUNCATE ctf");
	$ctf->execute();
	$ctfP = $mysqli->prepare("TRUNCATE ctf_player");
	$ctfP->execute();

	$uplink = $mysqli->prepare("TRUNCATE uplink");
	$uplink->execute();
	$uplinkP = $mysqli->prepare("TRUNCATE uplink_player");
	$uplinkP->execute();

	$snd = $mysqli->prepare("TRUNCATE snd");
	$snd->execute();
	$sndP = $mysqli->prepare("TRUNCATE snd_player");
	$sndP->execute();

	$game = $mysqli->prepare("TRUNCATE game");
	$game->execute();

	$match = $mysqli->prepare("TRUNCATE match_table");
	$match->execute();

	echo 'all tables cleared';
}
?>