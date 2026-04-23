package com.example.folder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}