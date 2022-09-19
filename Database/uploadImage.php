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
    echo "Connesso al database $dbName";
    echo "<br><br>";

    extract($_POST);

    $UploadedFileName=$_FILES['UploadImage']['name'];
    //var_dump($_FILES['UploadImage']);
    if($UploadedFileName!='')
    {
        $upload_directory = "images/"; //This is the folder which you created just now
        $TargetPath=$upload_directory.$UploadedFileName;
        $id = $_POST["id"];
        if(move_uploaded_file($_FILES['UploadImage']['tmp_name'], $TargetPath)){    
            $q="UPDATE valtAdv_ristoranti SET image = '$UploadedFileName' WHERE idRistorante = $id;"; 
            mysqli_query($con, $q);
            echo "immagine salvata";
            echo "<a href=getImage.php?id=$id>visualizza</a>";
            die();
        }
    }
    echo "errore";
    
?>