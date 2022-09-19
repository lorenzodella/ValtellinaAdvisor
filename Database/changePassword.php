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

	$password = $_POST["password"];
    $idUtente = $_POST["idUtente"];
    $q = "UPDATE `valtAdv_utenti` SET password = '$password' WHERE idUtente=$idUtente;";
    $ris = mysqli_query($con, $q);
    if(!$ris)
    	echo "error";
    else
    	echo $password;
    //echo json_encode($ris, JSON_UNESCAPED_UNICODE);

?>