package enterroute;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class SelectController implements Initializable {

    DataGetter dataGetter = new DataGetter();

    @FXML
    private Label dbStatus;
    @FXML
    private ComboBox fromBox;
    @FXML
    private ComboBox destinationBox;
    @FXML
    private TextField timeOfDepature;
    @FXML
    private Button executeButton;
    @FXML
    private Label routeprint;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (this.dataGetter.isDatabaseConnected()) {
            this.dbStatus.setText("Connected");
            this.fromBox.setItems(dataGetter.getStations());
            this.destinationBox.setItems(dataGetter.getStations());
        } else {
            this.dbStatus.setText("No connection to database");
        }
    }

    public void calculateRoute(ActionEvent event) {
        if (this.timeOfDepature.getText().length() != 5) {
            this.routeprint.setText("Please input a correct time of departure.");
            System.out.println(this.timeOfDepature.toString());
        } else if (fromBox.getValue() == null || destinationBox.getValue() == null) {
            this.routeprint.setText("Please input a valid from and destination point.");
        } else {
            try {
                DateFormat format = new SimpleDateFormat("HH:mm");
                Date timeOfDeparture = format.parse(this.timeOfDepature.getText());
                int hour = Integer.parseInt(this.timeOfDepature.getText(0,2));
                int minutes = Integer.parseInt(this.timeOfDepature.getText(3,5));
                if (hour > 23 || minutes > 59) {
                    this.routeprint.setText("This program does not support decimal time, please use UTC standard time");
                } else if (fromBox.getValue().equals(destinationBox.getValue())) {
                    this.routeprint.setText("Congratulations you have arrived!");
                } else {
                    System.out.println("Route Calculation");
                    this.routeprint.setText(this.dataGetter.routeCalculation(fromBox.getValue().toString(),destinationBox.getValue().toString(),timeOfDeparture));
                }
            } catch (NumberFormatException nfex) {
                this.routeprint.setText("Please input a correct time of departure.");
                nfex.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
