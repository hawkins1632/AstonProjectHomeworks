package event;

public class UserEvent {

    private EventType event;

    private String email;

    public UserEvent() {
    }

    public UserEvent(EventType event, String email) {
        this.event = event;
        this.email = email;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
