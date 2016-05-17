package controllers;

import messaging.CollectionMessageConnection;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.messaging.CollectionMessage;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Josh on 4/25/16.
 */
//@Security.Authenticated(Secured.class)
public class Message extends Controller {
    @Inject
    private CollectionMessageConnection collectionMessageConnection;

    public Result testMessage() {
        CollectionMessage c = new CollectionMessage();
        c.setSetId(1003);
        c.setCollectionRecordId("123456789");

        try {
            collectionMessageConnection.sendMessage(c);
            return ok("Sent!");
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("Trouble!");
        }
    }
}
