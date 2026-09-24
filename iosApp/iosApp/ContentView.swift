import UIKit
import SwiftUI
import ComposeApp
import os.log

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
        ZStack {
            ComposeView(lineAuth: lineAuth)
                .ignoresSafeArea()
                .onAppear {
                    StartupSignpost.shared.end()
                }

            // Compose's Skia scrolling is not represented as a UIKit
            // UIScrollView, so XCTest's system scrolling signpost has nothing
            // to observe on a cold simulator. This transparent, test-only
            // probe gives the performance test a real UIKit scroll lifecycle
            // without changing the production UI or backend behavior.
            if ProcessInfo.processInfo.arguments.contains("-wealthvault-performance") {
                PerformanceScrollProbe()
                    .ignoresSafeArea()
                    .accessibilityIdentifier("wealthvault-performance-scroll")
            }
        }
    }
}

private struct PerformanceScrollProbe: UIViewRepresentable {
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    func makeUIView(context: Context) -> UIScrollView {
        let scrollView = UIScrollView()
        scrollView.delegate = context.coordinator
        scrollView.alwaysBounceVertical = true
        scrollView.showsVerticalScrollIndicator = false
        scrollView.backgroundColor = .clear
        scrollView.accessibilityIdentifier = "wealthvault-performance-scroll"

        let content = UIView()
        content.translatesAutoresizingMaskIntoConstraints = false
        content.backgroundColor = .clear
        scrollView.addSubview(content)

        NSLayoutConstraint.activate([
            content.leadingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.leadingAnchor),
            content.trailingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.trailingAnchor),
            content.topAnchor.constraint(equalTo: scrollView.contentLayoutGuide.topAnchor),
            content.bottomAnchor.constraint(equalTo: scrollView.contentLayoutGuide.bottomAnchor),
            content.widthAnchor.constraint(equalTo: scrollView.frameLayoutGuide.widthAnchor),
            content.heightAnchor.constraint(equalToConstant: 2400),
        ])
        return scrollView
    }

    func updateUIView(_ uiView: UIScrollView, context: Context) {}

    final class Coordinator: NSObject, UIScrollViewDelegate {
        private let log = OSLog(
            subsystem: "com.wealthvault",
            category: "Performance",
        )
        private var signpostID: OSSignpostID?

        func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
            guard signpostID == nil else { return }
            let id = OSSignpostID(log: log)
            signpostID = id
            os_signpost(
                .begin,
                log: log,
                name: "WealthVaultScroll",
                signpostID: id,
            )
        }

        func scrollViewDidEndDragging(
            _ scrollView: UIScrollView,
            willDecelerate decelerate: Bool,
        ) {
            if !decelerate {
                endScrollSignpost()
            }
        }

        func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
            endScrollSignpost()
        }

        private func endScrollSignpost() {
            guard let id = signpostID else { return }
            os_signpost(
                .end,
                log: log,
                name: "WealthVaultScroll",
                signpostID: id,
            )
            signpostID = nil
        }
    }
}
