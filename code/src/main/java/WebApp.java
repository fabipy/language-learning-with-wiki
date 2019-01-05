import java.util.HashMap;
import java.io.File;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import static spark.Spark.*;

/**
 * ==========================================
 * EBERHARD-KARLS UNIVERSITÄT TÜBINGEN
 * July 2016
 *
 * Course: ISCL
 * Subject: Data structures and Algorithms II
 * Final project
 *
 * The collective work of:
 * Daniel
 * Fabian
 * Luana
 * Pia
 * Rainer
 * Saleh
 * Stephanie
 * ==========================================
 *
 * Acknowledgement
 * ---------------
 * This program is based on an idea/program (WERTi) from Prof. Detmer Meurers of University of
 * Tübingen.
 *
 * Description
 * -----------
 * Class to set up our web application as the user interface for the user.
 *
 */

public class WebApp {

    /**
     * Sets up the routes and settings of the web application.
     *
     */
    public static void main(String[] args) {
        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions

        // Configure Spark
        port(4567); // http://localhost:4567/ for local
        staticFileLocation("/public"); // HTML & CSS files


        // Routes

        // http://localhosts:4567/wiki?url=WIKIPEDIA_URL?articles=ARTICLES
        get("/wiki", (request, response) -> {
            String path = new File("src/main/java").getAbsolutePath();

            Parser parser = new Parser(path);
            String url = request.queryParams("url");
            String cloze = request.queryParams("cloze").toUpperCase();

            return parser.processSite(url, WordClass.valueOf(cloze));
        });

        // http://localhost:4567/stats
        get("/stats", (request, response) -> {
            return new ModelAndView(new HashMap(), "stats.mustache");
        }, new MustacheTemplateEngine());

        // http://localhost:4567/
        get("/", (request, response) -> {
            return new ModelAndView(new HashMap(), "index.html");
        }, new MustacheTemplateEngine());
    }
}
