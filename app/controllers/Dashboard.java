package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.index;

/**
 * The dashboard controller.
 */
@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {
    public Result index() {
        return ok(index.render(User.findByLogin(request().username())));
    }
}
