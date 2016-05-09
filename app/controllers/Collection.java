package controllers;

import models.User;
import models.collection.CollectionRecord;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.edm.EDM;
import views.html.collection.index;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by Josh on 4/28/16.
 */
public class Collection extends Controller {
    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(Secured.class)
    public Result createCollection() {
        String uuid = UUID.randomUUID().toString();
        Form<CollectionRecord> userForm = formFactory.form(CollectionRecord.class);
        userForm = userForm.fill(new CollectionRecord(uuid));
        User user = User.findByLogin(request().username());
        return ok(index.render(user, userForm));
    }

    @Security.Authenticated(Secured.class)
    public Result makeCollection() {
        User user = User.findByLogin(request().username());
        Form<CollectionRecord> userForm = formFactory.form(CollectionRecord.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest();
        } else {
            CollectionRecord cr = userForm.get();
            EDM edm = new EDM(cr);
            return ok(edm.createEDM());
        }
    }
}