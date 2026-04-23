package com.example.folder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public FolderNode getFolderTree() {
        File rootFile = new File(uploadDir);
        // Bắt đầu build tree từ thư mục gốc, truyền chuỗi rỗng làm path cha
        return buildTree(rootFile, "");
    }

    private FolderNode buildTree(File folder, String parentPath) {
        // Fix: Đảm bảo relative path không bị lặp chữ "media"
        String currentRelativePath = parentPath.isEmpty() ? folder.getName() : parentPath + "/" + folder.getName();

        File[] allContents = folder.listFiles();
        int fileCount = 0;
        if (allContents != null) {
            for (File f : allContents)
                if (f.isFile())
                    fileCount++;
        }

        FolderNode node = new FolderNode(folder.getName(), currentRelativePath, fileCount);

        if (allContents != null) {
            for (File f : allContents) {
                if (f.isDirectory()) {
                    node.getChildren().add(buildTree(f, currentRelativePath));
                }
            }
        }
        return node;
    }

    public List<File> getFilesByPath(String relativePath) {
        List<File> fileList = new ArrayList<>();

        try {
            // Lấy Path gốc (media)
            Path root = Paths.get(uploadDir);
            String rootFolderName = root.getFileName().toString(); // "media"

            Path fullPath;
            // Nếu relativePath bắt đầu bằng tên thư mục gốc, ta cần xử lý để tránh nối
            // chồng
            if (relativePath.startsWith(rootFolderName)) {
                // Lấy phần còn lại sau "media"
                String subPathStr = relativePath.substring(rootFolderName.length());
                // Xóa dấu / ở đầu nếu có để Paths.get không hiểu lầm là đường dẫn gốc hệ thống
                if (subPathStr.startsWith("/") || subPathStr.startsWith("\\")) {
                    subPathStr = subPathStr.substring(1);
                }
                fullPath = root.resolve(subPathStr);
            } else {
                fullPath = root.resolve(relativePath);
            }

            File folder = fullPath.toFile();

            System.out.println("--- Debug Path ---");
            System.out.println("Input RelativePath: " + relativePath);
            System.out.println("Full Physical Path: " + folder.getAbsolutePath());

            if (folder.exists() && folder.isDirectory()) {
                File[] allFiles = folder.listFiles();
                if (allFiles != null) {
                    for (File f : allFiles) {
                        if (f.isFile())
                            fileList.add(f);
                    }
                }
            } else {
                System.out.println("Lỗi: Thư mục không tồn tại hoặc không phải thư mục!");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi quét file: " + e.getMessage());
        }

        return fileList;
    }
}