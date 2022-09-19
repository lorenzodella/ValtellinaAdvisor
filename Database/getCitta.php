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

    $q = "SELECT * FROM valtAdv_citta";
    $ris = mysqli_fetch_all(mysqli_query($con, $q), MYSQLI_ASSOC);
    //var_dump($ris);
    echo json_encode($ris);

?>