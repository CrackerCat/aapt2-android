package io.github.rohitverma882.aapt2;

public interface Aapt2DiagnosticsListener {
	void onLog(int level, String path, long line, String messags);
}
