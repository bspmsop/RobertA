package com.app.raassoc;

/**
 * Created by New android on 19-01-2019.
 */

public class ListDoc {
    private int buildImg;
    private String file, filename;
    private String date;
    private  String filePath;
    public ListDoc(int buildImg, String file, String filename,String date, String path) {
        this.buildImg = buildImg;
        this.file = file;
        this.filename = filename;
        this.date   = date;
        this.filePath = path;
    }

    public int getBuildImg(){
        return buildImg;
    }

    public String getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }
    public String getDate(){ return date;}
    public  String getPath(){return  filePath;}
}
