# Saayinsii Waliigalaa Kutaa 8ffaa (Grade 8 General Science)

[![Android Version](https://img.shields.io/badge/Android-7.0%2B-green.svg)](https://developer.android.com)
[![API Level](https://img.shields.io/badge/API-24%2B-blue.svg)](https://developer.android.com/distribute/best-practices/develop/target-sdk)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Version](https://img.shields.io/badge/Version-1.7.2-orange.svg)](https://play.google.com/store/apps/details?id=com.beckytech.saayinsiiwaliigalaakutaa8ffaa)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Saayinsii Waliigalaa Kutaa 8ffaa** is a high-performance, feature-rich educational platform tailored for Grade 8 students. It provides a seamless digital reading experience for the General Science curriculum, combining advanced PDF rendering technology with a modern, distraction-free UI.

---

## 🚀 Key Features

- **📚 Advanced PDF Engine**: High-fidelity native rendering with smooth multi-touch zoom and low memory footprint.
- **🔄 Seamless Navigation**: Chapter-based `ViewPager2` implementation for fluid transitions between curriculum sections.
- **🛡️ Ad-Free Focus Mode**: Integrated rewarded ad mechanism allowing students to "earn" ad-free study sessions.
- **⚡ 16KB Page Size Support**: Optimized for the latest Android performance standards and device architectures.
- **📡 Multi-Network Ad Stack**: Enterprise-grade ad mediation involving Facebook (Meta), Vungle (Liftoff), and Unity.
- **📈 Analytics & Reliability**: Real-time crash monitoring and user behavior analytics via Firebase.

## 🛠️ Tech Stack

- **Language**: Java / Android SDK
- **UI Framework**: AndroidX, Material Design 3
- **PDF Rendering**: `android.graphics.pdf.PdfRenderer` (Native)
- **Ad Networks**: Meta Audience Network, Vungle/Liftoff, Unity Ads
- **Backend Services**: Firebase Analytics, Crashlytics, Cloud Messaging
- **Lifecycle**: Play Core (In-App Updates & Reviews)

## 🏗️ Architecture

The project follows a clean, modular approach focused on performance and maintainability:
- **`utils/AdManager`**: Centralized ad orchestration with sophisticated fallback logic.
- **`activity/BookDetailActivity`**: Decoupled UI logic using `ViewPager2` and Fragment-based rendering.
- **Performance Optimization**: R8/ProGuard rules tailored for minimizing binary size while preserving critical networking and security classes.

## 📥 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17
- Android SDK 37 (Compile/Target)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/beresaabebe/Saayinsii-Waliigalaa-Kutaa-8ffaa.git
   ```
2. Open in Android Studio.
3. Sync Project with Gradle Files.
4. Add your `google-services.json` to the `app/` directory.

## 📄 Documentation

- [Changelog](CHANGELOG.md): Detailed history of releases and changes.
- [ProGuard Config](app/proguard-rules.pro): Security and shrinking rules.

## 🤝 Contributing

Contributions are welcome! Please follow these steps:
1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## 📧 Contact

**BeckyTech** - [beckytech@gmail.com](mailto:beckytech@gmail.com)  
Project Link: [https://github.com/beresaabebe/Saayinsii-Waliigalaa-Kutaa-8ffaa](https://github.com/beresaabebe/Saayinsii-Waliigalaa-Kutaa-8ffaa)

---
*Developed with ❤️ for students in Ethiopia.*
