package com.example.folder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public void createNewFolder(String folderName) {
        try {
            // Sử dụng uploadDir đã được cấu hình trong Service
            Path root = Paths.get(uploadDir);

            // Tạo đường dẫn tuyệt đối cho thư mục mới bên trong thư mục gốc
            Path newFolderPath = root.resolve(folderName);
            File newDir = newFolderPath.toFile();

            // Kiểm tra nếu thư mục chưa tồn tại thì tiến hành tạo mới
            if (!newDir.exists()) {
                boolean created = newDir.mkdirs();
                if (created) {
                    System.out.println("Đã tạo thư mục thành công tại: " + newDir.getAbsolutePath());
                } else {
                    System.err.println("Không thể tạo thư mục tại: " + newDir.getAbsolutePath());
                }
            } else {
                System.out.println("Thư mục đã tồn tại.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo thư mục mới: " + e.getMessage());
        }
    }

    //XÓA ĐỆ QUY VÌ JAVA KHÔNG CHO PHÉP XÓA FOLDER NẾU CÓ THƯ MỤC CON
    public void deleteFolder(String relativePath) {
        try {
            Path root = Paths.get(uploadDir);
            // Xử lý path tương tự như hàm getFilesByPath của bạn [cite: 74, 75]
            File folderToDelete = root.resolve(relativePath.replace("media/", "")).toFile();

            if (folderToDelete.exists()) {
                deleteRecursively(folderToDelete);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa: " + e.getMessage());
        }
    }

    private void deleteRecursively(File file) {
        File[] allContents = file.listFiles();
        if (allContents != null) {
            for (File f : allContents) {
                deleteRecursively(f);
            }
        }
        file.delete();
    }

    //XÓA FILES CÓ CHECKBOX=true
    public void deleteFiles(List<String> fileNames, String relativePath) {
        try {
            Path root = Paths.get(uploadDir);
            // Xử lý để lấy đường dẫn thư mục hiện tại [cite: 33, 34, 35]
            Path folderPath = root.resolve(relativePath.replace("media/", ""));

            for (String fileName : fileNames) {
                File fileToDelete = folderPath.resolve(fileName).toFile();
                if (fileToDelete.exists() && fileToDelete.isFile()) {
                    fileToDelete.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa files: " + e.getMessage());
        }
    }

    //UPLOAD
    public void saveFiles(List<MultipartFile> files, String relativePath) {
        try {
            Path root = Paths.get(uploadDir);
            // Xử lý logic path đồng nhất với getFilesByPath [cite: 35, 36]
            String rootFolderName = root.getFileName().toString();
            Path targetPath;

            if (relativePath.startsWith(rootFolderName)) {
                String subPathStr = relativePath.substring(rootFolderName.length());
                if (subPathStr.startsWith("/") || subPathStr.startsWith("\\")) {
                    subPathStr = subPathStr.substring(1);
                }
                targetPath = root.resolve(subPathStr);
            } else {
                targetPath = root.resolve(relativePath);
            }

            // Tạo thư mục nếu chưa có
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Path destination = targetPath.resolve(file.getOriginalFilename());
                    // Copy file đè lên nếu đã tồn tại
                    Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Uploaded: " + destination.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Upload: " + e.getMessage());
        }
    }
}