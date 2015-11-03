package com.company;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users(id IDENTITY, username VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS movies(id IDENTITY, user_id INT, title VARCHAR, genre VARCHAR)");
    }

    public static void insertUser(Connection conn, String username, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.execute();
}

    public static User selectUser (Connection connection, String username) throws SQLException {
        User user = null;
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        if(results.next()){
            user = new User();
            user.id = results.getInt("id");
            user.username = results.getString("username");
            user.password = results.getString("password");
        }
        return user;
}
    public static void insertEntry (Connection connection, int user_id, String title, String genre) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO movies VALUES (NULL, ?, ?, ?,) ");
        stmt.setInt(1,user_id);
        stmt.setString(2, title);
        stmt.setString(3,genre);
        stmt.execute();
    }

    public static Movies selectEntry (Connection connection, int id) throws SQLException {
        Movies movie = null;
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM movies INNER JOIN users ON movies.user_id = users.id WHERE movies.id = ?");
        stmt.setInt(1,id);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            movie = new Movies();
            movie.id = results.getInt("id");
            movie.title = results.getString("title");
            movie.genre = results.getString("genre");
        }
        return movie;
    }

    public static ArrayList<Movies> selectEntries (Connection connection) throws SQLException {
        ArrayList<Movies> movies = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM movies INNER JOIN users ON movies.user_id = users.id");
        ResultSet results = stmt.executeQuery();
        while (results.next()){
            Movies movie = new Movies();
            movie.id = results.getInt("id");
            movie.title = results.getString("title");
            movie.genre = results.getString("genre");
            movies.add(movie);
        }
        return movies;
    }
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);


        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();

                    HashMap m = new HashMap();
                    String username = session.attribute("username");
                    String id = session.attribute("id");
                    String title = session.attribute("title");
                    String genre = session.attribute("genre");

                    m.put("id",id);
                    m.put("title",title);
                    m.put("genre", genre);

                    if (username == null) {
                        return new ModelAndView(m, "not-logged-in.html");
                    } else {
                        m.put("username", username);


                        return new ModelAndView(m,"logged-in.html");
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
                    String password = session.attribute("password");
                    insertUser(conn,username,password);
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
                    movie.title = request.queryParams("moviename");
                    movie.genre = request.queryParams("genre");
                    Session session = request.session();
                    String username = session.attribute("username");
                    User me = selectUser(conn,username);
                    insertEntry(conn,me.id,movie.title,movie.genre);
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
                "/edit-movie",
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

