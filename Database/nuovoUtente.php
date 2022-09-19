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
    $mail = $_POST["mail"];
    $q = "SELECT username, mail
          FROM valtAdv_utenti
          WHERE username = '$username' OR mail = '$mail'";
    $ris = mysqli_fetch_array(mysqli_query($con, $q), MYSQLI_ASSOC);
    if($ris != NULL){
    	if($ris["username"] == $username)
        	echo "Errore: username non disponibile";
        else if($ris["mail"] == $mail)
        	echo "Errore: mail non disponibile";
    }
    else {
        $nome = $_POST["nome"];
        $cognome = $_POST["cognome"];
        $colore = $_POST["colore"];
    	$password = $_POST["password"];
    	$q = "INSERT INTO valtAdv_utenti (username, password, nome, cognome, mail, colore)
        		VALUES ('$username', '$password', '$nome', '$cognome', '$mail', '$colore')";
    	$ris = mysqli_query($con, $q);
        if(!$ris)
            echo "error";
        else
            var_dump($ris);
    }

?>