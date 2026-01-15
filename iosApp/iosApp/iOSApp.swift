import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        PlatformModule_iosKt.doInitKoin()
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}