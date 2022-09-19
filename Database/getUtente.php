<?php
	$host="localhost";
    $dbUser="root";
    $dbPwd="root";
    $dbName="my_dellamateralorenzo";

    $con=mysqli_connect($host, $dbUser, $dbPwd, $dbName);
    if(!$con)
    {
        die("Errore di connessione al database");
    }
    //echo "Connesso al database $dbName";
    //echo "<br><br>";
    
    mysqli_set_charset($con, "utf8");

	$username = $_POST["username"];
    $password = $_POST["password"];
    $q = "SELECT valtAdv_utenti.*, COUNT(idRecensione) AS numRecensioni, AVG(voto) AS mediaVoti
          FROM valtAdv_utenti
          LEFT JOIN valtAdv_recensioni USING(idUtente)
          WHERE username = '$username' AND password = '$password'";
    $ris = mysqli_fetch_array(mysqli_query($con, $q), MYSQLI_ASSOC);
    //var_dump($ris);
    $json = json_encode($ris, JSON_UNESCAPED_UNICODE);
    if($json == "null")
    	echo "error";
    else
    	echo $json;

?>