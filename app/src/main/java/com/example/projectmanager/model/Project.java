package com.example.projectmanager.model;

/**
 * Representa un proyecto.
 */
public class Project {
    private String id;
    private String name;
    private String desc;
    private String img;

    public Project(String id, String name, String desc, String img) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return this.img;
    }
}
