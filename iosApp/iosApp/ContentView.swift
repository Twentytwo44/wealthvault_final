import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    let lineAuth: SwiftLineAuth

    func makeUIViewController(context: Context) -> UIViewController {
        // เรียกฟังก์ชันจาก Kotlin พร้อมส่ง LineLoginHelper เข้าไป
        return MainViewControllerKt.MainViewController(lineAuth: lineAuth)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    // รับตัวแปรมาจาก iOSApp.swift
    let lineAuth: SwiftLineAuth

    var body: some View {
        ComposeView(lineAuth: lineAuth)
            .ignoresSafeArea()
    }
}

