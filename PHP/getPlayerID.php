<?php
include("dbConnect.php");
$playerIDQuery = $mysqli->prepare("SELECT id FROM  player WHERE STRCMP(alias, ?) = 0");
$playerIDQuery->bind_param('s', $_POST[PlayerName]);
$playerIDQuery->execute();
$playerIDQuery->store_result();
$playerIDQuery->bind_result($playerID);
$playerIDQuery->fetch();
echo $playerID;
?>