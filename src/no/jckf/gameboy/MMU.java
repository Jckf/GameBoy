package no.jckf.gameboy;

import java.util.Arrays;

public class MMU {
	private static MMU instance;
	private byte[] memory;
	private ROM rom;

	public MMU() {
		instance = this;

		memory = new byte[0xFFFF];
		rom = ROM.getInstance();

		// This can probably be done better.
		// Perhaps move ROM handling into the MMU?
		for (int i = 0x0000; i < 0x8000; i++) {
			memory[i] = rom.read(i);
		}
	}

	public static MMU getInstance() {
		return instance == null ? null : instance;
	}

	public byte readByte(int offset) {
		return memory[offset];
	}

	public void writeByte(int offset,byte data) {
		memory[offset] = data;
	}

	public void writeByte(int offset,int data) {
		writeByte(offset,new Integer(data).byteValue());
	}

	public void writeBytes(int offset,byte[] data) {
		for (byte b : data) {
			writeByte(offset,b);
			offset++;
		}
	}

	public int readWord(int offset) {
		return ((readByte(offset + 1) & 0xFF) << 8) | (readByte(offset) & 0xFF);
	}

	public void writeWord(int offset,int data) {
		writeBytes(offset,new byte[]{
			(byte)(data >> 8),
			(byte)(data & 0xFF)
		});
	}
}
