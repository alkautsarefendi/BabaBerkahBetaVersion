package org.bawaberkah.app;

public class Donatur {
    private String IdDonatur, nama, nominal, catatan;

    public Donatur(String idDonatur, String nama, String nominal, String catatan) {
        this.IdDonatur = idDonatur;
        this.nama = nama;
        this.nominal = nominal;
        this.catatan = catatan;
    }

    public String getIdDonatur() {
        return IdDonatur;
    }

    public String getNama() {
        return nama;
    }

    public String getNominal() {
        return nominal;
    }

    public String getCatatan() {
        return catatan;
    }
}
