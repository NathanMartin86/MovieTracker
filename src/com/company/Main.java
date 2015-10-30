package com.company;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        ArrayList<Movies> films = new ArrayList();
        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        return new ModelAndView(new HashMap<>(), "not-logged-in.html");

                    } else {
                        HashMap m = new HashMap();
                        m.put("username", username);
                        m.put("films", films);
                        return new ModelAndView(m, "logged-in.html");
                    }

                }),
                new MustacheTemplateEngine()

        );
        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    Session session = request.session();
                    session.attribute("username", username);//what's happening here with the attribute function?
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/logout",
                (request1, response1) -> {
                    Session session = request1.session();
                    session.invalidate();
                    response1.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/create-movie",
                ((request, response) -> {
                    Movies movie = new Movies();
                    movie.id = films.size() + 1;
                    movie.title = request.queryParams("moviename");
                    movie.genre = request.queryParams("genre");
                    films.add(movie);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.get(
                "/edit-post",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    String id = request.queryParams("id");
                    m.put("id", id);
                    return new ModelAndView(m, "edit.html");
                }),
                new MustacheTemplateEngine()

        );
        Spark.post(
                "/edit-post",
                (request, response) -> {
                    try {
                        String id = request.queryParams("id");
                        int idNum = Integer.valueOf(id);
                        Movies movie = films.get(idNum - 1);
                        movie.title = request.queryParams("editpost");
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                }
        );

        Spark.get(
                "/delete-movie",
                (request, response) -> {
                    String idNum = request.queryParams("id");
                    try{
                        int id = Integer.valueOf(idNum);
                        films.remove(id-1);

                        for (int i = 0; i < films.size(); i++) {
                            films.get(i).id = i + 1;
                        }
                    }catch (Exception e ) {
                    }
                    response.redirect("/");
                    return "";
                });
    }
}
// try to make it so that they only show the posts of the person who is logged in.
// try to get more detailed in general. Genres, etc.
