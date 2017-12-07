package sendedMessages;

/**
 * Created by Wirzourn on 5.12.2017.
 */

//Message That have the value true or false indicates whether requested action accomplished or failed
public class RequestStatusMessage {
    public String type;
    public String status;

    public RequestStatusMessage(String type, String status){
        this.setType(type);
        this.setStatus(status);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
