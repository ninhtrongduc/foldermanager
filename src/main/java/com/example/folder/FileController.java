package com.example.folder;

import java.nio.file.Paths;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

    // TẠO FOLDER
    @PostMapping("/folder/create")
    public String createFolder(@RequestParam String folderName, Model model) {
        // 1. Tạo thư mục vật lý
        fileService.createNewFolder(folderName);

        // 2. Lấy lại cây thư mục mới
        FolderNode root = fileService.getFolderTree();

        // 3. Đưa danh sách vào Model với tên 'folders'
        model.addAttribute("folders", root.getChildren());

        // 4. Trả về fragment.
        // Cú pháp nodes=${folders} giúp map danh sách 'folders' từ Model
        // vào tham số 'nodes' của fragment folderTree(nodes)
        return "foldermanager :: folderTree(nodes=${folders})";
    }

    //XÓA FOLDER
    @DeleteMapping("/folder/delete")
    public String deleteFolder(@RequestParam String path, Model model) {
        // 1. Thực hiện xóa thư mục vật lý
        fileService.deleteFolder(path);

        // 2. Lấy lại cây thư mục mới sau khi xóa
        FolderNode root = fileService.getFolderTree();

        // 3. Đưa danh sách mới vào Model [cite: 54]
        model.addAttribute("folders", root.getChildren());

        // 4. SỬA LỖI TẠI ĐÂY: Ánh xạ biến model 'folders' vào tham số 'nodes' của
        // fragment
        // Cú pháp: "tên_file :: tên_fragment(tham_số=${biến_model})"
        return "foldermanager :: folderTree(nodes=${folders})";
    }

    //XÓA FILES CÓ CHECKBOX=true
    @DeleteMapping("/folder/files/delete-selected")
    public String deleteSelectedFiles(
            @RequestParam("selectedFiles") List<String> selectedFiles,
            @RequestParam("path") String path,
            Model model) {

        // 1. Thực hiện xóa danh sách file [cite: 53]
        fileService.deleteFiles(selectedFiles, path);

        // 2. Lấy lại danh sách file mới sau khi xóa để cập nhật UI [cite: 7, 9]
        return listFiles(path, model);
    }

    //UPLOAD
    @PostMapping("/folder/upload")
    public String uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("path") String path,
            Model model) {
        
        // 1. Lưu file
        fileService.saveFiles(files, path);
        
        // 2. Trả về fragment danh sách file để refresh vùng bên phải [cite: 10, 21]
        return listFiles(path, model);
    }
}