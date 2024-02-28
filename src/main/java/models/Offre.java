package models;

import java.util.Date;

public class Offre {
    private int id, id_resto, id_plat;
    private int pourcentage;
    private Date date_debut;
    private Date date_fin;
    private float new_price;

    public float getNew_price() {
        return new_price;
    }

    public void setNew_price(float new_price) {
        this.new_price = new_price;
    }



    public Offre() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_resto() {
        return id_resto;
    }

    public void setId_resto(int id_resto) {
        this.id_resto = id_resto;
    }

    public int getId_plat() {
        return id_plat;
    }

    public void setId_plat(int id_plat) {
        this.id_plat = id_plat;
    }

    public int getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(int percentage) {
        this.pourcentage = percentage;
    }



    public Date getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(Date date_debut) {
        this.date_debut = date_debut;
    }

    public Date getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
    }

    @Override
    public String toString() {
        return "Offre{" +
                "id=" + id +
                ", id_resto=" + id_resto +
                ", id_plat=" + id_plat +
                ", percentage=" + pourcentage +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                '}';
    }
    private String platName ;
    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }
    public float getOriginalPrice() {
        return new_price / (1 - (pourcentage / 100.0f));
    }
    public Offre(int id, double pourcentage, Date dateDebut, Date dateFin, int idPlat, double newPrice , String platName) {
        this.id = id;
        this.pourcentage = (int) pourcentage;
        this.date_debut = dateDebut;
        this.date_fin = dateFin;
        this.id_plat = idPlat;
        this.new_price = (float) newPrice;
        this.platName = platName;
    }


}
