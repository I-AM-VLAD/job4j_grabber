package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private static Connection connection;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("jdbc.driver"));
            DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     connection.prepareStatement("insert into post (name, description, link, created) values (?, ?, ?, ?) ON CONFLICT (link) DO NOTHING",
                             Statement.RETURN_GENERATED_KEYS)) {
            Timestamp timestamp = Timestamp.valueOf(post.getCreated());
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, timestamp);
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(objectPost(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    public Post objectPost(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("link"),
                resultSet.getString("description"),
                resultSet.getTimestamp("created").toLocalDateTime()
        );
    }

    @Override
    public Post findById(int id) {
        Post result = null;
        try (PreparedStatement statement = connection.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = objectPost(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("src/main/resources/db.properties")) {
            Properties config = new Properties();
            config.load(in);
            PsqlStore psqlStore = new PsqlStore(config);
            HabrCareerDateTimeParser dateTime = new HabrCareerDateTimeParser();
            psqlStore.save(new Post(1, "name", "link", "description", dateTime.parse("2018-05-05T11:50:55+03:00")));
            psqlStore.save(new Post(2, "name1", "link1", "description1", dateTime.parse("2019-05-05T11:50:55+03:00")));
            List<Post> posts = new ArrayList<>();
            posts = psqlStore.getAll();
            for (Post post : posts) {
                System.out.println(post);
            }
            System.out.println(psqlStore.findById(2));

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
