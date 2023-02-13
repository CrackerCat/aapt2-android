package io.github.rohitverma882.aapt2;

import java.util.List;

public class Aapt2 {
    static {
		System.loadLibrary("aapt2");
	}
	
	public static native boolean compile(List<String> arguments, Aapt2DiagnosticsListener listener);
	
	public static native boolean link(List<String> arguments, Aapt2DiagnosticsListener listener);
	
	public static native boolean dump(List<String> arguments, String outFile, Aapt2DiagnosticsListener listener);
	
	public static native boolean convert(List<String> arguments);
}
