function validateFolderName() {
    const input = document.getElementById('newFolderName');
    if (!input.value.trim()) {
        alert("Bạn chưa nhập tên thư mục");
        return false;
    }
    return true;
}

let selectedPath = "";

function handleFolderClick(element) {
    // UI Active [cite: 45]
    document.querySelectorAll('.nav-row').forEach(el => el.classList.remove('active'));
    element.classList.add('active');

    // Lấy path từ thuộc tính hx-get (bạn cần substring để lấy tham số path) [cite: 42]
    const url = new URL(element.getAttribute('hx-get'), window.location.origin);
    selectedPath = url.searchParams.get('path');

    // Hiện nút xóa
    document.getElementById('btn-delete-folder').style.display = 'flex';

    // Đóng mở thư mục [cite: 47]
    const parentItem = element.closest('.nav-item');
    if (parentItem) parentItem.classList.toggle('expanded');
}

function getSelectedFolderPath() {
    return selectedPath;
}

// Reset sau khi HTMX xóa thành công
document.body.addEventListener('htmx:afterOnRequest', function (evt) {
    if (evt.detail.pathInfo.requestPath === "/folder/delete") {
        selectedPath = "";
        document.getElementById('btn-delete-folder').style.display = 'none';
        // Xóa nội dung hiển thị file bên phải vì thư mục không còn tồn tại
        document.getElementById('file-display-area').innerHTML = '';
    }
});

document.body.addEventListener('htmx:afterOnRequest', function (evt) {
    if (evt.detail.pathInfo.requestPath === "/folder/create") {
        document.getElementById('newFolderName').value = '';
    }
});