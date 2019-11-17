import enterroute.DataGetter;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DataGetterTest {
    DataGetter test = new DataGetter();
    DateFormat format = new SimpleDateFormat("HH:mm");
    String dateString = ("00:01");
    Date date = format.parse(dateString);

    public DataGetterTest() throws ParseException {
    }

    @Test
    public void TestDataBaseConnection() {
        assert test.isDatabaseConnected();
    }
    @Test
    public void TestDataExtraction() {
        assert (test.getStations().size() == 7);
    }
    @Test
    public void TestRouteCalculation() throws SQLException {
        assert (Objects.deepEquals(test.routeCalculation("Ringsted", "Roskilde", date), "failure to find."));
    }
}
