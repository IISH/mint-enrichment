package messaging;

import java.io.Serializable;

public class DummyMessage implements Serializable {
    public String messageOne;
    public String messageTwo;

    @Override
    public String toString() {
        return "DummyMessage{" +
                "messageOne='" + messageOne + '\'' +
                ", messageTwo='" + messageTwo + '\'' +
                '}';
    }
}