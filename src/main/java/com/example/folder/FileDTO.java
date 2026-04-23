package com.example.folder;

public class FileDTO {
    private String name;
    private String size;
    private String extension;

    // Constructor không tham số (mặc định cho các framework)
    public FileDTO() {
    }

    // Constructor đầy đủ tham số
    public FileDTO(String name, String size, String extension) {
        this.name = name;
        this.size = size;
        this.extension = extension;
    }

    // Getter và Setter thuần cho Thymeleaf truy cập
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "FileDto{" +
                "name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }
}