package no.jckf.gameboy;

public class CPU {
	private MMU mmu;
	private int a,f;
	private int b,c;
	private int d,e;
	private int h,l;
	private int sp,pc;

	public CPU() {
		mmu = MMU.getInstance();

		a = 0x01;
		f = 0xB0;
		b = 0;
		c = 0x13;
		d = 0;
		e = 0xD8;
		h = 0x01;
		l = 0x4D;
		sp = 0xFFFE;
		pc = 0x0100;

		mmu.writeByte(0xFF10,0x80);
		mmu.writeByte(0xFF11,0xBF);
		mmu.writeByte(0xFF12,0xF3);
		mmu.writeByte(0xFF14,0xBF);
		mmu.writeByte(0xFF16,0x3F);
		mmu.writeByte(0xFF19,0xBF);
		mmu.writeByte(0xFF1A,0x7F);
		mmu.writeByte(0xFF1B,0xFF);
		mmu.writeByte(0xFF1C,0x9F);
		mmu.writeByte(0xFF1E,0xBF);
		mmu.writeByte(0xFF20,0xFF);
		mmu.writeByte(0xFF23,0xBF);
		mmu.writeByte(0xFF24,0x77);
		mmu.writeByte(0xFF25,0xF3);
		mmu.writeByte(0xFF26,0xF1);
		mmu.writeByte(0xFF40,0x91);
		mmu.writeByte(0xFF42,0x91);
		mmu.writeByte(0xFF43,0x91);
		mmu.writeByte(0xFF45,0x91);
		mmu.writeByte(0xFF47,0xFC);
		mmu.writeByte(0xFF48,0xFF);
		mmu.writeByte(0xFF49,0xFF);

		main:
		while (true) {
			int opcode = mmu.readByte(pc) & 0xFF;
			System.out.println("0x" + Integer.toHexString(pc) + " 0x" + Integer.toHexString(opcode));
			pc++;

			switch (opcode) {
				// NOP = No operation.
				case 0x00:
					break;

				// LD bc d16 = Copy the following 2 bytes into the b and c registers.
				case 0x01:
					c = mmu.readByte(pc);
					b = mmu.readByte(pc + 1);
					pc += 2;
					break;

				// DEC bc = Decrement 16 bit bc register.
				case 0x0B:
					int _bc = bc() - 1;
					bc(_bc);
					if (_bc <= 0) {
						f |= 0x80; // Set the zero flag.
					} else {
						f &= 0xEF; // Unset the zero flag.
					}
					break;

				// JR r8 = Jump relative to current PC as defined by the following byte.
				case 0x18:
					pc += mmu.readByte(pc) + 1;
					break;

				// JR nz r8 = Jump relative to current PC as defined by the following byte if zero flag is set.
				case 0x20:
					if ((f & 0x80) != 0) {
						pc += mmu.readByte(pc);
					}
					pc++;
					break;

				// LD hl d16 = Copy the following two bytes into high and low registers.
				case 0x21:
					l = mmu.readByte(pc);
					h = mmu.readByte(pc + 1);
					pc += 2;
					break;

				// INC hl
				case 0x23:
					hl(hl() + 1);
					break;

				// JR z r8 = Jump relative to current PC as defined by next byte if zero flag is set.
				case 0x28:
					if ((f & 0x80) != 0) {
						pc += mmu.readByte(pc);
					}
					pc++;
					break;

				// LD sp d16 = Copy the following word into the stack pointer.
				case 0x31:
					sp = mmu.readWord(pc);
					pc += 2;
					break;

				// LD (hl) d8 = Copy the following byte into the address defined by the high and low registers.
				case 0x36:
					mmu.writeByte(hl(),mmu.readByte(pc));
					pc++;
					break;

				// LD a d8 = Copy the following byte into the accumulator.
				case 0x3E:
					a = mmu.readByte(pc);
					pc++;
					break;

				// LD b a = Copy the accumulator's value into the b register.
				case 0x47:
					b = a;
					break;

				// LD a b = Copy register b's value into the accumulator.
				case 0x78:
					a = b;
					break;

				// OR c = "Or" register c against the accumulator.
				case 0xB1:
					a |= c;
					if (a <= 0) {
						f = 0x80; // Set the zero flag, unset all others.
					} else {
						f = 0; // Unset the zero flag, and all others.
					}
					break;

				// JP a16 = Jump to absolute address defined in the following word.
				case 0xC3:
					pc = mmu.readWord(pc);
					break;

				// Multi-byte opcode.
				case 0xCB:
					int cbcode = mmu.readByte(pc) & 0xFF;
					System.out.println("0x" + Integer.toHexString(pc) + "      0x" + Integer.toHexString(cbcode));
					pc++;

					switch (cbcode) {
						// RES 0 a = Reset bit 0 of accumulator.
						case 0x87:
							a &= 0xFE;
							break;

						default:
							break main;
					}
					break;

				// CALL a16 = Move down the stack and jump to address defined in the following word.
				case 0xCD:
					// Hack to skip the scrolling Nintendo logo that doesn't scroll.
					// I'm guessing the GPU should be scrolling it, but we don't have a GPU yet!
					if (pc - 1 == 0x1F74) {
						pc += 2;
						break;
					}
					sp -= 2;
					mmu.writeWord(sp,pc + 2);
					pc = mmu.readWord(pc);
					break;

				// LDH (a8) a = Copy accumulator value to IO port defined in the following byte.
				case 0xE0:
					mmu.writeByte(0xFF00 + mmu.readByte(pc),a);
					pc++;
					break;

				// LD a16 a = Copy accumulator value into address defined in the following word.
				case 0xEA:
					mmu.writeByte(mmu.readWord(pc),a);
					pc += 2;
					break;

				// LDH a (a8) = Copy from given IO port into accumulator.
				case 0xF0:
					a = mmu.readByte(0xFF00 + mmu.readByte(pc));
					pc++;
					break;

				// DI = Disable interrupts.
				case 0xF3:
					break;

				// CP d8 = Compare the following byte to accumulator.
				case 0xFE:
					int b = mmu.readByte(pc) & 0xFF;
					System.out.println(b + " =? " + a);
					if (b == a) {
						f |= 0x80; // Set zero flag.
					} else {
						f &= 0xEF; // Unset zero flag.
					}
					f |= 0x40; // Ser operation flag.
					pc++;
					break;

				// XOR a = "Exclusive or" accumulator against itself, effectively zeroing it out.
				case 0xAF:
					a = 0;
					f |= 0x80; // Ser zero flag.
					break;

				// Da fuq?
				default:
					break main;
			}
		}
	}

	private int hl() {
		return ((l & 0xFF) << 8) | (h & 0xFF);
	}

	private void hl(int data) {
		l = data >>> 8;
		h = data & 0xFF;
	}

	private int bc() {
		return ((c & 0xFF) << 8) | (b & 0xFF);
	}

	private void bc(int data) {
		c = data >>> 8;
		b = data & 0xFF;
	}
}
