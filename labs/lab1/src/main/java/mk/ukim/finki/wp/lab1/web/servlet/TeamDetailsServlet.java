package mk.ukim.finki.wp.lab1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mk.ukim.finki.wp.lab1.model.Team;
import mk.ukim.finki.wp.lab1.service.TeamService;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "TeamDetailsServlet", urlPatterns = "/teamDetails")
public class TeamDetailsServlet extends HttpServlet {
    private final TeamService teamService;
    private final SpringTemplateEngine springTemplateEngine;

    public TeamDetailsServlet(TeamService teamService, SpringTemplateEngine springTemplateEngine) {
        this.teamService = teamService;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String teamIdParam = req.getParameter("teamId");
        if (teamIdParam == null || teamIdParam.isBlank()) {
            resp.sendRedirect("/teams");
            return;
        }

        Team team;
        try {
            team = teamService.findById(Long.parseLong(teamIdParam));
        } catch (IllegalArgumentException e) {
            resp.sendRedirect("/teams");
            return;
        }

        IWebExchange webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext context = new WebContext(webExchange);
        context.setVariable("team", team);

        resp.setContentType("text/html; charset=UTF-8");
        springTemplateEngine.process("teamDetails.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String teamIdParam = req.getParameter("teamId");
        String playerId = req.getParameter("playerId");

        if (teamIdParam == null || teamIdParam.isBlank()) {
            resp.sendRedirect("/teams");
            return;
        }

        try {
            if (playerId == null || playerId.isBlank()) {
                throw new IllegalArgumentException("Please select a player");
            }
            teamService.addPlayerToTeam(Long.parseLong(teamIdParam), playerId);
            resp.sendRedirect("/teamDetails?teamId=" + teamIdParam);
        } catch (IllegalArgumentException e) {
            String message = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            resp.sendRedirect("/players?teamId=" + teamIdParam + "&errorMessage=" + message);
        }
    }
}
