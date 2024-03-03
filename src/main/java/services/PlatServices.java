package services;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Category;
import models.Gerant;
import models.Plat;
import utils.MyDatabase;
import utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatServices  {

    private final Connection connection;

    public PlatServices() {
        this.connection = MyDatabase.getInstance().getConnection();
    }




    public void ajouter(Plat plat) throws SQLException {
        String req = "INSERT INTO plat (nom, description, prix, image, id_category, id_restaurant) VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(req);
        pst.setString(1, plat.getNom());
        pst.setString(2, plat.getDescription());
        pst.setFloat(3, plat.getPrix());
        pst.setString(4, plat.getImage());
        pst.setInt(5, plat.getId_category()); // Directly using id_category from Plat object

        // Getting the current logged-in Gerant's ID for the id_restaurant field
        Gerant currentGerant = SessionManager.getCurrentGerant();
        if (currentGerant != null) {
            pst.setInt(6, currentGerant.getId()); // Adjusted to match Gerant class structure
        } else {
            throw new SQLException("No Gerant logged in.");
        }

        pst.executeUpdate();
    }

    public List<Category> fetchCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String req = "SELECT * FROM category";
        PreparedStatement pst = connection.prepareStatement(req);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            categories.add(new Category(rs.getInt("id"), rs.getString("type")));
        }
        return categories;
    }


    public List<Plat> afficher() throws SQLException {
        List<Plat> plats = new ArrayList<>();
        String req = "SELECT p.*, c.type as categoryType FROM plat p INNER JOIN category c ON p.id_category = c.id";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            plats.add(new Plat(rs.getInt("id_plat"), rs.getString("nom"), rs.getInt("id_category"),
                    rs.getInt("id_restaurant"), rs.getString("description"), rs.getFloat("prix"), rs.getString("image")));
        }
        return plats;
    }



    public void supprimer(int id_plat) throws SQLException {
        String req = "DELETE FROM `plat` WHERE id_plat = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1,id_plat);
        preparedStatement.executeUpdate();
    }



    public List<Gerant> fetchGerants() throws SQLException {
        List<Gerant> gerants = new ArrayList<>();
        String sql = "SELECT * FROM gerant"; // Replace with your actual gerant table name
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Gerant gerant = new Gerant(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("document"),
                        rs.getString("image"),
                        rs.getString("email"),    // Additional email parameter
                        rs.getString("password"), // Additional password parameter
                        rs.getTimestamp("date")   // Additional date parameter
                );
                gerants.add(gerant);
            }
        }
        return gerants;
    }


    public List<Plat> fetchPlatsByGerant(int gerantId) throws SQLException {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT * FROM plat WHERE id_restaurant = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, gerantId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Plat plat = new Plat(
                            rs.getInt("id_plat"),
                            rs.getInt("id_category"),
                            rs.getInt("id_restaurant"),
                            rs.getString("nom"),
                            rs.getString("image"),
                            rs.getString("description"),
                            rs.getFloat("prix")
                    );
                    plats.add(plat);
                }
            }
        }
        return plats;
    }
    public List<Plat> fetchPlatssByGerant(int gerantId) throws SQLException {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT p.*, c.type as categoryName, r.name as restaurantName " +
                "FROM plat p " +
                "INNER JOIN category c ON p.id_category = c.id " +
                "INNER JOIN gerant r ON p.id_restaurant = r.id " +
                "WHERE p.id_restaurant = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, gerantId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Plat plat = new Plat(
                            rs.getInt("id_plat"),
                            rs.getString("nom"),
                            rs.getString("categoryName"),
                            rs.getString("restaurantName"),
                            rs.getString("description"),
                            rs.getFloat("prix"),
                            rs.getString("image")
                    );
                    plats.add(plat);
                }
            }
        }
        return plats;
    }

}
