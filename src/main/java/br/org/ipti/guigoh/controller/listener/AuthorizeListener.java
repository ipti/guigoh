/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.ipti.guigoh.controller.listener;

import br.org.ipti.guigoh.util.CookieService;
import br.org.ipti.guigoh.model.entity.UserAuthorization;
import br.org.ipti.guigoh.model.entity.Users;
import br.org.ipti.guigoh.model.jpa.controller.UserAuthorizationJpaController;
import br.org.ipti.guigoh.model.jpa.controller.UsersJpaController;
import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author IPTI
 */
public class AuthorizeListener implements PhaseListener {

    @Override
    public void afterPhase(PhaseEvent event) {
        // Obtém o contexto atual
        FacesContext context = event.getFacesContext();
        // Obtém a página que atualmente está interagindo com o ciclo
        // Se for a página 'login.jsp' seta a variável como true
        boolean isLoginPage = context.getViewRoot().getViewId().lastIndexOf("/login/auth.xhtml") > -1;
        boolean isRegisterPage = context.getViewRoot().getViewId().lastIndexOf("/login/create.xhtml") > -1;
        // Obtém a sessão atual
        // Resgata o nome do usuário logado
        Users user = new Users();
        user.setUsername(CookieService.getCookie("user"));
        user.setToken(CookieService.getCookie("token"));
        Boolean activeAccess = false;
        Boolean inactiveAccess = false;
        Boolean pendingAccess = false;
        Boolean confirmed = false;
        Boolean pending = false;
        if (user.getUsername() != null) {
            UsersJpaController usersJpaController = new UsersJpaController();
            Users usertemp = usersJpaController.findUsers(user.getUsername());

            confirmed = (usertemp.getStatus().equals("CA"));
            pending = (usertemp.getStatus().equals("CP"));

            UserAuthorizationJpaController userAuthorizationJpaController = new UserAuthorizationJpaController();
            UserAuthorization authorization = userAuthorizationJpaController.findUserAuthorization(usertemp.getToken());
            if (authorization != null) {
                activeAccess = (authorization.getStatus().equals("AC"));
                inactiveAccess = (authorization.getStatus().equals("IC"));
                pendingAccess = (authorization.getStatus().equals("PC"));
            }
        }
        try {
            // Verifica se o usuário não está na página de registro
            if (!isRegisterPage) {
                if (user.getUsername() != null) {
                    CookieService.addCookie("user", user.getUsername());
                    CookieService.addCookie("token", user.getToken());
                    if (isLoginPage) {
                        // Se o usuário logado tentar acessar a página de login, ele é redirecionado para a página inicial
                        FacesContext.getCurrentInstance().getExternalContext().redirect("/home.xhtml");
                    }
                } else {
                    if (!isLoginPage) {
                        HttpServletRequest httpServletRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
                        CookieService.addCookie("page-to-redirect", httpServletRequest.getRequestURL().toString());
                        FacesContext.getCurrentInstance().getExternalContext().redirect("/login/auth.xhtml");
                    }

                }
            }
        } catch (IOException e) {

        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
