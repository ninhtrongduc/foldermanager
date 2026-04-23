package com.example.folder;

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
    //     // Trả về tên file HTML (không cần đuôi .html) nằm trong folder templates
    //     return "foldermanager";
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
        var files = fileService.getFilesByPath(path);

        // Xử lý lấy tên folder hiển thị an toàn
        String folderName = "Unknown";
        if (path != null) {
            int lastSlash = path.lastIndexOf("/");
            folderName = (lastSlash != -1) ? path.substring(lastSlash + 1) : path;
        }

        model.addAttribute("currentFolder", folderName);
        model.addAttribute("files", files);

        return "foldermanager :: file-list-fragment";
    }
}