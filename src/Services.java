import javafx.collections.ObservableList;

import java.io.IOException;
import java.text.ParseException;

public interface Services {

    public void addRecord() throws ParseException;
    public void updateRecord() throws IOException ;
    public void deleteRecord() throws IOException;
    public ObservableList addShowListDataToTableview();
    public void showSelectedData();

}
