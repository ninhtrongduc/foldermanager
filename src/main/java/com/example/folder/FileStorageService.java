package com.example.folder;

import java.io.File;

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
}
