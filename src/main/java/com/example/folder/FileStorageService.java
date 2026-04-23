package com.example.folder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {
    // Service này sẽ quét thư mục vật lý và chuyển đổi thành cây đối tượng FolderNode
    @Value("${app.upload.dir}")
    private String uploadDir;

    public FolderNode getFolderTree() {
        File rootFile = new File(uploadDir);
        return buildTree(rootFile, "");
    }

    private FolderNode buildTree(File folder, String parentPath) {
        String currentRelativePath = parentPath.isEmpty() ? folder.getName() : parentPath + "/" + folder.getName();
        
        // Đếm số lượng file trong folder này
        File[] allContents = folder.listFiles();
        int fileCount = 0;
        if (allContents != null) {
            for (File f : allContents) if (f.isFile()) fileCount++;
        }

        FolderNode node = new FolderNode(folder.getName(), currentRelativePath, fileCount);

        // Đệ quy tìm các thư mục con
        if (allContents != null) {
            for (File f : allContents) {
                if (f.isDirectory()) {
                    node.getChildren().add(buildTree(f, currentRelativePath));
                }
            }
        }
        return node;
    }

    // --- THÊM PHẦN NÀY VÀO ---
    public List<File> getFilesByPath(String relativePath) {
        // 1. Loại bỏ phần "media" ở đầu chuỗi nếu có để lấy sub-path chuẩn
        String subPath = relativePath.startsWith("media")
                ? relativePath.substring(5) // "media".length() = 5
                : relativePath;

        // 2. Kết hợp với uploadDir (D:/E-LEARNING/JAVA/QUAN_LY_FILE_CHUAN/media)
        File folder = new File(uploadDir, subPath);

        List<File> fileList = new ArrayList<>();
        // 3. Kiểm tra kỹ sự tồn tại và tính chất thư mục
        if (folder.exists() && folder.isDirectory()) {
            File[] allFiles = folder.listFiles();
            if (allFiles != null) {
                for (File f : allFiles) {
                    if (f.isFile()) {
                        fileList.add(f);
                    }
                }
            }
        } else {
            // Log lỗi ra console để debug nếu không tìm thấy thư mục
            System.out.println("Path not found: " + folder.getAbsolutePath());
        }
        return fileList;
    }
}
