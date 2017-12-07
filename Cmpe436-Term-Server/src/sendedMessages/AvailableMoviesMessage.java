package sendedMessages;



import java.util.ArrayList;

/**
 * Created by Wirzourn on 5.12.2017.
 */
//THE MESSAGE THAT RETURNS THE MOVIES IN THE SELECTED DAY
public class AvailableMoviesMessage {
    public String type;
    public ArrayList<AvailableMovie> availableMovies;
    public AvailableMoviesMessage(String type, ArrayList<AvailableMovie> availableMovies){
        this.setType(type);
        this.setAvailableMovies(availableMovies);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<AvailableMovie> getAvailableMovies() {
        return availableMovies;
    }

    public void setAvailableMovies(ArrayList<AvailableMovie> availableMovies) {
        this.availableMovies = availableMovies;
    }
}
