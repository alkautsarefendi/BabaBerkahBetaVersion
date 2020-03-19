package org.bawaberkah.app;

public class Product {
    private int id,percentage;
    private String judul, target, terkumpul, path, sinopsis, detail, start, end;


    public Product(int id, String judul, String target, String terkumpul, String path, int percentage
            , String sinopsis, String detail, String start, String end) {
        this.id = id;
        this.judul = judul;
        this.target = target;
        this.terkumpul = terkumpul;
        this.path = path;
        this.percentage = percentage;
        this.sinopsis = sinopsis;
        this.detail = detail;
        this.start = start;
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getTarget() {
        return target;
    }

    public String getTerkumpul() {
        return terkumpul;
    }

    public String getPath() {
        return path;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public String getDetail() {
        return detail;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}