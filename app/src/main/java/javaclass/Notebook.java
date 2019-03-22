package javaclass;

public class Notebook {
    private String name;
    private int imageId;

    public Notebook(String name, int imageId){
        this.name = name;
        this.imageId = imageId;
    }

    public String getName(){
        return this.name;
    }

    public int getImageId(){
        return this.imageId;
    }
}
