package com.example.folder;

import java.nio.file.Paths;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
// Đẩy dữ liệu cây thư mục ra View
public class FileController {

    // @GetMapping("/folder")
    // public String showFolderPage() {
    // // Trả về tên file HTML (không cần đuôi .html) nằm trong folder templates
    // return "foldermanager";
    // }
    @Autowired
    private FileStorageService fileService;

    @GetMapping("/folder")
    public String showManager(Model model) {
        FolderNode root = fileService.getFolderTree();
        model.addAttribute("folders", root.getChildren()); // Lấy các con của thư mục gốc media
        return "foldermanager";
    }

    // --- PHẦN THAY ĐỔI: Thêm endpoint cho HTMX ---
    @GetMapping("/folder/files")
    public String listFiles(@RequestParam String path, Model model) {
        // Lấy danh sách File từ service và chuyển đổi sang FileDTO
        List<File> rawFiles = fileService.getFilesByPath(path);
        List<FileDTO> files = rawFiles.stream().map(f -> {
            String name = f.getName();
            String size = (f.length() / 1024) + " KB";
            String extension = "";
            int i = name.lastIndexOf('.');
            if (i > 0) {
                extension = name.substring(i + 1);
            }
            return new FileDTO(name, size, extension);
        }).collect(Collectors.toList());

        model.addAttribute("files", files);
        model.addAttribute("currentFolder", Paths.get(path).getFileName().toString());

        // Quan trọng: Tên file HTML của bạn là "foldermanager.html" đúng không?
        // Và bên trong đó có th:fragment="file-list-fragment"
        return "foldermanager :: file-list-fragment";
    }
}