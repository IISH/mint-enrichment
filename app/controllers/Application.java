package controllers;

import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import javax.inject.Inject;

/**
 * Default application controller.
 */
public class Application extends Controller {
    @Inject
    private FormFactory formFactory;

    /**
     * Display the login page or dashboard if connected.
     *
     * @return Either the login page or the dashboard.
     */
    public Result index() {
        String login = ctx().session().get("login");
        if (login != null) {
            User user = User.findByLogin(login);
            if (user != null) {
                return redirect(routes.Application.index());
            }
            else {
                session().clear();
            }
        }
        return ok(index.render(formFactory.form(Login.class)));
    }

    /**
     * Login class used by Login Form.
     */
    public static class Login {
        @Constraints.Required
        private String login;

        @Constraints.Required
        private String password;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * Validate the authentication.
         *
         * @return null Null if validation ok, string with details otherwise.
         */
        public String validate() {
            User user = User.authenticate(login, password);
            if (user == null) {
                return Messages.get("invalid.user.or.password");
            }
            return null;
        }
    }

    /**
     * Handle login form submission.
     *
     * @return Dashboard if auth OK or login form if auth KO.
     */
    public Result authenticate() {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            return badRequest(index.render(loginForm));
        }
        else {
            session("login", loginForm.get().login);
            return redirect(routes.Dashboard.index());
        }
    }

    /**
     * Logout and clean the session.
     *
     * @return The index page.
     */
    public Result logout() {
        session().clear();
        flash("success", Messages.get("youve.been.logged.out"));
        return redirect(routes.Application.index());
    }
}