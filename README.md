# BTl Android

## Project Structure

- Module `fcm`: Notification
- Module `firestore`: Logic about Firebase Firestore
- Module `local`: Logic about Room Database
- Module `presentation`: UI & ViewModel
- Module `remote`: Logic to pull data from another API
- Module `repository`: Remote + local data
- Module `utils`: Contains mapper,...
- `auto_test`: Test automation script written in Katalon Studio


## Step-by-step guide to creating automated test scripts in Katalon

1. Kiểm tra đã cài Node.js chưa
    - node -v
    - npm -v

2. Nếu chưa cài đặt Node.js tại https://nodejs.org/en

3. Cài đặt Appium
    - npm install -g appium
    - appium --version
    - where appium

4. Tải và cài đặt Katalon Studio tại https://katalon.com/

5. Kết nối Katalon Studio với Appium
    - Window -> Katalon Studio Preferences -> Katalon -> Mobile
    - Thay đường dẫn Appium bằng đường dẫn đã cài ở bước 3

6. Thiết lập android mobile (bật USB Debugging)
    - Trong bài tập lớn này sử dụng máy ảo trên Android Studio
    - Settings -> About Phone -> bấm 7 lần vào Build number
    - Settings -> Developer Options -> bật USB Debugging

7. Nếu cần cài thêm build-tools cho katalon
    - Tải phiên bản build-tools cần thiết tại https://developer.android.com/tools/releases/build-tools
    - Giải nén và đặt vào trong thư mục: .katalon\tools\android_sdk\build-tools

8. Build project thành file .apk
    - Trong Android Studio: Build > Build Bundle(s) / APK(s) > Build APK(s)
    - Lưu lại file .apk được tạo ra

9. Tạo project trong Katalon Studio 
    - Mở Katalon Studio → File > New > Project → chọn Mobile
    - Đặt tên project, chọn thư mục lưu

10. Ghi thao tác
    - Nhấn "Record Mobile"
    - Chọn thiết bị và file APK
    - Katalon sẽ khởi chạy app và mở giao diện record UI
    - Thực hiện các thao tác trên thiết bị
    -  Katalon sẽ tự ghi lại các bước đó thành test script.

11. Chạy và kiểm tra kết quả
    - Nhấn nút "Run" để chạy lại các thao tác đã record
    - Katalon sẽ mô phỏng đúng các hành vi như người dùng
    - Test một lúc nhiều chức năng có thể sử dụng Test Suites
