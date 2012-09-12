package no.jckf.gameboy;

public class Utils {
	public static void println(String str) {
		System.out.println(str);
	}

	public static String hexb(int i) {
		return "0x" + Integer.toHexString(0x100 | i).substring(1).toUpperCase();
	}

	public static String hexw(int i) {
		return "0x" + Integer.toHexString(0x10000 | i).substring(1).toUpperCase();
	}
}
