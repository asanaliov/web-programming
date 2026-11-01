package mk.ukim.finki.wp.lab1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mk.ukim.finki.wp.lab1.model.Team;
import mk.ukim.finki.wp.lab1.service.PlayerService;
import mk.ukim.finki.wp.lab1.service.TeamService;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet(name = "PlayerServlet", urlPatterns = "/players")
public class PlayerServlet extends HttpServlet {

    private final PlayerService playerService;
    private final TeamService teamService;
    private final SpringTemplateEngine springTemplateEngine;


    public PlayerServlet(PlayerService playerService, TeamService teamService, SpringTemplateEngine springTemplateEngine) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String teamIdParam = req.getParameter("teamId");
        if (teamIdParam == null || teamIdParam.isBlank()){
            resp.sendRedirect("/teams");
        }
        Team team;
        try{
            team = teamService.findById(Long.parseLong(teamIdParam));
        } catch (IllegalArgumentException e){
            resp.sendRedirect("/teams");
            return;
        }
        IWebExchange webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext context = new WebContext(webExchange);
        context.setVariable("players", playerService.listPlayers());
        context.setVariable("team", team);
        context.setVariable("errorMessage", req.getParameter("errorMessage"));

        resp.setContentType("text/html; charset=UTF-8");
        springTemplateEngine.process("playerList.html", context, resp.getWriter());
    }
}
