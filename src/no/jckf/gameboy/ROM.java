package no.jckf.gameboy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ROM {
	private static ROM instance;
	private RandomAccessFile rom;

	public ROM(String path) {
		instance = this;

		try {
			rom = new RandomAccessFile(path,"r");
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	public static ROM getInstance() {
		return instance == null ? null : instance;
	}

	public byte read(int offset) {
		try {
			rom.seek(offset);
			return rom.readByte();
		} catch (IOException exception) {
			exception.printStackTrace();
			return 0;
		}
	}

	public byte[] read(int offset,int length) {
		try {
			rom.seek(offset);
			byte[] bytes = new byte[length];
			rom.readFully(bytes,0,length);
			return bytes;
		} catch (IOException exception) {
			exception.printStackTrace();
			return new byte[length];
		}
	}
}
