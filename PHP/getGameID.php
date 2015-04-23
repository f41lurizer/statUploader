<?php
//takes matchID, gameNum, mapModeID, scoreTypeID
include("dbConnect.php");
$gameIDQuery = $mysqli->prepare("INSERT INTO game (match_id, game_number, map_mode_id, score_type_id) VALUES (?, ?, ?, ?)");
$gameIDQuery->bind_param('iiii', $_POST[MatchID], $_POST[GameNum] , $_POST[MapModeID], $_POST[ScoreTypeID]);
$gameIDQuery->execute();
$gameIDQuery->store_result();
$gameID = $mysqli->insert_id;
echo $gameID;
?>