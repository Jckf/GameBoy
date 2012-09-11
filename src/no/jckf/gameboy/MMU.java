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
	}

	public static MMU getInstance() {
		return instance == null ? null : instance;
	}

	public byte[] readBytes(int offset,int length) {
		if (offset < 0x8000) {
			return rom.read(offset,length);
		} else {
			if (length == 1) {
				return new byte[]{memory[offset]};
			} else {
				return Arrays.copyOfRange(memory,offset,offset + length);
			}
		}
	}

	public byte readByte(int offset) {
		// Not sure if this nesting is all that good,
		// but I'd rather not duplicate the offset checks.
		return readBytes(offset,1)[0];
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
		byte[] b = readBytes(offset,2);
		return ((b[1] & 0xFF) << 8) | (b[0] & 0xFF);
	}

	public void writeWord(int offset,int data) {
		writeBytes(offset,new byte[]{
			(byte)(data >> 8),
			(byte)(data & 0xFF)
		});
	}
}
