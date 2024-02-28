package services;

import models.Offre;
import utils.MyDatabase;
import utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreServices {
    private final Connection connection;

    public OffreServices() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public void createOffer(int idPlat, double pourcentage, double newPrice, Date startDate, Date endDate, int idResto) throws SQLException {
        String sql = "INSERT INTO offre_resto (id_plat, pourcentage, new_price, date_debut, date_fin, id_resto) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idPlat);
            preparedStatement.setDouble(2, pourcentage);
            preparedStatement.setFloat(3, (float) newPrice); // Cast double to float
            preparedStatement.setDate(4, startDate);
            preparedStatement.setDate(5, endDate);
            preparedStatement.setInt(6, idResto); // Add the id_resto (id of gerant) to the PreparedStatement

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating offer failed, no rows affected.");
            }
            System.out.println("Offer added successfully.");
        }
    }
    public List<Offre> getOffersForGerant(int idResto) {
        List<Offre> offers = new ArrayList<>();
        String sql = "SELECT offre_resto.*, plat.nom AS plat_name FROM offre_resto JOIN plat ON offre_resto.id_plat = plat.id_plat WHERE offre_resto.id_resto = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idResto);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    double pourcentage = resultSet.getDouble("pourcentage");
                    Date dateDebut = resultSet.getDate("date_debut");
                    Date dateFin = resultSet.getDate("date_fin");
                    int idPlat = resultSet.getInt("id_plat");
                    double newPrice = resultSet.getDouble("new_price");
                    String platName = resultSet.getString("plat_name");


                    Offre offer = new Offre(id, pourcentage, dateDebut, dateFin, idPlat, newPrice, platName);
                    offers.add(offer);

                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return offers;
    }

    public void deleteOffer(int offerId) throws SQLException {
        String sql = "DELETE FROM offre_resto WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, offerId);
            preparedStatement.executeUpdate();
        }
    }


    public void updateOffer(Offre offer) throws SQLException {
        String sql = "UPDATE offre_resto SET pourcentage = ?, new_price = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, offer.getPourcentage());
            preparedStatement.setFloat(2, offer.getNew_price());
            preparedStatement.setInt(3, offer.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update offer. No rows affected.");
            }
            System.out.println("Offer updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating offer: " + e.getMessage());
            throw e; // Re-throw the exception to handle it at the calling site
        }
    }




}

