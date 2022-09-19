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

    $q = "SELECT image FROM valtAdv_ristoranti WHERE idRistorante = ".$_GET["id"].";";
    $path = mysqli_fetch_array(mysqli_query($con, $q))[0];
    echo "<img src='images/$path'><br>";
    echo "<a href='http://dellamateralorenzo.altervista.org/valtellina_advisor/images/$path'>http://dellamateralorenzo.altervista.org/valtellina_advisor/images/$path</a>";

?>
