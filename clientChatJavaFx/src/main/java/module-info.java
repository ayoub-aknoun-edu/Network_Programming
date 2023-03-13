module me.exemple.clientchatjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens me.exemple.clientchatjavafx to javafx.fxml;
    exports me.exemple.clientchatjavafx;
}