package enterroute;

import databaseUtility.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class DataGetter {
    Connection connection;

    public DataGetter() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException sqex) {
            sqex.printStackTrace();
        }
        if (this.connection == null) {
            System.exit(1);
        }
    }

    public boolean isDatabaseConnected() {
        return this.connection !=null;
    }

    public ObservableList getStations () {
        ObservableList<String> menuElements = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "SELECT name FROM Stations ORDER BY name;";
        try {
            preparedStatement = this.connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                menuElements.add(resultSet.getString("name"));
            }
        } catch (SQLException sqex) {
            sqex.printStackTrace();
        }
        finally {
            try {
                preparedStatement.close();
                resultSet.close();
            } catch (SQLException sqex) {
                sqex.printStackTrace();
            }
        }
        return menuElements;
    }

    public String routeCalculation(String from, String destination, Date timeOfDeparture) throws SQLException {
        int fromKO = -1;
        int fromKNF = -2;
        int destKO = -3;
        int destKNF = -4;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "SELECT * FROM Stations;";
        System.out.println("Starting calculation");
        try {
            preparedStatement = this.connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println("calculating.");
                if (resultSet.getString("name").equals(from)) {
                    fromKO = resultSet.getInt("KoebenhavnOdense");
                    fromKNF = resultSet.getInt("KoebenhavnNykoebingFalster");
                    System.out.println("Found routes on station" + resultSet.getInt("KoebenhavnOdense") + resultSet.getInt("KoebenhavnNykoebingFalster"));
                }
                if (resultSet.getString("name").equals(destination)) {
                    destKO = resultSet.getInt("KoebenhavnOdense");
                    destKNF = resultSet.getInt("KoebenhavnNykoebingFalster");
                }
            }
            if (fromKNF == destKNF) {
                sql = "SELECT * FROM KoebenhavnNykoebingfalster ORDER BY ?;";
                preparedStatement = this.connection.prepareStatement(sql);
                preparedStatement.setString(1,from);
                resultSet = preparedStatement.executeQuery();
                return determineValidTrip(from, destination, timeOfDeparture, resultSet);
            } else if (fromKO == destKO && fromKO == 1) {
                sql = "SELECT * FROM KoebenhavnOdense ORDER BY ?;";
                preparedStatement = this.connection.prepareStatement(sql);
                preparedStatement.setString(1, from);
                resultSet = preparedStatement.executeQuery();
                return determineValidTrip(from, destination, timeOfDeparture, resultSet);
            } else {
                sql = "SELECT * FROM KoebenhavnOdense, KoebenhavnNykoebingfalster ORDER BY ?;";
                preparedStatement = this.connection.prepareStatement(sql);
                preparedStatement.setString(1, from);
                resultSet = preparedStatement.executeQuery();
                DateFormat format = new SimpleDateFormat("HH:mm");
                try {
                    while (resultSet.next()) {
                        System.out.println("determine " + resultSet.getString(from));
                        if (!resultSet.wasNull()) {
                            Date resultRowFrom = format.parse(resultSet.getString(from));
                            Date resultRowDestRingsted = format.parse(resultSet.getString("Ringsted"));
                            if (resultRowFrom.after(timeOfDeparture) && resultRowFrom.before(resultRowDestRingsted)) {
                                System.out.println("Trip found");
                                String strReturnTime1 = format.format(resultRowFrom);
                                try {
                                    while (resultSet.next()) {
                                        System.out.println("determine " + resultSet.getString(from));
                                        if (!resultSet.wasNull()) {
                                            Date resultRowFromRingsted = format.parse(resultSet.getString("Ringsted"));
                                            Date resultRowDest = format.parse(resultSet.getString(destination));
                                            if (resultRowFromRingsted.after(resultRowDestRingsted) && resultRowFromRingsted.before(resultRowDest)) {
                                                System.out.println("Trip found");
                                                String strReturnTime2 = format.format(resultRowFromRingsted);
                                                return (strReturnTime1+" from "+from+" transit at Ringsted at "+strReturnTime2+" to "+destination+".");
                                            }
                                        }
                                    }
                                }catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return "failure to find.";
                            }
                        }
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
                return "failure to find.";
            }
        } catch (SQLException sqex) {
            sqex.printStackTrace();
        }
        finally {
            try {
                preparedStatement.close();
                resultSet.close();
            } catch (SQLException sqex) {
                sqex.printStackTrace();
            }
        }
        return "failure to find.";//Work on return statement... And if change is needed.
    }

    private String determineValidTrip(String from, String destination, Date timeOfDeparture, ResultSet resultSet) throws SQLException {
        DateFormat format = new SimpleDateFormat("HH:mm");
            try {
                while (resultSet.next()) {
                    System.out.println("determine " + resultSet.getString(from));
                    if (!resultSet.wasNull()) {
                        Date resultRowFrom = format.parse(resultSet.getString(from));
                        Date resultRowDest = format.parse(resultSet.getString(destination));
                        if (resultRowFrom.after(timeOfDeparture) && resultRowFrom.before(resultRowDest)) {
                            System.out.println("Trip found");
                            String strReturnTime = format.format(resultRowFrom);
                            return (strReturnTime + " from " + from);
                        }
                    }
                }
            }catch (ParseException e) {
                e.printStackTrace();
            }
        return "failure to find.";
    }
}
