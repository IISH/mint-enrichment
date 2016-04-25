package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import messaging.RabbitMQConnection;
import messaging.CollectionMessage;

import java.io.IOException;

/**
 * Created by Josh on 4/25/16.
 */
@Security.Authenticated(Secured.class)
public class Message extends Controller {

    public Result testMessage() {
        CollectionMessage c = new CollectionMessage();
        c.setSetId("1004");
        c.setCollectionRecordId("123456789");
        RabbitMQConnection rbmq = new RabbitMQConnection();
        try {
            rbmq.sendMessage(c);
            return ok("Sent!");
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("Trouble!");
        }
    }
}
